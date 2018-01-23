package cn.zain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 *
 * @author Zain
 */
@ConfigurationProperties(value = "classpath:config/sys-constants.properties")
public class SysConstants {
    /**
     * 取不到使用默认值
     */
    @Value("${sys.constants.name:9527}")
    private String name;

    public String getName() {
        return name;
    }
}
