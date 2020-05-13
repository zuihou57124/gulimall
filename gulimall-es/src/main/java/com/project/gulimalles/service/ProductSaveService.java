package com.project.gulimalles.service;


import io.renren.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author qcw
 */
public interface ProductSaveService {

    Boolean productSave(List<SkuEsModel> skuEsModels) throws Exception;

}
