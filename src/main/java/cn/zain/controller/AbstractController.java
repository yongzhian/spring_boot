package cn.zain.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zain
 */
public class AbstractController {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    //实际上线时打开
    @Autowired
    protected HttpServletRequest request;
}
