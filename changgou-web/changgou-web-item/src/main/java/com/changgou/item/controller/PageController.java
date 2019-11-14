package com.changgou.item.controller;

import com.changgou.item.service.PageService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName PageController
 * @Description
 * @Author sanbai5
 * @Date 11:21 2019/9/28
 * @Version 2.1
 **/
@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * @author 栗子
     * @Description 生成静态页
     * @Date 2:24 2019/8/19
     * @param id
     * @return entity.Result
     **/
    @GetMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(name = "id") Long id){
        pageService.createHtml(id);
        return new Result(true, StatusCode.OK,"ok");
    }
}
