package cn.zain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;

import javax.annotation.PreDestroy;

/**
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 * ws包提供webservice服务
 * controller包提供api和页面跳转服务
 * @author Zain
 */

@SpringBootApplication
@EnableConfigurationProperties
@ImportResource(locations = {"classpath:config/application-bean.xml"})
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ServerProperties serverProperties;

    /**
     * 启动类，基于注解可添加监听等
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 产生一个bean的方法，并且交给Spring容器管理,可以修改系统默认的servlet容器配置
     * 函数式接口
     *
     * @return
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400.html"), //resources/static 目录下
                    new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"),
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"),
                    new ErrorPage("/error.html")
            );
        };
    }

    @PreDestroy
    private void exit() {
        //销毁操作
        logger.info("执行退出清理操作...");
    }
}
