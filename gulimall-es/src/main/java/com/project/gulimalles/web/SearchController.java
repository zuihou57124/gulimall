package com.project.gulimalles.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qcw
 */
@Controller
public class SearchController {

    @RequestMapping("/list.html")
    public String list(){
        return "list";
    }

}
