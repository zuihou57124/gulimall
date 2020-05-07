package com.project.gulimallproduct.product.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author qcw
 * 属性分组以及属性分组所包含的属性vo
 */
@Data
public class AttrAndGroupVo {

    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 拥有的属性集合
     */
    private List<AttrEntity> attrs;
}
