package com.ly.customer.service.impl;

import com.ly.common.util.DistanceUtil;
import com.ly.common.util.RedisUtils;
import com.ly.common.util.UserCacheUtils;
import com.ly.customer.mapper.ChatRoomMapper;
import com.ly.customer.mapper.CustomerLocationMapper;
import com.ly.customer.service.ChatRoomService;
import com.ly.customer.service.ChatRoomUserService;
import com.ly.customer.service.CustomerLocationService;
import com.ly.customer.service.MatchService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.entity.chat.ChatRoomUser;
import com.ly.model.entity.customer.CustomerLocation;
import com.ly.model.enums.ChatEnum;
import com.ly.model.vo.customer.MatchUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MatchServiceImpl implements MatchService {

    @Autowired
    private RedisUtils redisUtil;

    @Autowired
    private UserCacheUtils userCacheUtils;

    @Autowired
    private CustomerLocationService customerLocationService;

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private CustomerLocationMapper customerLocationMapper;
    @Autowired
    private ChatRoomUserService chatRoomUserService;

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

        if(Objects.isNull(customerId)) customerId = userCacheUtils.getCustomerId();
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

    public MatchUserVo createRoom(Long userId, MatchUserVo target) {
        Long targetId = target.getCustomerId();

        // 1. 查找是否已经存在一个 "private" 聊天室，并且这两个用户都在
        List<Long> commonRoomIds = chatRoomUserService.lambdaQuery()
                .select(ChatRoomUser::getRoomId)
                .in(ChatRoomUser::getUserId, Arrays.asList(userId, targetId))
                .list()
                .stream()
                .collect(Collectors.groupingBy(ChatRoomUser::getRoomId))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        ChatRoom room = null;
        if (!commonRoomIds.isEmpty()) {
            // 2. 查询这些房间中有没有 type=private 的
            room = chatRoomService.lambdaQuery()
                    .in(ChatRoom::getId, commonRoomIds)
                    .eq(ChatRoom::getType, ChatEnum.PRIVATE.getValue())
                    .last("LIMIT 1")
                    .one();
        }

        if (room == null) {
            // 3. 没有，创建新的聊天室
            room = new ChatRoom();
            room.setType(ChatEnum.PRIVATE.getValue());
            room.setName(null);
            chatRoomService.save(room);

            // 4. 插入两条聊天室成员记录
            ChatRoomUser user1 = new ChatRoomUser();
            user1.setRoomId(room.getId());
            user1.setUserId(userId);
            user1.setIsOwner(true);

            ChatRoomUser user2 = new ChatRoomUser();
            user2.setRoomId(room.getId());
            user2.setUserId(targetId);
            user2.setIsOwner(false);

            chatRoomUserService.saveBatch(Arrays.asList(user1, user2));
        }

        // 5. 返回
        return new MatchUserVo()
                .setCustomerId(targetId)
                .setDistanceKm(target.getDistanceKm())
                .setRoomId(room.getId());
    }
    private MatchUserVo randomPick(List<MatchUserVo> list) {
        return list.get(new Random().nextInt(list.size()));
    }

}