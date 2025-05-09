package com.ly.customer.controller;

import com.ly.common.result.Result;
import com.ly.customer.service.CustomerService;
import com.ly.model.dto.customer.UpdateCustomerDTO;
import com.ly.model.vo.customer.CustomerLoginVo;
import com.ly.model.vo.customer.CustomerUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Operation(summary = "微信登录")
	@GetMapping("/wxLogin")
	public Result<String> wxLogin(@Schema(description = "微信code", name = "code") @RequestParam(value = "code") String code){
		return Result.ok(customerService.login(code));
	}

	@Operation(summary = "本地微信登录测试")
	@GetMapping("/login/{code}")
	public Result<String> login(@Schema(description = "微信code", name = "code") @PathVariable(value = "code") String code){
		return Result.ok(customerService.login(code));
	}

	@Operation(summary = "获取用户登录信息")
	@GetMapping("/getCustomerLoginInfo")
	public Result<CustomerLoginVo> getCustomerLoginInfo() {
		return Result.ok(customerService.getCustomerLoginInfo());
	}

	@Operation(summary = "用户信息更新")
	@PostMapping("/updateCustomerInfo")
	public Result updateCustomerInfo(@RequestBody UpdateCustomerDTO updateCustomerDTO) {
		customerService.updateCustomerInfo(updateCustomerDTO);
		return Result.ok();
	}

	@Operation(summary = "批量获取用户信息")
	@GetMapping("/getBatchCustomerInfo")
	public Result<List<CustomerUserVo>> getBatchCustomerInfo(
			@Parameter(description = "用户id列表", required = true) @RequestParam(value = "customerIds") List<Long> customerIds) {
		List<CustomerUserVo> list = customerService.getBatchCustomerInfo(customerIds);
		return Result.ok(list);
	}


}

