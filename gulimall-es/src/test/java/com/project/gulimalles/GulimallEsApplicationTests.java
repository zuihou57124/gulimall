package com.project.gulimalles;

import com.alibaba.fastjson.JSON;
import com.project.gulimalles.config.ElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallEsApplicationTests {

    @Autowired
    RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {

        System.out.println(client);
        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("1");
        User user = new User();
        user.setName("秦风");
        user.setGender("男");
        user.setAge(20);
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON);
        client.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
    }

    @Data
    class User {

        private String name;

        private String gender;

        private Integer age;

    }

}
