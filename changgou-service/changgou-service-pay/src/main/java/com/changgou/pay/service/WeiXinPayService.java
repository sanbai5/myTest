package com.changgou.pay.service;

import java.util.Map;

/**
 * @ClassName WeiXinPayService
 * @Description
 * @Author sanbai5
 * @Date 10:45 2019/10/13
 * @Version 2.1
 **/
public interface WeiXinPayService {
    /**
     * @return
     * @Author mqy
     * @Description 用于生成二维码
     * @Date out_trade_no:订单号  total_fee：支付金额
     * @Param
     **/
    Map<String, String> creatNative(Map<String , String> parameter);

    /**
     * @return
     * @Author mqy
     * @Description 查询支付状态
     * @Date
     * @Param
     **/
    Map<String, String> queryPayStatus(String out_trade_no);
}
