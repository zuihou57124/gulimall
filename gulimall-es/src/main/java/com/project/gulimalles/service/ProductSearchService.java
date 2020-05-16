package com.project.gulimalles.service;

import com.project.gulimalles.vo.SearchParam;
import com.project.gulimalles.vo.SearchResp;

import java.io.IOException;

/**
 * @author qcw
 */
public interface ProductSearchService {

    SearchResp search(SearchParam param);
}
