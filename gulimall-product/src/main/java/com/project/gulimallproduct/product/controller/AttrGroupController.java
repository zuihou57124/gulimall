package com.project.gulimallproduct.product.controller;

import java.util.*;

import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import com.project.gulimallproduct.product.vo.AttrGroupRelationVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallproduct.product.entity.AttrGroupEntity;
import com.project.gulimallproduct.product.service.AttrGroupService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import sun.plugin.javascript.navig.Array;


/**
 * 属性分组
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 获取属性分组关联的属性列表
     */
    @RequestMapping("/{attrgroupId}/attr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public R relationAttrList(@RequestParam Map<String, Object> params,
                              @PathVariable("attrgroupId") Long attrgroupId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.getAttrRelation(params,attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 获取未关联的的属性列表
     */
    @RequestMapping("/{attrgroupId}/noattr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public R noRelationAttrList(@RequestParam Map<String, Object> params,
                              @PathVariable("attrgroupId") Long attrgroupId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.getAttrNoRelation(params,attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long catelogId){
        //params.put("catlogId",catlogId);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        List<Long> path = new ArrayList<>();
        CategoryEntity categoryEntity = null;
        categoryEntity = categoryService.getById(attrGroup.getCatelogId());
        path.add(categoryEntity.getCatId());
        categoryEntity = categoryService.getById(categoryEntity.getParentCid());
        path.add(categoryEntity.getCatId());
        categoryEntity = categoryService.getById(categoryEntity.getParentCid());
        path.add(categoryEntity.getCatId());

        Collections.reverse(path);

		attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除属性关联，不会删除属性
     */
    @PostMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody AttrGroupRelationVo[] vos){
		//attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        attrGroupService.removeAttrRelation(vos);
        return R.ok();
    }

}
