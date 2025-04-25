package com.ly.common.util;

import com.ly.model.vo.customer.MatchUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.geo.Metrics;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ly.common.constant.RedisConstant.GEO_KEY;

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

    /**
     * 获取 key 剩余过期时间（单位：秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    public void saveUserLocation(Long userId, double latitude, double longitude) {
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(longitude, latitude), String.valueOf(userId));
        redisTemplate.expire(GEO_KEY, Duration.ofHours(24));
    }

    public List<MatchUserVo> searchNearby(Long userId, double radiusKm) {
        List<Point> points = redisTemplate.opsForGeo().position(GEO_KEY, String.valueOf(userId));
        if (points == null || points.isEmpty() || points.get(0) == null) return Collections.emptyList();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
                GEO_KEY,
                new Circle(points.get(0), new Distance(radiusKm, RedisGeoCommands.DistanceUnit.KILOMETERS)),
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending()
        );

        if (results == null) return Collections.emptyList();

        return results.getContent().stream()
                .map(res -> {
                    String id = res.getContent().getName();
                    double dist = res.getDistance().getValue();
                    MatchUserVo matchUserVo = new MatchUserVo();
                    matchUserVo.setCustomerId(Long.valueOf(id));
                    matchUserVo.setDistanceKm(dist);
                    return matchUserVo;
                })
                .filter(u -> !u.getCustomerId().equals(userId))
                .collect(Collectors.toList());
    }
}