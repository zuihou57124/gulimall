package com.project.gulimallproduct.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.SpuImagesDao;
import com.project.gulimallproduct.product.entity.SpuImagesEntity;
import com.project.gulimallproduct.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 保存spu图片集
     * @param id spuId
     * @param spuImages 图片集
     */
    @Override
    public void saveImages(Long id, List<String> spuImages) {

            List<SpuImagesEntity> spuInfoImages = spuImages.stream().map((image -> {
                SpuImagesEntity spuImage = new SpuImagesEntity();
                spuImage.setSpuId(id);
                spuImage.setImgUrl(image);
                return spuImage;
            })).collect(Collectors.toList());

            this.saveBatch(spuInfoImages);
    }
}