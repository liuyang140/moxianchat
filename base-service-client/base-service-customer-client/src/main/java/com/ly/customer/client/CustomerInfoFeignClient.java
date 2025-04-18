package com.ly.customer.client;

import com.ly.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

	@GetMapping("/customer/info/login/{code}")
	Result<Long> login(@PathVariable("code") String code);
}