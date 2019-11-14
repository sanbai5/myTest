package com.changgou;

import entity.TokenDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName UserApplication
 * @Description
 * @Author sanbai5
 * @Date 20:22 2019/9/28
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.user.dao"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    public TokenDecode tokenDecode() {
        return new TokenDecode();
    }
}
