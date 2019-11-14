package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeiXinPayServiceImpl
 * @Description
 * @Author sanbai5
 * @Date 10:48 2019/10/13
 * @Version 2.1
 **/
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    /**
     * @return
     * @Author mqy
     * @Description 用于生成二维码
     * @Date out_trade_no:订单号  total_fee：支付金额
     * @Param
     **/
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    //weixin:
    //  appid: wx8397f8696b538317
    //  partner: 1473426802
    //  partnerkey: T6m9iK73b0kn9g5v426MKfHQH7X8rKwb
    //  notifyurl: http://www.itcast.cn
    @Override
    public Map<String, String> creatNative(Map<String, String> parameter) {
        try {
            //统一下单地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            String out_trade_no = parameter.get("out_trade_no");
            String total_fee = parameter.get("total_fee");
            //封装集合
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);                               // 微信公众账号或开放平台APP的唯一标识
            data.put("mch_id", partner);                            // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());    // 随机字符串
            data.put("body", "畅购商城");                            // 商品描述

            data.put("out_trade_no", out_trade_no);                 // 商户订单号
            data.put("total_fee", total_fee);                       // 商品支付金额
            data.put("spbill_create_ip", "127.0.0.1");              // 终端ip
            data.put("notify_url", notifyurl);                      // 回调地址
            data.put("trade_type", "NATIVE");                       // 支付类
            String username = parameter.get("username");
            String exchange = parameter.get("exchange");
            String routingKey = parameter.get("routingKey");
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("username", username);
            attachMap.put("exchange", exchange);
            attachMap.put("routingKey", routingKey);
            data.put("attach", JSON.toJSONString(attachMap));
            //将数据转换成XML
            String dataXml = WXPayUtil.generateSignedXml(data, partnerkey);
            //创建httpclient发请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(dataXml);
            httpClient.post();
            //处理响应数据
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     * @Author mqy
     * @Description 查询支付状态
     * @Date
     * @Param
     **/
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        try {
            //统一下单地址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);                               // 微信公众账号或开放平台APP的唯一标识
            data.put("mch_id", partner);                            // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());    // 随机字符串
            data.put("out_trade_no", out_trade_no);                 // 商户订单号
            String dataXml = WXPayUtil.generateSignedXml(data, partnerkey);
            //创建httpclient发请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(dataXml);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
