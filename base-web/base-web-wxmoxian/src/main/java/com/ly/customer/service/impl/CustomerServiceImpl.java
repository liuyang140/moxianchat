package com.ly.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.common.constant.RedisConstant;
import com.ly.common.execption.LyException;
import com.ly.common.result.ResultCodeEnum;
import com.ly.common.util.JwtUtils;
import com.ly.common.util.RedisUtils;
import com.ly.common.util.UserCacheUtils;
import com.ly.customer.mapper.CustomerInfoMapper;
import com.ly.customer.mapper.CustomerLoginLogMapper;
import com.ly.customer.service.CustomerService;
import com.ly.model.dto.user.CacheUserDTO;
import com.ly.model.entity.customer.CustomerInfo;
import com.ly.model.entity.customer.CustomerLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl  extends ServiceImpl<CustomerInfoMapper, CustomerInfo>  implements CustomerService {

	@Autowired
	private UserCacheUtils userCacheUtils;

	@Autowired
	private WxMaService wxMaService;

	@Autowired
	private CustomerLoginLogMapper customerLoginLogMapper;

	@Override
	public String login(String code) {

		Long customerId = this.getLoginCustomerId(code);
		if(customerId == null){
			throw new LyException(ResultCodeEnum.DATA_ERROR);
		}
		//派发token使用jwt工具类，用customerId作为唯一标识，缓存到当前线程和redis中
		String token = JwtUtils.generateToken(customerId);
		return token;
	}

	private long getLoginCustomerId(String code) {
		String openId = null;
		try {
			WxMaUserService userService = wxMaService.getUserService();
			WxMaJscode2SessionResult sessionInfo = userService.getSessionInfo(code);
			openId = sessionInfo.getOpenid();
			log.info("openId = {}",openId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LyException(ResultCodeEnum.WX_CODE_ERROR);
		}

		CustomerInfo customerInfo = lambdaQuery().eq(CustomerInfo::getWxOpenId,openId).one();
		if(Objects.isNull(customerInfo)){
			customerInfo = new CustomerInfo();
			customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));
			customerInfo.setWxOpenId(openId);
			customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
			this.save(customerInfo);
		}

		CacheUserDTO cacheUserDTO = BeanUtil.copyProperties(customerInfo, CacheUserDTO.class);
		//缓存用户信息
		userCacheUtils.cacheUser(customerInfo.getId(), cacheUserDTO);

		//登录日志
		CustomerLoginLog customerLoginLog = new CustomerLoginLog();
		customerLoginLog.setCustomerId(customerInfo.getId());
		customerLoginLog.setMsg("小程序登录");
		customerLoginLogMapper.insert(customerLoginLog);

		return customerInfo.getId();

	}
}
