package com.project.gulimallproduct;

import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GulimallProductApplicationTests {

    //@Autowired(required = false)
    @Resource(name = "brandService")
    BrandService brandService;

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("huawei");
        brandEntity.setDescript("手机品牌");
        brandService.save(brandEntity);

    }

}
