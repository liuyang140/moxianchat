package com.ly.customer.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WxMaConfig {

	@Autowired
	private WxMaProperties wxMaProperties;

	@Bean
	public WxMaService wxMaService(){
		WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
		wxMaDefaultConfig.setAppid(wxMaProperties.getAppId());
		wxMaDefaultConfig.setSecret(wxMaProperties.getSecret());

		WxMaServiceImpl wxMaService = new WxMaServiceImpl();
		wxMaService.setWxMaConfig(wxMaDefaultConfig);
		return wxMaService;
	}
}
