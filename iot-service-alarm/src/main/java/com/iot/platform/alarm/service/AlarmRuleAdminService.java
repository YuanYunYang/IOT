package com.iot.platform.alarm.service;

import com.iot.platform.alarm.domain.AlarmRule;
import com.iot.platform.alarm.dto.AlarmRuleRequest;
import com.iot.platform.alarm.dto.DevicePointMeta;
import com.iot.platform.alarm.dto.DeviceTypeMeta;
import com.iot.platform.alarm.repo.AlarmRuleRepository;
import com.iot.platform.common.util.EmptyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 报警规则 CRUD：保存时可根据点位 ID 从 Redis 补全编码/名称，并按模板生成遥测 Redis 键（telemetryRedisKey）。
 */
@Service
@RequiredArgsConstructor
public class AlarmRuleAdminService {

    private final AlarmRuleRepository ruleRepository;
    private final DeviceMetaRedisReader deviceMetaRedisReader;
    private final com.iot.platform.alarm.config.IotAlarmProperties alarmProperties;

    @Transactional(readOnly = true)
    public List<AlarmRule> listAll() {
        return ruleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AlarmRule get(Long id) {
        return ruleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("规则不存在"));
    }

    @Transactional
    public AlarmRule create(AlarmRuleRequest req) {
        AlarmRule r = map(new AlarmRule(), req);
        return ruleRepository.save(r);
    }

    @Transactional
    public AlarmRule update(Long id, AlarmRuleRequest req) {
        AlarmRule r = ruleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("规则不存在"));
        map(r, req);
        r.setUpdatedAt(Instant.now());
        return ruleRepository.save(r);
    }

    @Transactional
    public void delete(Long id) {
        ruleRepository.deleteById(id);
    }

    private AlarmRule map(AlarmRule r, AlarmRuleRequest req) {
        r.setName(req.getName());
        r.setEnabled(req.getEnabled());
        r.setSubsystem(req.getSubsystem());
        r.setDeviceTypeId(req.getDeviceTypeId());
        r.setDeviceTypeName(req.getDeviceTypeName());
        r.setDeviceId(req.getDeviceId());
        r.setDeviceName(req.getDeviceName());
        r.setPointCode(req.getPointCode());
        r.setPointId(req.getPointId());
        r.setPointName(req.getPointName());
        r.setCompareOperator(req.getCompareOperator());
        r.setThresholdValue(req.getThresholdValue());
        r.setDurationSeconds(req.getDurationSeconds());
        r.setTelemetryRedisKey(req.getTelemetryRedisKey());
        r.setAlarmTitle(req.getAlarmTitle());
        r.setAlarmLevel(req.getAlarmLevel());

        fillFromPointMetaIfNeeded(r);
        fillTelemetryKeyIfNeeded(r);
        return r;
    }

    /** 若选了点位 ID，则从 device 服务写入 Redis 的元数据补齐 pointCode、deviceType 等 */
    private void fillFromPointMetaIfNeeded(AlarmRule r) {
        if (r.getPointId() == null) {
            return;
        }
        Optional<DevicePointMeta> pOpt = deviceMetaRedisReader.getPoint(r.getPointId());
        if (!pOpt.isPresent()) {
            return;
        }
        DevicePointMeta p = pOpt.get();
        if (EmptyUtil.isBlank(r.getPointCode())) {
            r.setPointCode(p.getPointCode());
        }
        if (EmptyUtil.isBlank(r.getPointName())) {
            r.setPointName(p.getPointName());
        }
        if (r.getDeviceTypeId() == null) {
            r.setDeviceTypeId(p.getDeviceTypeId());
        }
        if (EmptyUtil.isBlank(r.getDeviceTypeName()) && p.getDeviceTypeId() != null) {
            Optional<DeviceTypeMeta> tOpt = deviceMetaRedisReader.getType(p.getDeviceTypeId());
            if (tOpt.isPresent()) {
                r.setDeviceTypeName(tOpt.get().getTypeName());
                if (EmptyUtil.isBlank(r.getSubsystem())) {
                    r.setSubsystem(tOpt.get().getSubsystem());
                }
            }
        }
    }

    /**
     * 未手写 Redis key 时，用配置项 IotAlarmProperties 中的遥测键模板 telemetryKeyTemplate
     * 拼接出采集侧写入的遥测 key，供评估任务读取当前值。
     */
    private void fillTelemetryKeyIfNeeded(AlarmRule r) {
        if (!EmptyUtil.isBlank(r.getTelemetryRedisKey())) {
            return;
        }
        String tpl = alarmProperties.getTelemetryKeyTemplate();
        if (EmptyUtil.isBlank(tpl)) {
            tpl = "telemetry:{deviceId}:{pointCode}";
        }
        String deviceId = r.getDeviceId() == null ? "" : String.valueOf(r.getDeviceId());
        String deviceTypeId = r.getDeviceTypeId() == null ? "" : String.valueOf(r.getDeviceTypeId());
        String pointCode = EmptyUtil.isBlank(r.getPointCode()) ? "" : r.getPointCode();
        String externalPointId = "";
        if (r.getPointId() != null) {
            externalPointId = deviceMetaRedisReader.getPoint(r.getPointId())
                    .map(DevicePointMeta::getExternalPointId)
                    .orElse("");
            if (externalPointId == null) {
                externalPointId = "";
            }
        }
        String key = tpl
                .replace("{deviceId}", deviceId)
                .replace("{deviceTypeId}", deviceTypeId)
                .replace("{pointCode}", pointCode)
                .replace("{externalPointId}", externalPointId);
        r.setTelemetryRedisKey(key);
    }
}
