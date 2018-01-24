package cn.zain.model.po;

/**
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 *
 * @author Zain
 */

public class SysUser {
    private Long id;
    private String username;
    private String password;

    public SysUser() {
    }

    public SysUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
