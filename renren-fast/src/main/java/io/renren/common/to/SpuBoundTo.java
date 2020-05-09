package io.renren.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qcw
 */
@Data
public class SpuBoundTo {

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

}
