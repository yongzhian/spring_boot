package cn.zain.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 加载项目时启动
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 *
 * @author Zain
 */
@Component
public class ApplicationStopEventListener implements ApplicationListener<ContextClosedEvent> {
    private Logger logger = Logger.getLogger(ApplicationStopEventListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        logger.info("spring Stop Success...");
    }
}
