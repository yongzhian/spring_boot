package cn.zain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Resource;

/**
 * 权限控制
 * @author Zain
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Resource(name = "authSysUserService")
    private UserDetailsService userDetailsService;

    /**
     * 通过重载，配置user-detail服务
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (null == userDetailsService) {
            logger.error("userDetailsService为空：{}", userDetailsService);
            return;
        }
        auth.userDetailsService(userDetailsService); //user Details Service验证
//        auth.inMemoryAuthentication() //内存认证模式，1、可以直接配置用户名和密码 2、
    }

    /**
     * 通过重载，配置如何通过拦截器保护请求
     * 默认进入应用的HTTP请求都要进行认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//         http.authorizeRequests().anyRequest().authenticated() //任何请求,登录后可以访问
        http.authorizeRequests().antMatchers("/my/*").authenticated();
//                .antMathchers("/my/*").authenticated();
//                .regexMatchers("static/*").hasRole("ADMIN") //正则表达式风格,登录后可以访问
//                .and()
//                .formLogin()  //启用默认登陆页面
//                .loginPage("/templates/login.html")
//                .failureUrl("/templates/error.html")
//                .permitAll() //登录页面用户任意访问
//                .and()
//                .logout().permitAll(); //注销行为任意访问
    }

    //5忽略静态资源的拦截,通过重载，配置Spring Security的Filter链
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/static/**","/resources/templates/**");
    }
}
