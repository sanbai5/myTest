package com.changgou;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @ClassName WeiXinPayApplication
 * @Description
 * @Author sanbai5
 * @Date 10:42 2019/10/13
 * @Version 2.1
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class WeiXinPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeiXinPayApplication.class, args);
    }

    @Autowired
    private Environment env;

    // 创建队列
    @Bean
    public Queue orderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.order"), true);
    }

    // 创建交换机
    @Bean
    public Exchange orderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true, false);
    }

    // 将队列绑定到交换机
    @Bean
    public Binding queueBindToExchange(Queue orderQueue, Exchange orderExchange){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.key")).noargs();
    }

    // 创建队列
    @Bean
    public Queue SeckillOrderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"), true);
    }

    // 创建交换机
    @Bean
    public Exchange SeckillOrderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"), true, false);
    }

    // 将队列绑定到交换机
    @Bean
    public Binding SeckillQueueBindToExchange(Queue SeckillOrderQueue, Exchange SeckillOrderExchange){
        return BindingBuilder.bind(SeckillOrderQueue).to(SeckillOrderExchange).with(env.getProperty("mq.pay.routing.seckillkey")).noargs();
    }
}
