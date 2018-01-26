package cn.zain.controller;

import cn.zain.dao.SysUserMapper;
import cn.zain.model.po.SysUser;
import cn.zain.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;


/**
 * @author Zain
 */
@RequestMapping("/auth")
@Controller
public class AuthController extends AbstractController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/login")
    String login() {
        logger.info("login..");
        return "login";
    }

    @PostMapping("/loginPost")
    String login(@RequestParam String username, String password) {
        logger.info("loginPost.. username:{} password:{}", username, password);
        final SysUser sysUser = sysUserService.getByUsername(username);
        if(null != sysUser && sysUser.getPassword().equals(password)){
            request.setAttribute("sysUser",sysUser);
            return "index";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 移除session
//        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
