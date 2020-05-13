package io.renren.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qcw
 * 商品的es传输模型
 */
@Data
public class SkuEsModel {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hasScore;

    private Long brandId;

    private String brandName;

    private String brandImg;

    private Long catalogId;

    private String catelogName;

    private List<Attrs> attrs;

    //直接将商品的规格参数存在模型中
    @Data
    public static class Attrs{
        private Long attrId;

        private String attrName;

        private String attrValue;
    }

}
