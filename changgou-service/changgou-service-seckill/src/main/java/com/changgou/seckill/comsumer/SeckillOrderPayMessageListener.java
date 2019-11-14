package com.changgou.seckill.comsumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName SeckillOrderPayMessageListener
 * @Description
 * @Author sanbai5
 * @Date 18:16 2019/10/15
 * @Version 2.1
 **/
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderPayMessageListener {
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * @return
     * @Author mqy
     * @Description 监听mq
     * @Date
     * @Param
     **/
    @RabbitHandler
    public void readSeckillOrderMessage(String text) {
        Map<String, String> map = JSON.parseObject(text, Map.class);
        String return_code = map.get("return_code");
        if ("SUCCESS".equals(return_code)) {
            String attachStr = map.get("attach");
            Map<String, String> attach = JSON.parseObject(attachStr, Map.class);
            String username = attach.get("username");
            // 更新订单
            String out_trade_no = map.get("out_trade_no");//订单号
            String transaction_id = map.get("transaction_id");//流水号
            String time_end = map.get("time_end");//支付完成时间
            String result_code = map.get("result_code");
            if ("SUCCESS".equals(result_code)) {
                //表示支付成功
                seckillOrderService.updateStatus(username,out_trade_no,transaction_id,time_end);
            } else {
                //支付失败
                seckillOrderService.deleteOrder(username,out_trade_no,transaction_id,time_end);

            }
        }

    }
}
