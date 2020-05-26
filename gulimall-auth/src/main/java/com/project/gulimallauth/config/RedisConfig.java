package com.project.gulimallauth.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author qcw
 */
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
public class RedisConfig extends CachingConfigurerSupport{

        @Bean
        public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
            config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
            config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

            CacheProperties.Redis redis = cacheProperties.getRedis();
            if(redis.getTimeToLive()!=null){
                config = config.entryTtl(redis.getTimeToLive());
            }
            if(!redis.isCacheNullValues()){
                config = config.disableCachingNullValues();
            }

            return config;
        }

}


