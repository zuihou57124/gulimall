package com.project.gulimallware.ware.vo;

import com.project.gulimallware.ware.entity.PurchaseDetailEntity;
import lombok.Data;

import java.util.List;

/**
 * @author qcw
 * 完成采购的vo封装
 */
@Data
public class PurchaseDoneVo {

        private Long id;

        private List<PurchaseItemDoneVo> items;

}
