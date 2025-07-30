package com.ly.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.guardian.GuardianConfig;
import com.ly.model.request.guardian.GuardianConfigRequest;
import com.ly.model.request.guardian.MarkNotifiedRequest;
import com.ly.model.vo.guardian.GuardianAlertStatusVO;
import com.ly.model.vo.guardian.GuardianAlertUserVO;
import com.ly.model.vo.guardian.GuardianConfigVO;

import java.util.List;

public interface GuardianService extends IService<GuardianConfig> {

    GuardianConfigVO getGuardianConfig();

    void saveOrUpdateConfig(GuardianConfigRequest req);

    void refreshActive();

    GuardianAlertStatusVO getAlertStatus();

    List<GuardianAlertUserVO> getUsersNeedAlert();

    void markUsersNotified(MarkNotifiedRequest req);
}