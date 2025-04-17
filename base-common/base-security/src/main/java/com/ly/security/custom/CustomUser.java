package com.ly.security.custom;

import com.ly.model.entity.system.SysUser;
import lombok.Data;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;


public class CustomUser extends User {

    /**
     * 我们自己的用户实体对象，要调取用户信息时直接获取这个实体对象
     */
    private SysUser sysUser;


    public CustomUser(SysUser sysUser) {
        super(sysUser.getUsername(), sysUser.getPassword(), new ArrayList<>());
        this.sysUser = sysUser;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

}

