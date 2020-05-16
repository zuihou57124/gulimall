package com.project.gulimalles.service.impl;

import com.project.gulimalles.GulimallEsApplication;
import com.project.gulimalles.config.ElasticsearchConfig;
import com.project.gulimalles.constant.EsConst;
import com.project.gulimalles.service.ProductSearchService;
import com.project.gulimalles.vo.SearchParam;
import com.project.gulimalles.vo.SearchResp;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.InternalOrder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author qcw
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public SearchResp search(SearchParam param) {

        SearchResp result = null;
        //构建出DSL语句
        SearchRequest searchRequest = buildSearchRequest(param);

        try {
            SearchResponse response = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            //封装响应数据
            result = buildSearchResult(response);
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
            highlight.preTags("<b style='color:red'");
            highlight.postTags("/b>");
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
                nestedBooleanQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs",nestedBooleanQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }

        //filter查询(是否有库存)
        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));

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
    private SearchResp buildSearchResult(SearchResponse response) {

        return null;
    }

}
