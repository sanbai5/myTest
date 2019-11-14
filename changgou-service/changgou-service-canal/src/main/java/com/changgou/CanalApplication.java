package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName CanalApplication
 * @Description
 * @Author 传智播客
 * @Date 12:12 2019/9/22
 * @Version 2.1
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableCanalClient  // 开启canal
@EnableFeignClients
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }
}
