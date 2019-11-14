package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName OrderPayMessageListener
 * @Description
 * @Author sanbai5
 * @Date 21:06 2019/10/13
 * @Version 2.1
 **/
@Component
@RabbitListener(queues = {"${mq.pay.queue.order}"})
public class OrderPayMessageListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void readMessage(String msg){
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        // 通信标识
        String return_code = map.get("return_code");
        if ("SUCCESS".equals(return_code)){
            // 业务结果
            String result_code = map.get("result_code");
            String out_trade_no = map.get("out_trade_no"); // 商户订单号
            if ("SUCCESS".equals(result_code)){
                // 更新订单
                if (out_trade_no != null){
                    orderService.updateStatus(out_trade_no, map.get("transaction_id"));
                }
            }else {
                // 删除订单
                if (out_trade_no != null){
                    orderService.deleteOrder(out_trade_no);
                }
            }
        }
    }
}
