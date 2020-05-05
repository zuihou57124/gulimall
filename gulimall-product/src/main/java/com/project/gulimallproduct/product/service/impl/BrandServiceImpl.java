package com.project.gulimallproduct.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;
import com.project.gulimallproduct.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.BrandDao;
import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author qcw
 */
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired(required = false)
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key = (String) params.get("key");
        if(StringUtils.isEmpty(key)){
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );
            return new PageUtils(page);
        }
        else {
            QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<BrandEntity>();
            queryWrapper.and((obj)->{
                obj.eq("brand_id",key)
                        .or()
                        .like("name",key);
            });
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    queryWrapper
            );

            return new PageUtils(page);
        }
    }

    @Transactional
    @Override
    public void updateRelation(BrandEntity brand) {
        this.updateById(brand);
        //品牌不为空说明品牌名也要修改，其他关联表也要同步修改
        if(!StringUtils.isEmpty(brand.getName())){
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandId(brand.getBrandId());
            categoryBrandRelationEntity.setBrandName(brand.getName());
            categoryBrandRelationService.update(categoryBrandRelationEntity
                    ,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brand.getBrandId())
            );
        }

    }

}