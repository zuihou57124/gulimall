package com.project.gulimallproduct.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.project.gulimallproduct.product.entity.ProductAttrValueEntity;
import com.project.gulimallproduct.product.service.ProductAttrValueService;
import com.project.gulimallproduct.product.vo.AttrRespVo;
import com.project.gulimallproduct.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.service.AttrService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 商品属性
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 规格参数列表
     */
    /*@RequestMapping("/base/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable ("catelogId") Long catelogId)
    {
        PageUtils page = attrService.baseQueryPage(params,catelogId);

        return R.ok().put("page", page);
    }
*/
    /**
     * 属性列表
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R saleList(@RequestParam Map<String, Object> params,
                      @PathVariable ("catelogId") Long catelogId,
                      @PathVariable ("attrType") String attrType)
    {
        PageUtils page = attrService.baseQueryPage(params, catelogId,attrType);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取spu的基本属性列表
     */
    @RequestMapping("/base/listforspu/{spuid}")
    //@RequiresPermissions("product:attr:list")
    public R baseAttrforSpu(@PathVariable("spuid") Long spuId){
        List<ProductAttrValueEntity> list = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", list);
    }

    /**
     * 更新spu的基本属性列表
     */
    @RequestMapping("/update/{spuid}")
    //@RequiresPermissions("product:attr:list")
    public R updateAttrforSpu(@PathVariable("spuid") Long spuId,@RequestBody List<ProductAttrValueEntity> list){
        productAttrValueService.updateAttrListForSpu(spuId,list);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
		AttrRespVo attrResp = attrService.getAttrVo(attrId);
        return R.ok().put("attr", attrResp);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		//attrService.save(attr);
        attrService.saveAttr(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		//attrService.updateById(attr);
        attrService.updateAttr(attrVo);
        return R.ok();
    }

    /**
     * 删除一条属性时，应该同时删除与属性分组的关联信息
     */
    @PostMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		//attrService.removeByIds(Arrays.asList(attrIds));
        attrService.removeAttr(Arrays.asList(attrIds));
        return R.ok();
    }

}
