package com.ly.customer.service.impl;

import com.ly.common.execption.LyException;
import com.ly.common.result.ResultCodeEnum;
import com.ly.customer.util.ClientUtils;
import com.ly.common.util.DistanceUtil;
import com.ly.common.util.RedisUtils;
import com.ly.common.util.UserCacheUtils;
import com.ly.customer.mapper.CustomerLocationMapper;
import com.ly.customer.service.*;
import com.ly.model.entity.customer.CustomerInfo;
import com.ly.model.entity.customer.CustomerLocation;
import com.ly.model.vo.customer.CustomerUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchServiceImpl implements MatchService {

    @Autowired
    private RedisUtils redisUtil;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private CustomerLocationService customerLocationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    @Autowired
    private ClientUtils clientUtils;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLocation(Long customerId, double latitude, double longitude) {

        CustomerInfo customerInfo = customerService.getById(customerId);
        if (Objects.isNull(customerInfo)){
            throw new LyException(ResultCodeEnum.ACCOUNT_NOT_FOUND);
        }

        redisUtil.saveUserLocation(customerId, latitude, longitude);

        CustomerLocation location = customerLocationService.lambdaQuery()
                .eq(CustomerLocation::getCustomerId, customerId)
                .one();

        if(Objects.isNull(location)){
            location = new CustomerLocation();
            location.setCustomerId(customerId);
        }
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        customerLocationService.saveOrUpdate(location);
    }

    /**
     * 发起匹配请求
     */
    @Override
    public CustomerUserVo matchUser(Long customerId, Double initRadius, Double maxRadius, Double stepKm) {

        if(Objects.isNull(customerId)) customerId = userCacheUtils.getCustomerId();
        double radius = initRadius;

        CustomerUserVo customerUserVo = null;
        /*if (maxRadius > 100.0){
            maxRadius = 100.0;
        }*/
        while (radius <= maxRadius) {//暂未设置循环最大限制或最大距离限制，若请求发起距离过长可能导致服务器长时间循环
            // Redis匹配
            List<CustomerUserVo> nearby = redisUtil.searchNearby(customerId, radius);
            if (!nearby.isEmpty()) {
                customerUserVo = clientUtils.createRoom(customerId, randomPick(nearby));
                break;
            }

            // 数据库匹配
            CustomerLocation self = customerLocationService.lambdaQuery().eq(CustomerLocation::getCustomerId, customerId).one();
            if (self == null) return null;

            List<CustomerLocation> dbMatches = customerLocationMapper.selectNearbyUsers(
                    customerId,
                    self.getLatitude(),
                    self.getLongitude(),
                    radius
            );

            if (!dbMatches.isEmpty()) {
                List<CustomerUserVo> matchVos = dbMatches.stream()
                        .map(loc -> new CustomerUserVo().setCustomerId(loc.getCustomerId())
                                .setDistanceKm(DistanceUtil.calculate(self.getLatitude(), self.getLongitude(), loc.getLatitude(), loc.getLongitude())))
                        .collect(Collectors.toList());

                customerUserVo = clientUtils.createRoom(customerId, randomPick(matchVos));
                break;
            }

            radius += stepKm;
        }

        if(Objects.nonNull(customerUserVo)){
            CustomerInfo customerInfo = customerService.getById(customerUserVo.getCustomerId());
            customerUserVo.setNickname(customerInfo.getNickname())
                    .setAvatarUrl(customerInfo.getAvatarUrl())
                    .setGender(customerInfo.getGender())
                    .setPhone(customerInfo.getPhone());
        }

        return customerUserVo;
    }



    private CustomerUserVo randomPick(List<CustomerUserVo> list) {
        return list.get(new Random().nextInt(list.size()));
    }

}