package com.project.gulimallproduct.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://59.110.137.100:6379");
        return Redisson.create(config);
    }

}
