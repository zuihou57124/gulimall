package com.project.gulimalles;

import io.renren.datasource.annotation.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qcw
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients("com.project.gulimalles.feign")
public class GulimallEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallEsApplication.class, args);
    }

}
