package com.ly.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.customer.mapper.CustomerLocationMapper;
import com.ly.customer.service.CustomerLocationService;
import com.ly.model.entity.customer.CustomerLocation;
import org.springframework.stereotype.Service;

@Service
public class CustomerLocationServiceImpl extends ServiceImpl<CustomerLocationMapper, CustomerLocation> implements CustomerLocationService {
}
