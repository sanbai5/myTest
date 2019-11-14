package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SkuFeign
 * @Description
 * @Author 传智播客
 * @Date 11:14 2019/9/24
 * @Version 2.1
 **/
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {


    /**
     * @return
     * @Author mqy
     * @Description 库存扣减
     * @Date
     * @Param
     **/
    @GetMapping("/decr/{username}")
    Result decr(@RequestParam(value = "username", required = false) String username);
    /**
     * @author 栗子
     * @Description 查询正常状态的库存信息
     * @Date 11:12 2019/9/24
     * @param status
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Sku>>
     **/
    @GetMapping("/findListByStatus/{status}")
    Result<List<Sku>> findListByStatus(@PathVariable(value = "status") String status);

    /**
     * @author 栗子
     * @Description 通过spu获取对应的库存信息
     * @Date 19:02 2019/9/27
     * @param sku
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Sku>>
     **/
    @PostMapping(value = "/search" )
    Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);
    /**
     * @author 栗子
     * @Description 获取库存信息
     * @Date 13:29 2019/8/23
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Sku>
     **/
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable(value = "id", required = true) Long id);
}
