package com.iot.platform.aiot.kb.milvus;

import com.iot.platform.aiot.config.KbProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "iot.aiot.kb.milvus", name = "enabled", havingValue = "true")
public class MilvusKbConfiguration {

    @Bean(destroyMethod = "close")
    public MilvusServiceClient milvusServiceClient(KbProperties kbProperties) {
        KbProperties.Milvus m = kbProperties.getMilvus();
        ConnectParam connect = ConnectParam.newBuilder()
                .withHost(m.getHost())
                .withPort(m.getPort())
                .build();
        return new MilvusServiceClient(connect);
    }
}
