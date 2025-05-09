package com.ly.chat.utils;

import com.ly.common.execption.LyException;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCodeEnum;
import com.ly.customer.client.CustomerInfoFeignClient;
import com.ly.model.dto.chat.RoomCreateDTO;
import com.ly.model.vo.customer.CustomerUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientUtils {

    @Autowired
    private CustomerInfoFeignClient  customerInfoFeignClient;

    /*
    * 批量获取人员信息
    * */
    public List<CustomerUserVo> getBatchUserVos(List<Long> customerIds){
        Result<List<CustomerUserVo>> result = customerInfoFeignClient.getBatchCustomerInfo(customerIds);
        if (result != null && result.isOk()) {
            return result.getData();
        } else {
            throw new LyException(ResultCodeEnum.FAIL.getCode(),"房间创建失败: " + (result != null ? result.getMessage() : "未知错误"));
        }
    }
}
