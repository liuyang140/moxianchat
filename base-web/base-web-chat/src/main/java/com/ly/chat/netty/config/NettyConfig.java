package com.ly.chat.netty.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ly.chat.netty.handler.WebSocketHandler;
import com.ly.chat.netty.websocket.WebSocketInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class NettyConfig {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Value("${netty.port}")
    private int port;

    @Bean
    public WebSocketInitializer webSocketInitializer() {
        return new WebSocketInitializer(webSocketHandler);
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
            instance.setServiceName("web-chat-ws");
            instance.setWeight(discoveryProperties.getWeight());
            instance.setHealthy(true);
            instance.setClusterName(discoveryProperties.getClusterName());
            instance.setMetadata(new HashMap<>(discoveryProperties.getMetadata())); // 复制元数据
            instance.setMetadata(Map.of("protocol", "ws"));

            // 注册服务实例
            namingService.registerInstance("web-chat-ws",
                    discoveryProperties.getGroup(),
                    instance);
            log.info("Netty 服务注册成功: web-chat-ws:10012");
        };
    }
}