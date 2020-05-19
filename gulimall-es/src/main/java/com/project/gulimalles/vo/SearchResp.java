package com.project.gulimalles.vo;

import io.renren.common.to.es.SkuEsModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.ArrayList;
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
     * 页码数组
     */
    private List<Integer> pageNavs;

    public void setPageNavs(){
        List<Integer> pageNavs = new ArrayList<>();
        for(int i=1;i<totalPages;i++){
            pageNavs.add(i);
        }
        this.pageNavs = pageNavs;
    }

    /**
     * 查询结果包含的品牌
     */
    private List<BrandVo> brands;

    /**
     * 查询结果包含的属性
     */
    private List<AttrVo> attrs;

    /**
     * 已经筛选的属性id
     */
    private List<Long> selectedAttrs = new ArrayList<>();

    /**
     * 查询结果包含的分类
     */
    private List<CatalogVo> catalogs;

    /**
     * 面包屑导航
     */
    private List<NavVo> navVoList = new ArrayList<>();

    
    @Data
    public static class NavVo{
        private String navName;

        private String navValue;

        private String link;
    }

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
