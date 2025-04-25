package com.ly.customer.service.impl;

import com.ly.common.util.DistanceUtil;
import com.ly.common.util.RedisUtils;
import com.ly.customer.mapper.ChatRoomMapper;
import com.ly.customer.mapper.CustomerLocationMapper;
import com.ly.customer.service.CustomerLocationService;
import com.ly.customer.service.MatchService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.entity.customer.CustomerLocation;
import com.ly.model.vo.customer.MatchUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MatchServiceImpl implements MatchService {

    @Autowired
    private RedisUtils redisUtil;

    @Autowired
    private CustomerLocationService customerLocationService;

    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    public void updateLocation(Long customerId, double latitude, double longitude) {
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
    public MatchUserVo matchUser(Long customerId, Double initRadius, Double maxRadius, Double stepKm) {
        double radius = initRadius;

        while (radius <= maxRadius) {
            // Redis匹配
            List<MatchUserVo> nearby = redisUtil.searchNearby(customerId, radius);
            if (!nearby.isEmpty()) {
                return createRoom(customerId, randomPick(nearby));
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
                List<MatchUserVo> matchVos = dbMatches.stream()
                        .map(loc -> new MatchUserVo().setCustomerId(loc.getCustomerId())
                                .setDistanceKm(DistanceUtil.calculate(self.getLatitude(), self.getLongitude(), loc.getLatitude(), loc.getLongitude())))
                        .collect(Collectors.toList());

                return createRoom(customerId, randomPick(matchVos));
            }

            radius += stepKm;
        }

        return null;
    }

    private MatchUserVo createRoom(Long userId, MatchUserVo target) {
        ChatRoom room = new ChatRoom();
        room.setCustomer1Id(userId);
        room.setCustomer2Id(target.getCustomerId());
        chatRoomMapper.insert(room);
        return new MatchUserVo().setCustomerId(target.getCustomerId()).setDistanceKm(target.getDistanceKm());
    }

    private MatchUserVo randomPick(List<MatchUserVo> list) {
        return list.get(new Random().nextInt(list.size()));
    }

}