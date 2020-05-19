package com.project.gulimalles.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.project.gulimalles.config.ElasticsearchConfig;
import com.project.gulimalles.constant.EsConst;
import com.project.gulimalles.feign.ProductServiceFeign;
import com.project.gulimalles.service.ProductSearchService;
import com.project.gulimalles.vo.AttrRespVo;
import com.project.gulimalles.vo.BrandVo;
import com.project.gulimalles.vo.SearchParam;
import com.project.gulimalles.vo.SearchResp;
import io.renren.common.to.es.SkuEsModel;
import io.renren.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qcw
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ProductServiceFeign productServiceFeign;

    @Override
    public SearchResp search(SearchParam param) {

        SearchResp result = null;
        //构建出DSL语句
        SearchRequest searchRequest = buildSearchRequest(param);

        try {
            SearchResponse response = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            //封装响应数据
            result = buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 构建检索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        //构建DSL语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //must模糊匹配
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
            //按照关键字模糊查询高亮显示
            HighlightBuilder highlight = new HighlightBuilder();
            highlight.field("skuTitle");
            highlight.preTags("<b style='color:red'>");
            highlight.postTags("</b>");
            searchSourceBuilder.highlighter(highlight);
        }
        //filter查询(三级分类id)
        if(param.getCatelog3Id()!=null)
        {
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatelog3Id()));
        }
        //filter查询(品牌id(可能有多个))
        if(param.getBrandId()!=null && param.getBrandId().size()>0)
        {
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //filter查询(按照属性查询)
        if(param.getAttrs()!=null && param.getAttrs().size()>0){
            // attr=1_5.5寸:5寸&&attr=1_麒麟990:晓龙865
            //属性条件可能有多个,分割每个检索的属性条件
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestedBooleanQuery = QueryBuilders.boolQuery();
                //每个属性的属性值可能会有多个，分割属性值
                String[] s = attr.split("_");
                //属性id
                String attrId = s[0];
                //属性值
                String[] attrValues = s[1].split(":");
                nestedBooleanQuery.must(QueryBuilders.termsQuery("attrs.attrId",attrId));
                nestedBooleanQuery.must(QueryBuilders.termsQuery("attrs.attrValue.keyword",attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs",nestedBooleanQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }

        //filter查询(是否有库存)
        if(param.getHasStock()!=null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }

        //filter查询(价格区间)
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] skuPrice = param.getSkuPrice().split("_");
            if(skuPrice.length==2){
                rangeQuery.gte(skuPrice[0]).lte(skuPrice[1]);
            }else if (skuPrice.length==1){
                    if (param.getSkuPrice().startsWith("_")){
                        rangeQuery.lte(skuPrice[0]);
                    } else {
                        rangeQuery.gte(skuPrice[0]);
                    }
                }

            boolQuery.filter(rangeQuery);
        }
        searchSourceBuilder.query(boolQuery);

        //排序
        //sort=hotScore_asc/desc
        if(!StringUtils.isEmpty(param.getSort())){
            String[] sort = param.getSort().split("_");
            searchSourceBuilder.sort(sort[0],"asc".equals(sort[1])?SortOrder.ASC:SortOrder.DESC);
        }
        //分页
        searchSourceBuilder.from((param.getPageNum()-1)*EsConst.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConst.PRODUCT_PAGESIZE);

        //聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        //品牌聚合
        brand_agg.field("brandId").size(10);
        //子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        //分类聚合
        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catalog_agg");
        catelog_agg.field("catalogId").size(10);
        //子聚合
        catelog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catelogName.keyword").size(1));
        searchSourceBuilder.aggregation(catelog_agg);

        //属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(5);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue.keyword").size(5));
        attr_agg.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(attr_agg);


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConst.PRODUCT_INDEX}, searchSourceBuilder);
        String s = searchSourceBuilder.toString();
        System.out.println("DSL语句:--"+s);
        return searchRequest;
    }

    /**
     * 封装响应结果
     */
    private SearchResp buildSearchResult(SearchResponse response,SearchParam param) {

        SearchResp searchResp = new SearchResp();
        //封装总记录数,总页码,当前页码
        searchResp.setTotal(response.getHits().getTotalHits().value);
        long totalPage = searchResp.getTotal() % EsConst.PRODUCT_PAGESIZE;
        if(totalPage==0){
            totalPage = searchResp.getTotal() / EsConst.PRODUCT_PAGESIZE;
        }else {
            totalPage = searchResp.getTotal() / EsConst.PRODUCT_PAGESIZE+1;
        }
        searchResp.setTotalPages(totalPage);
        searchResp.setPageNum(param.getPageNum());
        searchResp.setPageNavs();

        SearchHits hits = response.getHits();
        List<SkuEsModel> skuEsModelList = new ArrayList<>();
        //封装商品信息
        if(hits.getHits()!=null && hits.getHits().length>0){
            for (SearchHit hit : hits.getHits()) {
                String sourceString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceString, SkuEsModel.class);
                if(!StringUtils.isEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    skuEsModel.setSkuTitle(skuTitle.getFragments()[0].string());
                }
                skuEsModelList.add(skuEsModel);
            }
        }
        searchResp.setSkuEsModels(skuEsModelList);

        //封装聚合信息
        //封装分类
        Aggregations aggregations = response.getAggregations();
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<SearchResp.CatalogVo> catalogVoList = new ArrayList<>();
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResp.CatalogVo catalogVo = new SearchResp.CatalogVo();
            String keyString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyString));
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatelogName(catalogName);
            catalogVoList.add(catalogVo);
        }
        searchResp.setCatalogs(catalogVoList);

        //封装品牌
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<SearchResp.BrandVo> brandVoList = new ArrayList<>();
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResp.BrandVo brandVo = new SearchResp.BrandVo();
            //品牌id
            String keyString = bucket.getKeyAsString();
            brandVo.setBrandId(Long.parseLong(keyString));
            //品牌名称
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //品牌logo
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            brandVoList.add(brandVo);
        }
        searchResp.setBrands(brandVoList);

        //封装属性
        List<SearchResp.AttrVo> attrsList = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResp.AttrVo attrVo = new SearchResp.AttrVo();
            //封装属性id
            String attr_id = bucket.getKeyAsString();
            attrVo.setAttrId(Long.parseLong(attr_id));
            //封装属性名称
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            //封装属性值
            List<String> attrValueList = new ArrayList<>();
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            for (Terms.Bucket attr_value_aggBucket : attr_value_agg.getBuckets()) {
                String attrVlue = attr_value_aggBucket.getKeyAsString();
                attrValueList.add(attrVlue);
            }
            attrVo.setAttrValue(attrValueList);

            attrsList.add(attrVo);
        }
        searchResp.setAttrs(attrsList);

        //封装属性面包屑导航
        if(param.getAttrs()!=null && param.getAttrs().size()>0){
            List<SearchResp.NavVo> navList = param.getAttrs().stream().map((attr -> {
                SearchResp.NavVo navVo = new SearchResp.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productServiceFeign.getAttrInfo(Long.parseLong(s[0]));
                //取得被筛选的属性id
                searchResp.getSelectedAttrs().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrRespVo attrRespVo = r.getData("attr", new TypeReference<AttrRespVo>() {
                    });
                    navVo.setNavName(attrRespVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                String replaceUrl = replaceQueryString(param,"attrs",attr);
                if(StringUtils.isEmpty(replaceUrl)){
                    navVo.setLink("http://127.0.0.2/list.html");
                }else {
                    navVo.setLink("http://127.0.0.2/list.html?"+replaceUrl);
                }

                return navVo;
            })).collect(Collectors.toList());
            searchResp.setNavVoList(navList);
        }

        //封装品牌分类等面包屑导航
        if(param.getBrandId()!=null && param.getBrandId().size()>0){
            List<SearchResp.NavVo> navVoList = searchResp.getNavVoList();
            SearchResp.NavVo navVo = new SearchResp.NavVo();
            navVo.setNavName("品牌");
            R r = productServiceFeign.getBrandInfos(param.getBrandId());
            if(r.getCode()==0){
                List<BrandVo> brands = r.getData("brands", new TypeReference<List<BrandVo>>() {});
                StringBuilder buffer = new StringBuilder();
                String replace = "";
                for (BrandVo brand : brands) {
                    buffer.append(brand.getName()).append(";");
                    replace = replaceQueryString(param, "brandId", brand.getBrandId().toString());
                }
                navVo.setNavValue(buffer.toString());
                if(StringUtils.isEmpty(replace)){
                    navVo.setLink("http://127.0.0.2/list.html");
                }else {
                    navVo.setLink("http://127.0.0.2/list.html?"+replace);
                }
            }
            navVoList.add(navVo);
            searchResp.setNavVoList(navVoList);
        }
        return searchResp;
    }

    private String replaceQueryString(SearchParam param, String where,String what) {
        String encode = null;
        try {
            encode = URLEncoder.encode(what, "UTF-8");
            encode = encode.replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replaceUrl = param.getQueryString().replace("&" + where + "=" + encode, "");
        replaceUrl = replaceUrl.replace(where + "=" + encode, "");

        return replaceUrl;
    }

}
