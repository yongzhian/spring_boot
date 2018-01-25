package cn.zain.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 加载项目时启动
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 *
 * @author Zain
 */
@Component
public class ApplicationStartEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(ApplicationStartEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("spring Start Success...");ThymeleafProperties thymeleafProperties;
        logger.info("WebService path:{}", "http://localhost:9081/services/SysUserWebService?wsdl");
        logger.info("Api path:{}", "http://localhost:9081/api/hello.do");
        logger.info("Api path:{}", "http://localhost:9081/api/users/{username}");
        logger.info("Api path:{}", "http://localhost:9081/api/model/{name}");
    }
}
