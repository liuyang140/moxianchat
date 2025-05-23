package com.ly.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.dto.customer.UpdateCustomerDTO;
import com.ly.model.entity.customer.CustomerInfo;
import com.ly.model.vo.customer.CustomerLoginVo;
import com.ly.model.vo.customer.CustomerUserVo;

import java.util.List;

public interface CustomerService extends IService<CustomerInfo> {


	String login(String code);

	CustomerLoginVo getCustomerLoginInfo();

	void updateCustomerInfo(UpdateCustomerDTO updateCustomerDTO);

    List<CustomerUserVo> getBatchCustomerInfo(List<Long> customerIds);

	CustomerUserVo getCustomerInfoById(Long customerId);
}
