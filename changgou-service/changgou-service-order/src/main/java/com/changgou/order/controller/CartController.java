package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.TokenCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CartController
 * @Description
 * @Author sanbai5
 * @Date 9:25 2019/10/10
 * @Version 2.1
 **/
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
    @Autowired(required = false)
    private CartService cartService;
    @Autowired
    private TokenDecode tokenDecode;

    @RequestMapping("/add")
    public Result add(Long id, Integer num) {
        String username = tokenDecode.getUserInfo().get("username");
        //String username = "wangwu";
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK, "添加购物车成功");
    }

    @RequestMapping("/list")
    public Result<List<OrderItem>> list() {
        String username = tokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.cartList(username);
        return new Result(true, StatusCode.OK, "获取购物车成功", orderItems);
    }
}
