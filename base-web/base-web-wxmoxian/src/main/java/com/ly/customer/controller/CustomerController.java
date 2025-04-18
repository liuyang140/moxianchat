package com.ly.customer.controller;

import com.ly.common.result.Result;
import com.ly.customer.service.CustomerService;
import com.ly.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Operation(summary = "微信登录")
	@GetMapping("/login/{code}")
	public Result<String> wxLogin(@Schema(description = "微信code") @PathVariable String code){
		return Result.ok(customerService.login(code));
	}

	@Operation(summary = "获取客户登录信息")
	@GetMapping("/getCustomerLoginInfo/{customerId}")
	public Result<CustomerLoginVo> getCustomerLoginInfo(@@PathVariable Long customerId) {
		return Result.ok(customerInfoService.getCustomerLoginInfo(customerId));
	}
}

