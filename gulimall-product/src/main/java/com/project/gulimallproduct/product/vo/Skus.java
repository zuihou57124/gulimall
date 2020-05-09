package com.project.gulimallproduct.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Skus {
    /**
     * attr : [{"attrId":12,"attrName":"颜色","attrValue":"白色"},{"attrId":15,"attrName":"内存","attrValue":"8g"},{"attrId":16,"attrName":"存储容量","attrValue":"64g"}]
     * skuName : 魅族17 白色 8g 64g
     * price : 3999
     * skuTitle : 魅族17 白色 8g 64g
     * skuSubtitle : 魅族17
     * images : [{"imgUrl":"","defaultImg":0}]
     * descar : ["白色","8g","64g"]
     * fullCount : 0
     * discount : 0
     * countStatus : 0
     * fullPrice : 0
     * reducePrice : 0
     * priceStatus : 0
     * memberPrice : [{"id":5,"name":"普通会员","price":0},{"id":6,"name":"超级会员","price":0}]
     */

    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<Attr> attr;
    private List<Images> images;
    private List<String> descar;
    private List<MemberPrice> memberPrice;

}
