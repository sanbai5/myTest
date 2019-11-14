package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeiXinPayController
 * @Description
 * @Author sanbai5
 * @Date 11:06 2019/10/13
 * @Version 2.1
 **/

@RestController
@RequestMapping("/weixin/pay")
public class WeiXinPayController {
    @Autowired
    private WeiXinPayService weiXinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    //此方法用来生成微信二维码
    @RequestMapping("/create/native")
    public Result creatNative(@RequestParam Map<String, String> parameter) {
        Map<String, String> map = weiXinPayService.creatNative(parameter);
        return new Result(true, StatusCode.OK, "生成支付二维码成功", map);
    }

    //此方法用于查询订单
    @RequestMapping("/status/query")
    public Result queryStatus(String outtradeno) {
        Map<String, String> map = weiXinPayService.queryPayStatus(outtradeno);
        return new Result(true, StatusCode.OK, "查询订单成功", map);
    }

    //此方法用于获取微信回调
    @RequestMapping("/notify/url")
    public String notifyUrl(HttpServletRequest request) throws Exception {
        ServletInputStream inputStream = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(b)) != -1) {
            byteArrayOutputStream.write(b, 0, len);
        }
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        inputStream.close();
        String a = new String(byteArrayOutputStream.toByteArray());
        Map<String, String> map = WXPayUtil.xmlToMap(a);
        String attach = map.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
        String exchange = attachMap.get("exchange");
        String rottingKey = attachMap.get("rottingKey");
        rabbitTemplate.convertAndSend(exchange,rottingKey,JSON.toJSONString(map));
        /*ServletInputStream inputStream = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        inputStream.close();
        String strXML = new String(byteArrayOutputStream.toByteArray(), "UTF-8");*/
        //Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        // 将支付结果发送到mq中
        //String attachStr = map.get("attach");
        //Map<String, String> attach = JSON.parseObject(attachStr, Map.class);
        //String exchange = env.getProperty(attach.get("exchange"));
        //String routingKey = env.getProperty(attach.get("routingKey"));
        //rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(map));   // 坑：需要将map转成json

        return "success";
    }
}
