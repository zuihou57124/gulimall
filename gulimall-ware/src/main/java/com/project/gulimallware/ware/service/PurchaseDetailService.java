package com.project.gulimallware.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import com.project.gulimallware.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:27:08
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

