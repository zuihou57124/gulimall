package com.project.gulimallware.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallware.ware.vo.MergeVo;
import com.project.gulimallware.ware.vo.PurchaseDoneVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallware.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:27:07
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivePage(Map<String, Object> params);

    void merge(MergeVo mergeVo);

    void received(List<Long> purchaseIds);

    void purchaseDone(PurchaseDoneVo purchaseDoneVo);
}

