package com.project.gulimalles.vo;

import lombok.Data;

import java.util.List;

/**
 * @author qcw
 */
@Data
public class AttrRespVo extends AttrVo {

    String groupName;

    String catelogName;

    /**
     * 分类的完整路径
     */
    List<Long> catelogPath;

}
