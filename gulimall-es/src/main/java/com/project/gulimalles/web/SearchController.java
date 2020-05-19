package com.project.gulimalles.web;

import com.project.gulimalles.service.ProductSearchService;
import com.project.gulimalles.vo.SearchParam;
import com.project.gulimalles.vo.SearchResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qcw
 */
@Controller
public class SearchController {

    @Autowired
    ProductSearchService productSearchService;

    @RequestMapping("/list.html")
    public String list(SearchParam param, Model model, HttpServletRequest httpServletRequest){
        param.setQueryString(httpServletRequest.getQueryString());
        SearchResp result = productSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }

}
