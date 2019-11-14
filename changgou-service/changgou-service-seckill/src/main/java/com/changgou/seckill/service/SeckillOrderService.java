package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService {
    /**
     * @return
     * @Author mqy
     * @Description 表示支付成功更新订单
     * @Date
     * @Param
     **/
    void updateStatus(String username, String out_trade_no, String transaction_id, String time_end);

    /**
     * @return
     * @Author mqy
     * @Description 支付失败删除订单
     * @Date
     * @Param
     **/

    void deleteOrder(String username, String out_trade_no, String transaction_id, String time_end);

    /**
     * @return
     * @Author mqy
     * @Description 查询抢购的订单状态
     * @Date
     * @Param
     **/
    SeckillStatus queryStatus(String username);

    /**
     * @return
     * @Author mqy
     * @Description 下单操作
     * @Date
     * @Param
     **/
    Boolean add(String time, Long seckillId, String userId);

    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();



}
