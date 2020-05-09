package com.project.gulimallproduct.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qcw
 */
@Data
public class SpuSaveVo {


    /**
     * spuName : 魅族17
     * spuDescription : 魅族17
     * catalogId : 225
     * brandId : 6
     * weight : 0.175
     * publishStatus : 0
     * decript : ["https://qinfengoss.oss-cn-shenzhen.aliyuncs.com/2020-05-08//a2d57d8a-b184-4b5f-8a57-5304d912cbec_meizu.png"]
     * images : ["https://qinfengoss.oss-cn-shenzhen.aliyuncs.com/2020-05-08//f68c9641-2f62-49c2-81bc-e755e94bd366_meizu.png"]
     * bounds : {"buyBounds":100,"growBounds":100}
     * baseAttrs : [{"attrId":13,"attrValues":"玻璃;陶瓷","showDesc":0},{"attrId":17,"attrValues":"4000","showDesc":0},{"attrId":18,"attrValues":"全网通;移动联通","showDesc":0},{"attrId":10,"attrValues":"led","showDesc":1},{"attrId":20,"attrValues":"6.5;5.5","showDesc":0},{"attrId":19,"attrValues":"高通865","showDesc":1}]
     * skus : [{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"白色"},{"attrId":15,"attrName":"内存","attrValue":"8g"},{"attrId":16,"attrName":"存储容量","attrValue":"64g"}],"skuName":"魅族17 白色 8g 64g","price":"3999","skuTitle":"魅族17 白色 8g 64g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["白色","8g","64g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"白色"},{"attrId":15,"attrName":"内存","attrValue":"8g"},{"attrId":16,"attrName":"存储容量","attrValue":"128g"}],"skuName":"魅族17 白色 8g 128g","price":"3999","skuTitle":"魅族17 白色 8g 128g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["白色","8g","128g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"白色"},{"attrId":15,"attrName":"内存","attrValue":"16g"},{"attrId":16,"attrName":"存储容量","attrValue":"64g"}],"skuName":"魅族17 白色 16g 64g","price":"3999","skuTitle":"魅族17 白色 16g 64g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["白色","16g","64g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"白色"},{"attrId":15,"attrName":"内存","attrValue":"16g"},{"attrId":16,"attrName":"存储容量","attrValue":"128g"}],"skuName":"魅族17 白色 16g 128g","price":"3999","skuTitle":"魅族17 白色 16g 128g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["白色","16g","128g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"蓝色"},{"attrId":15,"attrName":"内存","attrValue":"8g"},{"attrId":16,"attrName":"存储容量","attrValue":"64g"}],"skuName":"魅族17 蓝色 8g 64g","price":"3999","skuTitle":"魅族17 蓝色 8g 64g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["蓝色","8g","64g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"蓝色"},{"attrId":15,"attrName":"内存","attrValue":"8g"},{"attrId":16,"attrName":"存储容量","attrValue":"128g"}],"skuName":"魅族17 蓝色 8g 128g","price":"3999","skuTitle":"魅族17 蓝色 8g 128g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["蓝色","8g","128g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"蓝色"},{"attrId":15,"attrName":"内存","attrValue":"16g"},{"attrId":16,"attrName":"存储容量","attrValue":"64g"}],"skuName":"魅族17 蓝色 16g 64g","price":"3999","skuTitle":"魅族17 蓝色 16g 64g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["蓝色","16g","64g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]},{"attr":[{"attrId":12,"attrName":"颜色","attrValue":"蓝色"},{"attrId":15,"attrName":"内存","attrValue":"16g"},{"attrId":16,"attrName":"存储容量","attrValue":"128g"}],"skuName":"魅族17 蓝色 16g 128g","price":"3999","skuTitle":"魅族17 蓝色 16g 128g","skuSubtitle":"魅族17","images":[{"imgUrl":"","defaultImg":0}],"descar":["蓝色","16g","128g"],"fullCount":0,"discount":0,"countStatus":0,"fullPrice":0,"reducePrice":0,"priceStatus":0,"memberPrice":[{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]}]
     */

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private Bounds bounds;
    private List<String> decript;
    private List<String> images;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


}
