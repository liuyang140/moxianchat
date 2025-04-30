package com.ly.customer.client;

import com.ly.common.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "web-customer", configuration = FeignConfig.class)
public interface CustomerInfoFeignClient {

}