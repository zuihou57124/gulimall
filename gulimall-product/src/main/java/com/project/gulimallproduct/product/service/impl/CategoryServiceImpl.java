package com.project.gulimallproduct.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.project.gulimallproduct.product.dao.CategoryBrandRelationDao;
import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;
import com.project.gulimallproduct.product.service.CategoryBrandRelationService;
import com.project.gulimallproduct.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author qcw
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Autowired(required = false)
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> allCategory = categoryDao.selectList(null);

        List<CategoryEntity> categoryEntityList = categoryDao.selectList(null);

        categoryEntityList = categoryEntityList.stream()
           .filter(category->category.getParentCid()==0)
           .map(categoryEntity ->{
               categoryEntity.setChildCate(getChild(categoryEntity,allCategory));
               return categoryEntity;
           })
           .sorted((o1,o2)->o1.getSort()-o2.getSort())
           .collect(Collectors.toList());
        return categoryEntityList;
    }


    @Transactional(rollbackFor = {})
    @Override
    @Caching(evict = {
            @CacheEvict(value = "category",key = "'getCatelog2Json'"),
            @CacheEvict(value = "category",key = "'getCategorys1'")
    })
    public void updateDetail(CategoryEntity category) {

        this.updateById(category);
        //分类名不为空说明分类名也要修改，其他关联表也要同步修改
        if(!StringUtils.isEmpty(category.getName())){
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setCatelogId(category.getCatId());
            categoryBrandRelationEntity.setCatelogName(category.getName());
            categoryBrandRelationDao.update(categoryBrandRelationEntity
                    ,new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id",category.getCatId())
            );
        }

    }


    @Override
    @Cacheable(value = {"category"},key = "#root.method.name")
    public Map<String, List<Catelog2Vo>> getCatelog2Json() {

        //缓存穿透：空结果缓存，并设置失效时间
        //缓存雪崩：设置不同的随机时间值
        //缓存击穿：加锁

        Map<String, List<Catelog2Vo>> catelog2JsonMap = new HashMap<>();
        catelog2JsonMap = getCategoryDataListMapFromDb();
        /*String catelog2Json = stringRedisTemplate.opsForValue().get("catelog2Json");
        if(StringUtils.isEmpty(catelog2Json)){
            System.out.println("缓存未命中，即将查询数据库");
            catelog2JsonMap = getCatelog2JsonWithRedissonLock();
        }else {
            System.out.println("缓存命中");
            catelog2JsonMap = JSON.parseObject(catelog2Json,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        }*/
        return catelog2JsonMap;
    }


    /**
     * 获取一级分类菜单
     */

    @Override
    @Cacheable(value = {"category"},key = "#root.method.name")
    public List<CategoryEntity> getCategorys1() {

        List<CategoryEntity> cat_level1 = this.list(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));

        return cat_level1;
    }

    //使用redisson加锁
    public Map<String, List<Catelog2Vo>> getCatelog2JsonWithRedissonLock() {

        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        System.out.println("线程:--"+Thread.currentThread().getId()+"加锁成功");
        Map<String, List<Catelog2Vo>> catelogListMap = getCategoryDataListMapFromDb();
        System.out.println("查询了数据库");
        //设置缓存失效的随机值，防止所有缓存在同一时间失效
        stringRedisTemplate.opsForValue().set("catelog2Json", JSON.toJSONString(catelogListMap),new Random().nextInt(60)+60, TimeUnit.SECONDS);
        lock.unlock();
        System.out.println("线程:--"+Thread.currentThread().getId()+"释放锁");
        return catelogListMap;
    }

    //使用原生redis加锁
    public Map<String, List<Catelog2Vo>> getCatelog2JsonWithLock() {
        //每个进程的锁都应该有一个唯一标识，防止时间自动过期后，删除其他进程的锁
        String uuid = UUID.randomUUID().toString();
        //从redis获得锁,同时设置锁的过期时间,防止死锁
        Boolean hasLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,30,TimeUnit.SECONDS);
        if(hasLock!=null && hasLock){
            //如果获得锁，执行业务逻辑
            Map<String, List<Catelog2Vo>> catelogListMap = getCategoryDataListMapFromDb();
            //设置缓存失效的随机值，防止所有缓存在同一时间失效
            //在数据库查询完成以后，立即将结果进行缓存，保证原子性操作
            stringRedisTemplate.opsForValue().set("catelog2Json", JSON.toJSONString(catelogListMap),new Random().nextInt(10), TimeUnit.HOURS);
            //释放锁,如果是自己的锁，释放
                       /*if(uuid.equals(stringRedisTemplate.opsForValue().get("lock"))){
                stringRedisTemplate.delete("lock");
            }*/

            String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            //删除和判断应该是原子性的
            stringRedisTemplate.execute(new DefaultRedisScript<Long>(luaScript, Long.class),Arrays.asList("lock"),uuid);

            return catelogListMap;
        }
        else {
           //如果没有获得锁，自旋等待，直到获得锁
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelog2JsonWithLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getCategoryDataListMapFromDb() {
        //直接查询所有分类，然后从流中筛选，避免频繁操作数据库
        List<CategoryEntity> allCategoryList = this.list(null);
        List<CategoryEntity> category1List = getChilds(allCategoryList,0L);
        return category1List.stream().collect(Collectors.toMap((categoryEntity -> categoryEntity.getCatId().toString()), (categoryEntity -> {
            //设置二级分类
            List<CategoryEntity> catelog2List = getChilds(allCategoryList,categoryEntity.getCatId());
            List<Catelog2Vo> catelog2VoList = null;
            if (catelog2List != null) {
                catelog2VoList = catelog2List.stream().map((item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setCatelog1Id(categoryEntity.getCatId().toString());
                    catelog2Vo.setId(item.getCatId().toString());
                    catelog2Vo.setName(item.getName());
                    //设置三级分类
                    List<CategoryEntity> category3List = getChilds(allCategoryList,item.getCatId());
                    List<Catelog2Vo.Catalog3> catelog3List = null;
                    if (category3List != null) {
                        catelog3List = category3List.stream().map((category3 -> {
                            Catelog2Vo.Catalog3 catelog3 = new Catelog2Vo.Catalog3();
                            catelog3.setCatalog2Id(item.getCatId().toString());
                            catelog3.setId(category3.getCatId().toString());
                            catelog3.setName(category3.getName());
                            return catelog3;
                        })).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3List);

                    return catelog2Vo;
                })).collect(Collectors.toList());
            }
            return catelog2VoList;
        })));
    }

    //获取所有子分类
    private List<CategoryEntity> getChilds(List<CategoryEntity> list,Long parent_cid) {
        list = list.stream().filter((item-> item.getParentCid()==parent_cid)).collect(Collectors.toList());
        return list;
    }


    /**
     * @return 返回菜单的所有子菜单
     */
    public List<CategoryEntity> getChild(CategoryEntity root,List<CategoryEntity> allCate){

          List<CategoryEntity> child = allCate.stream()
                .filter(categoryEntity ->
                categoryEntity.getParentCid()==root.getCatId())
                .map(categoryEntity -> {
                     categoryEntity.setChildCate(getChild(categoryEntity,allCate));
                     return categoryEntity;
                })
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
          return child;
    }

}