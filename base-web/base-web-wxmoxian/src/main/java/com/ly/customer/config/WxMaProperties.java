package com.ly.customer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    private String appId;
    private String secret;
}