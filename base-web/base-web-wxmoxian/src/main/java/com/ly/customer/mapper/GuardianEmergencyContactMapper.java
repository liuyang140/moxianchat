package com.ly.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.guardian.GuardianEmergencyContact;

import java.util.List;

public interface GuardianEmergencyContactMapper extends BaseMapper<GuardianEmergencyContact> {

    /**
     * 批量插入紧急联系人
     * @param contacts 联系人列表
     * @return 影响行数
     */
    int batchInsert(List<GuardianEmergencyContact> contacts);


}