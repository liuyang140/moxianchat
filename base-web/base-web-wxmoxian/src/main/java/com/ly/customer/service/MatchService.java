package com.ly.customer.service;

import com.ly.model.vo.chat.ReceiverInfoVo;
import com.ly.model.vo.customer.CustomerUserVo;

public interface MatchService {


     void updateLocation(Long customerId, double latitude, double longitude);

     ReceiverInfoVo matchUser(Long customerId, Double initKm, Double maxKm, Double stepKm);
}
