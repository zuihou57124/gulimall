package com.project.gulimallproduct.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author qcw
 */
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("GULIMALLSESSIONID");
        //serializer.setCookiePath("/");
        //serializer.setDomainNamePattern("^127.+?\\.(\\w+\\.[0-9]+)$");
        //serializer.setDomainNamePattern("127.0.0(\\w+\\.[0-9]+)$");
        serializer.setDomainNamePattern("(^127.0.0.)[1-9]{1}$");
        //serializer.setDomainName("127.0.0.1");
        return serializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
