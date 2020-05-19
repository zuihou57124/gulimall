package com.project.gulimalles.vo;

import lombok.Data;

import java.util.List;

/**
 * @author qcw
 * 搜索商品的条件
 * 简单条件：商品id，商品名称，三级分类id等等
 * 复杂条件：根据品牌，商品属性，价格，销量等
 */
@Data
public class SearchParam {

    private String keyword;

    private Long catelog3Id;

    /**
     * 排序条件
     * skuPrice_asc/desc
     * saleCount_asc/desc
     * hotScore_asc/desc
     */
    private String sort;

    /**
     * hasStock=0/1
     */
    private Integer hasStock;

    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * skuPrice=1_500/_500/500_
     */
    private String skuPrice;

    /**
     * attrs=2_5寸:6寸&1_安卓
     */
    private List<String> attrs;

    private Integer pageNum = 1;

    /**
     * 从页面传回的查询参数
     */
    private String queryString;

}
