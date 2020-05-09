package com.project.gulimallware.ware.service.impl;

import com.project.gulimallware.ware.feign.SkuInfoFeignService;
import io.renren.common.to.SkuTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallware.ware.dao.WareSkuDao;
import com.project.gulimallware.ware.entity.WareSkuEntity;
import com.project.gulimallware.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    SkuInfoFeignService skuInfoFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.and((wrapper->{
                wrapper.eq("sku_id",skuId);
            }));
        }
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.and((wrapper->{
                wrapper.eq("ware_id",wareId);
            }));
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品入库
     */
    @Override
    public void addStock(WareSkuEntity wareSkuEntity) {
        //入库之前查询是否有此商品库存，没有则新建，有则更新
        WareSkuEntity wareSku = this.getOne(
                new QueryWrapper<WareSkuEntity>().eq("sku_id",wareSkuEntity.getSkuId())
                .eq("ware_id",wareSkuEntity.getWareId())
        );
        if(wareSku==null){
            //如果是第一次入库，则要远程获取sku_name,然后再保存
 /*           Map<String, SkuTo> r = skuInfoFeignService.skuInfo(wareSkuEntity.getSkuId());
            if(r.get("skuTo")==null){
                log.error("sku远程服务调用失败");
            }else {
                SkuTo skuTo = (SkuTo) r.get("skuTo");*/
                //wareSkuEntity.setSkuName(skuTo.getSkuName());
                this.save(wareSkuEntity);
           // }
        }
        else {
            wareSku.setStock(wareSku.getStock()+wareSkuEntity.getStock());
            this.updateById(wareSku);
        }


    }

}