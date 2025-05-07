package com.ly.customer.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "web-customer")
public interface CustomerInfoFeignClient {

}