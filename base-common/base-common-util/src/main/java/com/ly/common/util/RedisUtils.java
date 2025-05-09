package com.ly.common.util;

import com.ly.common.constant.RedisConstant;
import com.ly.model.vo.customer.CustomerUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置 key 的字符串值，并可指定过期时间（单位：秒）
     */
    public void set(String key, String value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 设置 key 的字符串值（不过期）
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定 key 的字符串值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定 key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void deleteKeys(String... keys) {
        redisTemplate.delete(List.of(keys));
    }

    // 从 Set 中移除元素
    public Long sRemove(String key, String... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }


    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置 key 的过期时间（单位：秒）
     */
    public void expire(String key, long timeoutSeconds) {
        redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    public void expire(String key, long timeoutSeconds, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeoutSeconds, timeUnit);
    }

    /**
     * 获取 key 剩余过期时间（单位：秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // 添加元素到 Set 中
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Boolean isMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public void saveUserLocation(Long userId, double latitude, double longitude) {
        redisTemplate.opsForGeo().add(RedisConstant.GEO_KEY, new Point(longitude, latitude), String.valueOf(userId));
        redisTemplate.expire(RedisConstant.GEO_KEY, Duration.ofHours(24));
    }


    public List<CustomerUserVo> searchNearby(Long userId, double radiusKm) {
        List<Point> points = redisTemplate.opsForGeo().position(RedisConstant.GEO_KEY, String.valueOf(userId));
        if (points == null || points.isEmpty() || points.get(0) == null) return Collections.emptyList();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
                RedisConstant.GEO_KEY,
                new Circle(points.get(0), new Distance(radiusKm, RedisGeoCommands.DistanceUnit.KILOMETERS)),
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending()
        );

        if (results == null) return Collections.emptyList();

        return results.getContent().stream()
                .map(res -> {
                    String id = res.getContent().getName();
                    double dist = res.getDistance().getValue();
                    CustomerUserVo customerUserVo = new CustomerUserVo();
                    customerUserVo.setCustomerId(Long.valueOf(id));
                    customerUserVo.setDistanceKm(dist);
                    return customerUserVo;
                })
                .filter(u -> !u.getCustomerId().equals(userId))
                .collect(Collectors.toList());
    }
}