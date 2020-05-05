package com.project.gulimallthirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSS ossClient;

    @Test
    void contextLoads() throws Exception {

        ossClient = (OSSClient)ossClient;
        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\root\\Desktop\\测试图片\\01.jpg");
        ossClient.putObject("qinfengoss", "01.jpg", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        inputStream.close();

    }

}
