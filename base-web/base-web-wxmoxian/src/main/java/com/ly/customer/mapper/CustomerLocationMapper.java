package com.ly.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.customer.CustomerLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CustomerLocationMapper extends BaseMapper<CustomerLocation> {
    List<CustomerLocation> selectNearbyUsers(@Param("userId") Long userId,
                                             @Param("lat") Double lat,
                                             @Param("lng") Double lng,
                                             @Param("radiusKm") Double radiusKm);
}