/*
package com.ly.security.service;
import com.ly.model.entity.system.SysUser;
import com.ly.security.custom.CustomUser;
import com.ly.system.client.SecurityLoginFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SecurityLoginFeignClient securityLoginFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = securityLoginFeignClient.getByUsername(username).getData();
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(!"admin".equals(sysUser.getUsername()) && sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        List<String> userPermsList = securityLoginFeignClient.findUserPermsList(sysUser.getId()).getData();
        sysUser.setUserPermsList(userPermsList);
        return new CustomUser(sysUser);
    }
}*/
