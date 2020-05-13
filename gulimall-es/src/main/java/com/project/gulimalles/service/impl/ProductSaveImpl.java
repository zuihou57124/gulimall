package com.project.gulimalles.service.impl;

import com.alibaba.fastjson.JSON;
import com.project.gulimalles.config.ElasticsearchConfig;
import com.project.gulimalles.constant.EsConst;
import com.project.gulimalles.service.ProductSaveService;
import io.renren.common.to.es.SkuEsModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qcw
 */
@Service
public class ProductSaveImpl implements ProductSaveService {

    @Autowired(required = false)
    RestHighLevelClient client;

    @Override
    public Boolean productSave(List<SkuEsModel> skuEsModels) throws Exception {

        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造批量保存请求
            IndexRequest indexRequest = new IndexRequest(EsConst.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        //是否出错
        boolean b = bulk.hasFailures();
        if(b){
            System.out.println(bulk.buildFailureMessage());
        }else {
            List<String> collect = Arrays.stream(bulk.getItems()).map((BulkItemResponse::getId)).collect(Collectors.toList());
            System.out.println("商品上架成功:---- "+collect);
        }
        return b;
    }
}
