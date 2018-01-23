package cn.zain.listener;

import cn.zain.config.SysConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 加载项目时启动
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 *
 * @author Zain
 */
public class ApplicationStartEventListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = Logger.getLogger(ApplicationStartEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("spring Start Success...");
    }
}
