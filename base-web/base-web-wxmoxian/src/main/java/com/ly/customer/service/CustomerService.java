package com.ly.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.customer.CustomerInfo;
import com.ly.model.vo.customer.CustomerLoginVo;

public interface CustomerService extends IService<CustomerInfo> {


	String login(String code);

	CustomerLoginVo getCustomerLoginInfo();
}
