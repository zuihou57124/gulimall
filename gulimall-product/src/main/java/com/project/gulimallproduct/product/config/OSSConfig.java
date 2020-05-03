package com.project.gulimallproduct.product.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qcw
 */
//@Configuration
public class OSSConfig {

    @Bean(name = "OSSClient")
    public OSS getOssClient() {
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        String accessKeyId = "LTAI4GCgva5jtoYnBtYEkfV2";
        String accessKeySecret = "Psx2aEs86ncYoY58HhuQ5ZsAtiUTpD";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

}
