package com.project.gulimallproduct.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {
    /**
     * buyBounds : 100
     * growBounds : 100
     */

    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
