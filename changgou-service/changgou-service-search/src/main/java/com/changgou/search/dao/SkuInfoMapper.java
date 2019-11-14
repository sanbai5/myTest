package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName SkuInfoMapper
 * @Description
 * @Author sanbai5
 * @Date 17:19 2019/9/24
 * @Version 2.1
 **/
public interface SkuInfoMapper extends ElasticsearchRepository<SkuInfo, Integer> {
}
