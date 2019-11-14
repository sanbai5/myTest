package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName SpuFeign
 * @Description
 * @Author 传智播客
 * @Date 18:59 2019/9/27
 * @Version 2.1
 **/
@FeignClient(name = "goods")
@RequestMapping("/spu")
public interface SpuFeign {

    /**
     * @author 栗子
     * @Description 获取商品信息
     * @Date 19:00 2019/9/27
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Spu>
     **/
    @GetMapping("/{id}")
    Result<Spu> findById(@PathVariable(value = "id") Long id);
}
