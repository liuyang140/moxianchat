package com.ly.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.model.entity.guardian.GuardianScheduleRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface GuardianScheduleRuleMapper extends BaseMapper<GuardianScheduleRule> {

    /**
     * 批量插入守护时间规则
     * @param rules 规则列表
     * @return 影响行数
     */
    int batchInsert(List<GuardianScheduleRule> rules);


}