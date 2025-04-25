package com.ly.customer.service;

import com.ly.model.vo.customer.CustomerInfoVo;
import com.ly.model.vo.customer.MatchUserVo;

import java.util.List;

public interface MatchService {


     void updateLocation(Long customerId, double latitude, double longitude);

    MatchUserVo matchUser(Long customerId, Double initKm, Double maxKm, Double stepKm);
}
