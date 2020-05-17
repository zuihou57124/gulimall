package com.project.gulimalles.vo;

import io.renren.common.to.es.SkuEsModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author qcw
 * 检索商品的返回结果模型
 */
@Data
public class SearchResp {

    /**
     * 查询到的商品信息
     */
    private List<SkuEsModel> skuEsModels;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Long totalPages;

    /**
     * 查询结果包含的品牌
     */
    private List<BrandVo> brands;

    /**
     * 查询结果包含的属性
     */
    private List<AttrVo> attrs;

    /**
     * 查询结果包含的分类
     */
    private List<CatalogVo> catalogs;


    @Data
    public static class BrandVo{

        private Long brandId;

        private String brandName;

        private String brandImg;

    }

    @Data
    public static class AttrVo{
        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo{

        private Long catalogId;

        private String catelogName;
    }


}
