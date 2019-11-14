package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description
 * @Author sanbai5
 * @Date 9:11 2019/10/10
 * @Version 2.1
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private SkuFeign skuFeign;
    @Autowired(required = false)
    private SpuFeign spuFeign;

    /**
     * @return
     * @Author mqy
     * @Description
     * @Date
     * @Param num:用于用户购买数量 id：库存id  username：当前用户
     **/
    @Override
    public void add(Integer num, Long id, String username) {
        if (num <= 0) {
            redisTemplate.boundHashOps("cart_" + username).delete(id);
            return;
        }
        OrderItem orderItem = new OrderItem();
        Result<Sku> result = skuFeign.findById(id);
        Sku sku = result.getData();
        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSkuId(sku.getId());
        orderItem.setSpuId(spu.getId());
        //orderItem.setOrderId("");
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(sku.getPrice() * num);
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight());
        orderItem.setPayMoney(sku.getPrice() * num);
        orderItem.setIsReturn("0");
        redisTemplate.boundHashOps("cart_" + username).put(id, orderItem);
    }

    @Override
    public List<OrderItem> cartList(String username) {
        List<OrderItem> list = redisTemplate.boundHashOps("cart_" + username).values();
        return list;
    }
}
