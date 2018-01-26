package cn.zain.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * session放到redis
 * maxInactiveIntervalInSeconds为SpringSession的过期时间（单位：秒）
 * @author Zain
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800)
public class SessionConfig {
}
