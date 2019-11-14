package com.changgou.item.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName EnableMvcConfig
 * @Description
 * @Author sanbai5
 * @Date 11:26 2019/9/28
 * @Version 2.1
 **/
@Configuration
@ControllerAdvice
public class EnableMvcConfig implements WebMvcConfigurer {

    /**
     * @author 栗子
     * @Description 对请求资源放行
     * @Date 2:52 2019/8/19
     * @param registry
     * @return void
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 对资源放行
        registry.addResourceHandler("/items/**")
                .addResourceLocations("classpath:/templates/items");
    }
}