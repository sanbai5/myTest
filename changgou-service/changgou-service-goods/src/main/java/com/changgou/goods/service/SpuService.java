package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {
    /**
     * @return
     * @Author mqy
     * @Description 批量上架
     * @Date
     * @Param
     **/
    void pullMany(Long[] ids);
    /**
     * @return
     * @Author mqy
     * @Description 批量上架
     * @Date
     * @Param
     **/
    void putMany(Long[] ids);

    /**
     * @return
     * @Author mqy
     * @Description 恢复逻辑删除商品
     * @Date
     * @Param
     **/
    void restore(Long spuId);

    /**
     * @return
     * @Author mqy
     * @Description 逻辑删除
     * @Date
     * @Param
     **/
    void logicDelete(Long spuId);

    /**
     * @return
     * @Author mqy
     * @Description 商品下架
     * @Date
     * @Param
     **/
    void soldOut(Long spuId);

    /**
     * @return
     * @Author mqy
     * @Description 商品上架
     * @Date
     * @Param
     **/
    void pull(Long spuId);

    /**
     * @return
     * @Author mqy
     * @Description 商品审核
     * @Date
     * @Param
     **/
    void audit(Long spuId);

    /**
     * @return
     * @Author mqy
     * @Description 根据SpuId查询Goods
     * @Date
     * @Param
     **/
    Goods findGoodsBySupId(Long supId);

    /**
     * @return
     * @Author mqy
     * @Description 保存Goods
     * @Date
     * @Param
     **/
    void saveGoods(Goods goods);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();

}
