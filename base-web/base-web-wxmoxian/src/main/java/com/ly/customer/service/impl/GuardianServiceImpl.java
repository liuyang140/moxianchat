package com.ly.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.common.util.UserCacheUtils;
import com.ly.customer.mapper.GuardianConfigMapper;
import com.ly.customer.mapper.GuardianEmergencyContactMapper;
import com.ly.customer.mapper.GuardianScheduleRuleMapper;
import com.ly.customer.service.GuardianService;
import com.ly.model.entity.guardian.GuardianConfig;
import com.ly.model.entity.guardian.GuardianEmergencyContact;
import com.ly.model.entity.guardian.GuardianScheduleRule;
import com.ly.model.request.guardian.GuardianConfigRequest;
import com.ly.model.request.guardian.MarkNotifiedRequest;
import com.ly.model.vo.guardian.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class GuardianServiceImpl extends ServiceImpl<GuardianConfigMapper, GuardianConfig> implements GuardianService {

    private final UserCacheUtils userCacheUtils;
    private final GuardianScheduleRuleMapper guardianScheduleRuleMapper;
    private final GuardianEmergencyContactMapper guardianEmergencyContactMapper;


    @Override
    public GuardianConfigVO getGuardianConfig() {
        Long userId = userCacheUtils.getLoginUserId();
        // 查询主配置
        GuardianConfig config = this.getBaseMapper().selectOne(
                new LambdaQueryWrapper<GuardianConfig>()
                        .eq(GuardianConfig::getUserId, userId)
                        .eq(GuardianConfig::getIsDeleted, false)
        );

        if (config == null) {
            return null; // 未配置过
        }

        // 构建返回对象
        GuardianConfigVO vo = new GuardianConfigVO();
        BeanUtils.copyProperties(config, vo);

        // 查询紧急联系人
        List<GuardianEmergencyContact> contacts = guardianEmergencyContactMapper.selectList(
                new LambdaQueryWrapper<GuardianEmergencyContact>()
                        .eq(GuardianEmergencyContact::getUserId, userId)
                        .eq(GuardianEmergencyContact::getIsDeleted, false)
        );

        vo.setContacts(BeanUtil.copyToList(contacts, EmergencyContactVO.class));

        // 查询时间配置
        List<GuardianScheduleRule> timeConfigList = guardianScheduleRuleMapper.selectList(
                new LambdaQueryWrapper<GuardianScheduleRule>()
                        .eq(GuardianScheduleRule::getConfigId, config.getId())
                        .eq(GuardianScheduleRule::getIsDeleted, false)
        );

        vo.setScheduleRules(BeanUtil.copyToList(timeConfigList, ScheduleRuleVO.class));
        return vo;
    }

    @Override
    @Transactional
    public void saveOrUpdateConfig(GuardianConfigRequest req) {
        Long userId = userCacheUtils.getLoginUserId();

        // 查询是否已有配置
        GuardianConfig config = this.getBaseMapper().selectOne(
                new LambdaQueryWrapper<GuardianConfig>()
                        .eq(GuardianConfig::getUserId, userId)
                        .eq(GuardianConfig::getIsDeleted, false)
        );

        if (config == null) {
            // 新增
            config = new GuardianConfig();
            config.setUserId(userId);
        }

        // 更新字段
        config.setEnabled(req.getEnabled());
        config.setAddress(req.getAddress());
        config.setNotified(false); // 配置修改后重新触发告警逻辑

        // 保存配置
        this.saveOrUpdate(config);

        Long configId = config.getId();

        // --- 保存时间规则 ---
        // 先逻辑删除旧规则
        LambdaUpdateWrapper<GuardianScheduleRule> ruleDeleteWrapper = new LambdaUpdateWrapper<>();
        ruleDeleteWrapper.eq(GuardianScheduleRule::getConfigId, configId)
                .set(GuardianScheduleRule::getIsDeleted, true)
                .set(GuardianScheduleRule::getUpdateTime, LocalDateTime.now());
        guardianScheduleRuleMapper.update(null, ruleDeleteWrapper);

        if (CollectionUtil.isNotEmpty(req.getScheduleRules())) {
            List<GuardianScheduleRule> newRules = req.getScheduleRules().stream().map(ruleReq -> {
                GuardianScheduleRule rule = new GuardianScheduleRule();
                BeanUtil.copyProperties(ruleReq, rule);
                rule.setConfigId(configId);
                return rule;
            }).collect(Collectors.toList());

            guardianScheduleRuleMapper.batchInsert(newRules);
        }

        // --- 保存紧急联系人 ---
        // 先逻辑删除旧联系人
        LambdaUpdateWrapper<GuardianEmergencyContact> contactDeleteWrapper = new LambdaUpdateWrapper<>();
        contactDeleteWrapper.eq(GuardianEmergencyContact::getUserId, userId)
                .set(GuardianEmergencyContact::getIsDeleted, true)
                .set(GuardianEmergencyContact::getUpdateTime, LocalDateTime.now());
        guardianEmergencyContactMapper.update(null, contactDeleteWrapper);

        if (CollectionUtil.isNotEmpty(req.getContacts())) {
            List<GuardianEmergencyContact> newContacts = req.getContacts().stream().map(contactReq -> {
                GuardianEmergencyContact contact = new GuardianEmergencyContact();
                contact.setUserId(userId);
                BeanUtil.copyProperties(contactReq, contact);
                return contact;
            }).collect(Collectors.toList());

            guardianEmergencyContactMapper.batchInsert(newContacts);
        }
    }

    @Override
    public void refreshActive() {

    }

    @Override
    public GuardianAlertStatusVO getAlertStatus() {
        return null;
    }

    @Override
    public List<GuardianAlertUserVO> getUsersNeedAlert() {
        return List.of();
    }

    @Override
    public void markUsersNotified(MarkNotifiedRequest req) {

    }
}
