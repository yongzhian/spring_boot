package cn.zain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (c) 2018 www.yongzhian.cn. All Rights Reserved.
 */

/**
 * 功能说明 ：@Controller如果修改未@RestController，则每个方法不再需要@ResponseBody
 *
 * @author Zain 2016/9/26 14:44
 * @return
 * @params
 */
@RequestMapping("/api")
@Controller
public class ApiController extends AbstractController {

    @RequestMapping("/hello.do")
    @ResponseBody
    String hello() {
        logger.info("hello...");
        return "hello world!,我是SpringBoot";
    }

    /**
     * 功能说明 ：URL中的变量
     * 访问地址 http://localhost:8080/sample/users/123
     *
     * @return
     * @author Zain 2016/9/26 14:46
     * @params
     */
    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    @ResponseBody
    public String userProfile(@PathVariable("username") String username) {
        logger.info("userProfile...");
        return String.format("返回 user %s", username);
    }

    /**
     * 功能说明 ：模板渲染 jsp 等
     *
     * @return
     * @author Zain 2016/9/26 14:51
     * @params
     */
    @RequestMapping("/model/{name}")
    public String model(@PathVariable("name") String name, Model model) {
        logger.info("model...");
        model.addAttribute("name", name);
        model.addAttribute("price", 952);
        return "demo";
    }
}
