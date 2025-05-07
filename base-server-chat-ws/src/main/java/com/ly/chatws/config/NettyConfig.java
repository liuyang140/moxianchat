package com.ly.chatws.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ly.chatws.handler.AuthHandshakeHandler;
import com.ly.chatws.handler.WebSocketHandler;
import com.ly.chatws.websocket.WebSocketInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class NettyConfig {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private AuthHandshakeHandler authHandshakeHandler;


    @Value("${spring.cloud.nacos.discovery.port}")
    private int port;

    @Bean
    public WebSocketInitializer webSocketInitializer() {
        return new WebSocketInitializer(webSocketHandler,authHandshakeHandler);
    }

    @Bean
    public ApplicationRunner registerNettyInstance(NacosServiceManager nacosServiceManager,
                                                   NacosDiscoveryProperties discoveryProperties) {
        return args -> {
            NamingService namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());

            // 构建注册信息
            Instance instance = new Instance();
            instance.setIp(discoveryProperties.getIp());
            instance.setPort(port);
            instance.setServiceName("server-chat-ws");
            instance.setWeight(discoveryProperties.getWeight());
            instance.setHealthy(true);
            instance.setClusterName(discoveryProperties.getClusterName());
            instance.setMetadata(new HashMap<>(discoveryProperties.getMetadata())); // 复制元数据
            instance.setMetadata(Map.of("protocol", "ws"));

            // 注册服务实例
            namingService.registerInstance("server-chat-ws",
                    discoveryProperties.getGroup(),
                    instance);
            log.info("Netty 服务注册成功: server-chat-ws:10012");
        };
    }
}