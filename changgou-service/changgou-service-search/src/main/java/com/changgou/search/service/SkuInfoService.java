package com.changgou.search.service;


import java.util.Map;

public interface SkuInfoService {

    /**
     * @return
     * @Author mqy
     * @Description 将数据存入索引库中
     * @Date
     * @Param
     **/
    void importSkuInfoToEs();

    /**
     * @return
     * @Author mqy
     * @Description 检索
     * @Date
     * @Param
     **/
    Map<String, Object> search(Map<String, String> searchMap);
}
