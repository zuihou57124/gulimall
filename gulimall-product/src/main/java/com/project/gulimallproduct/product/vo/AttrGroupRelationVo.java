package com.project.gulimallproduct.product.vo;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;

/**
 * @author qcw
 */
@Data
public class AttrGroupRelationVo {

    @NotNull(message = "属性id不能为空")
    
    private Long attrId;

    @NotNull(message = "分组id不能为空")
    private Long attrGroupId;

}
