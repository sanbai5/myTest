package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @Author mqy
 * @Description 用于添加购物车
 * @Date
 * @Param
 * @return
 **/
public interface CartService {
    /**
     * @return
     * @Author mqy
     * @Description
     * @Date
     * @Param num:用于用户购买数量 id：库存id  username：当前用户
     **/
    void add(Integer num, Long id, String username);

    /**
     * @return
     * @Author mqy
     * @Description 获取购物车列表
     * @Date
     * @Param
     **/
    List<OrderItem> cartList(String username);
}
