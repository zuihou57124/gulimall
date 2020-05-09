package com.project.gulimallware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * root qcw
 * @author root
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.project.gulimallware.ware.feign")
@EnableTransactionManagement
@MapperScan("com.project.gulimallware.ware.dao")
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
