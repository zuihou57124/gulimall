package com.project.gulimallproduct.product.vo;

import com.project.gulimallproduct.product.entity.SkuImagesEntity;
import com.project.gulimallproduct.product.entity.SkuInfoEntity;
import com.project.gulimallproduct.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author qcw
 */
@Data
public class SkuItemVo {

    /**
     * 1.sku基本信息
     */
    SkuInfoEntity skuInfo;

    /**
     * 是否有货(默认有货)
     */
    Boolean hasStock = true;

    /**
     * 2.sku图片信息
     */
    List<SkuImagesEntity> skuImages;

    /**
     * 3.spu销售属性组合
     */
    List<SkuItemSaleAttrVo> saleAttrVos;

    /**
     * 4.spu介绍(描述)
     */
    SpuInfoDescEntity spuInfoDesc;

    /**
     * 5.spu规格参数信息
     */
    List<SpuItemBaseAttrVo> groupAttrs;


    @Data
    public static class SkuItemSaleAttrVo{

        private Long attrId;

        private String attrName;

        private List<String> attrValues;

        private List<SaleAttrWithSkuId> attrValues2;
    }

    /**
     * 每种属性值被包含在哪些sku中
     */
    @Data
    public static class SaleAttrWithSkuId{

        private String attrValue;

        private String skuIds="";
    }

    /**
     * spu所属分组的所有规格参数
     */
    @Data
    public static class SpuItemBaseAttrVo{

        private String groupName;

        private List<SpuBaseAttrVo> spuBaseAttrs;

    }


    /**
     * spu规格参数
     */
    @Data
    public static class SpuBaseAttrVo{

        private String attrName;

        private String attrValue;

    }


}
