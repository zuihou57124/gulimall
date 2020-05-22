package com.project.gulimallproduct.product.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

/**
 * @author qcw
 * 自定义线程池配置
 */
@Data
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
       return new ThreadPoolExecutor(15,
                100,
                15, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
