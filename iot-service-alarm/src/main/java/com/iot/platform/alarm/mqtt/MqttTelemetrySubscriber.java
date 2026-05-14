package com.iot.platform.alarm.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.alarm.domain.AlarmRule;
import com.iot.platform.alarm.repo.AlarmRuleRepository;
import com.iot.platform.alarm.service.AlarmRuleEvaluator;
import com.iot.platform.common.util.EmptyUtil;
import com.iot.platform.starter.redis.IotRedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * 订阅 MQTT 主题，将点位值写入 Redis，再按遥测键评估绑定的报警规则。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "iot.mqtt", name = "enabled", havingValue = "true")
public class MqttTelemetrySubscriber {

    private final MqttProperties mqttProperties;
    private final ObjectMapper objectMapper;
    private final IotRedisHelper redisHelper;
    private final TelemetryRedisKeyBuilder keyBuilder;
    private final AlarmRuleRepository ruleRepository;
    private final AlarmRuleEvaluator alarmRuleEvaluator;

    private MqttClient client;

    @PostConstruct
    public void start() throws MqttException {
        String clientId = mqttProperties.getClientId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        client = new MqttClient(mqttProperties.getServerUri(), clientId);
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setAutomaticReconnect(true);
        opts.setCleanSession(true);
        if (!EmptyUtil.isBlank(mqttProperties.getUsername())) {
            opts.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null) {
            opts.setPassword(mqttProperties.getPassword().toCharArray());
        }
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverUri) {
                try {
                    int qos = Math.max(0, Math.min(2, mqttProperties.getQos()));
                    client.subscribe(mqttProperties.getTopic(), qos);
                    log.info("MQTT subscribed topic={} qos={}", mqttProperties.getTopic(), qos);
                } catch (MqttException e) {
                    log.error("MQTT subscribe failed", e);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                log.warn("MQTT connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    onMessage(topic, message);
                } catch (Exception e) {
                    log.error("MQTT message handling failed topic={}", topic, e);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        client.connect(opts);
        log.info("MQTT connected uri={}", mqttProperties.getServerUri());
    }

    private void onMessage(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        MqttTelemetryMessage msg = objectMapper.readValue(payload, MqttTelemetryMessage.class);
        String valueStr = msg.getValue() == null ? "" : String.valueOf(msg.getValue());
        String redisKey = keyBuilder.build(msg.getDeviceId(), msg.getDeviceTypeId(), msg.getPointCode(), msg.getExternalPointId());
        if (EmptyUtil.isBlank(redisKey)) {
            log.warn("MQTT telemetry skipped: empty redis key topic={} payload={}", topic, payload);
            return;
        }
        redisHelper.set(redisKey, valueStr);
        List<AlarmRule> rules = ruleRepository.findByEnabledTrueAndTelemetryRedisKey(redisKey);
        for (AlarmRule rule : rules) {
            alarmRuleEvaluator.evaluate(rule);
        }
    }

    @PreDestroy
    public void stop() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException ignored) {
            }
        }
    }
}
