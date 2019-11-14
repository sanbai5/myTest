package com.changgou.goods.feign;

import com.changgou.goods.pojo.Category;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName CategoryFeign
 * @Description
 * @Author 传智播客
 * @Date 19:03 2019/9/27
 * @Version 2.1
 **/
@FeignClient(name = "goods")
@RequestMapping("/category")
public interface CategoryFeign {

    /**
     * @author 栗子
     * @Description 获取商品分类
     * @Date 19:04 2019/9/27
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Category>
     **/
    @GetMapping("/{id}")
    Result<Category> findById(@PathVariable(value = "id") Integer id);
}
