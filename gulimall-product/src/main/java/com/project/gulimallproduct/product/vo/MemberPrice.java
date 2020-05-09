package com.project.gulimallproduct.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {
    /**
     * id : 5
     * name : 普通会员
     * price : 0
     */

    private Long id;
    private String name;
    private BigDecimal price;

}
