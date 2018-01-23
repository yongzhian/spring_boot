package cn.zain.controller.error;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

/**
 * Copyright (c) 2016 www.yongzhian.cn. All Rights Reserved.
 */
//@Controller
public class AppErrorController implements ErrorController{

    private static Logger logger = Logger.getLogger(AppErrorController.class);

    private static AppErrorController appErrorController;

    private ErrorAttributes errorAttributes;

    private final static String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return null;
    }
}
