package com.ly.customer.client;

import com.ly.common.config.feign.FeignConfig;
import com.ly.common.result.Result;
import com.ly.model.vo.customer.CustomerUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "web-customer",configuration = FeignConfig.class )
public interface CustomerInfoFeignClient {


    @GetMapping("/customer/getBatchCustomerInfo")
    Result<List<CustomerUserVo>> getBatchCustomerInfo(@RequestParam(value = "customerIds") List<Long> customerIds);




}