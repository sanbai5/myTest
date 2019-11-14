package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName UserFeign
 * @Description
 * @Author sanbai5
 * @Date 16:37 2019/10/9
 * @Version 2.1
 **/
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {
    @GetMapping(value = {"/{id}"})
    Result<User> findById(@PathVariable(value = "id") String id);
}
