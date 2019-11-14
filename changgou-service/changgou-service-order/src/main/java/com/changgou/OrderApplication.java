package com.changgou;

import entity.IdWorker;
import entity.TokenDecode;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName OrderApplication
 * @Description
 * @Author sanbai5
 * @Date 17:24 2019/10/9
 * @Version 2.1
 **/
@SpringBootApplication//springBoot的启动注解
@EnableEurekaClient//交给注册中心管理
@MapperScan(basePackages = {"com.changgou.order.dao"})//扫描的包
@EnableFeignClients(basePackages = {"com.changgou.goods.feign"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public TokenDecode tokenDecode() {
        return new TokenDecode();
    }

    /**
     * @return
     * @Author mqy
     * @Description 生成订单id
     * @Date
     * @Param
     **/

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1);
    }

    @Autowired
    private Environment env;

    // 创建队列
    @Bean
    public Queue orderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.order"), true);
    }

   /* // 创建交换机
    @Bean
    public Exchange orderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true, false);
    }

    // 将队列绑定到交换机
    @Bean
    public Binding queueBindToExchange(Queue orderQueue, Exchange orderExchange){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.key")).noargs();
    }*/
}
