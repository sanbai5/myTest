package com.changgou.search.controller;

import com.changgou.search.service.SkuInfoService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName SkuInfoController
 * @Description
 * @Author sanbai5
 * @Date 17:42 2019/9/24
 * @Version 2.1
 **/
@RestController
@RequestMapping("/search")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * @return
     * @Author mqy
     * @Description 检索
     * @Date
     * @Param
     **/
    @GetMapping
    public Map<String, Object> search(@RequestParam(required = false) Map<String, String> searchMap) {
        Map<String, Object> search = skuInfoService.search(searchMap);
        return search;
    }

    /**
     * @return
     * @Author mqy
     * @Description 将数据存入索引库中
     * @Date
     * @Param
     **/
    @GetMapping("/import")
    public Result importSkuInfoToEs() {
        skuInfoService.importSkuInfoToEs();
        return new Result(true, StatusCode.OK, "插入数据成功");
    }
}
