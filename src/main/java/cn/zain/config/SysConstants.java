package cn.zain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 * 其他公用常量
 * @author Zain
 */
@Configuration
@ConfigurationProperties(value = "classpath:config/sys-constants.properties")
public class SysConstants {
    /**
     * 取不到使用默认值
     */
    @Value("${sys.constants.name:9527}")
    private String name;

    @Value("${server.port}")
    private String serverPort;

    public String getName() {
        return name;
    }

    public String getServerPort() {
        return serverPort;
    }
}
