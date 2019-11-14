package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * @return
     * @Author mqy
     * @Description 批量上架
     * @Date
     * @Param
     **/
    @GetMapping("/putmany/{ids}")
    public Result putMany(@PathVariable(value = "ids") Long[] ids) {
        spuService.putMany(ids);
        return new Result(true, StatusCode.OK, "批量上架成功");
    }
    /**
     * @return
     * @Author mqy
     * @Description 批量上架
     * @Date
     * @Param
     **/
    @GetMapping("/pullMany/{ids}")
    public Result pullMany(@PathVariable(value = "ids") Long[] ids) {
        spuService.pullMany(ids);
        return new Result(true, StatusCode.OK, "批量下架成功");
    }
    /**
     * @return
     * @Author mqy
     * @Description 恢复逻辑删除商品
     * @Date
     * @Param
     **/
    @GetMapping("/restore/{spuId}")
    public Result restore(@PathVariable(value = "spuId") Long spuId) {
        spuService.restore(spuId);
        return new Result(true, StatusCode.OK, "商品恢复成功");
    }

    /**
     * @return
     * @Author mqy
     * @Description 逻辑删除
     * @Date
     * @Param
     **/
    @GetMapping("/logicDelete/{spuId}")
    public Result logicDelete(@PathVariable(value = "spuId") Long spuId) {
        spuService.logicDelete(spuId);
        return new Result(true, StatusCode.OK, "商品逻辑删除成功");
    }

    /**
     * @return
     * @Author mqy
     * @Description 商品下架
     * @Date
     * @Param
     **/
    @GetMapping("/soldOut/{spuId}")
    public Result soldOut(@PathVariable(value = "spuId") Long spuId) {
        spuService.soldOut(spuId);
        return new Result(true, StatusCode.OK, "商品下架成功");
    }

    /**
     * @return
     * @Author mqy
     * @Description 商品上架
     * @Date
     * @Param
     **/
    @GetMapping("/pull/{spuId}")
    public Result pull(@PathVariable(value = "spuId") Long spuId) {
        spuService.pull(spuId);
        return new Result(true, StatusCode.OK, "商品上架成功");
    }

    /**
     * @return
     * @Author mqy
     * @Description 商品审核
     * @Date
     * @Param
     **/
    @GetMapping("/update/{spuId}")
    public Result audit(@PathVariable(value = "spuId") Long spuId) {
        spuService.audit(spuId);
        return new Result(true, StatusCode.OK, "审核通过");
    }

    /**
     * @return
     * @Author mqy
     * @Description 根据SpuId查询Goods
     * @Date
     * @Param
     **/
    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsBySupId(@PathVariable(value = "id") Long id) {
        Goods goods = spuService.findGoodsBySupId(id);
        return new Result<>(true, StatusCode.OK, "查询成功", goods);
    }

    /**
     * @return
     * @Author mqy
     * @Description 保存Goods
     * @Date
     * @Param
     **/
    @PostMapping("/save")
    public Result saveGoods(@RequestBody(required = false) Goods goods) {
        spuService.saveGoods(goods);
        return new Result(true, StatusCode.OK, "新增成功");
    }

    /***
     * Spu分页条件搜索实现
     * @param spu
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) Spu spu, @PathVariable int page, @PathVariable int size) {
        //调用SpuService实现分页条件查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * Spu分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用SpuService实现分页查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param spu
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<Spu>> findList(@RequestBody(required = false) Spu spu) {
        //调用SpuService实现条件查询Spu
        List<Spu> list = spuService.findList(spu);
        return new Result<List<Spu>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        //调用SpuService实现根据主键删除
        spuService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改Spu数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Spu spu, @PathVariable Long id) {
        //设置主键值
        spu.setId(id);
        //调用SpuService实现修改Spu
        spuService.update(spu);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增Spu数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu) {
        //调用SpuService实现添加Spu
        spuService.add(spu);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable Long id) {
        //调用SpuService实现根据主键查询Spu
        Spu spu = spuService.findById(id);
        return new Result<Spu>(true, StatusCode.OK, "查询成功", spu);
    }

    /***
     * 查询Spu全部数据
     * @return
     */
    @GetMapping
    public Result<List<Spu>> findAll() {
        //调用SpuService实现查询所有Spu
        List<Spu> list = spuService.findAll();
        return new Result<List<Spu>>(true, StatusCode.OK, "查询成功", list);
    }
}
