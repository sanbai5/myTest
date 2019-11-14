package com.changgou.oauth.controller;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName UserloginController
 * @Description 用户登陆生成令牌
 * @Author sanbai5
 * @Date 10:40 2019/10/9
 * @Version 2.1
 **/
@RestController
@RequestMapping("/user")
public class UserloginController {
    /**
     * @return
     * @Author mqy
     * @Description 通过密码授权
     * @Date
     * @Param
     **/
    @Autowired(required = false)
    private UserLoginService userLoginService;

    @Autowired(required = false)
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

     @RequestMapping("/login")
     public Result login(String username, String password, HttpServletResponse response) {
         try {
             String grant_type = "password";
             AuthToken authToken = userLoginService.login(username, password, clientId, clientSecret, grant_type);
             String token = authToken.getAccessToken();
             Cookie cookie = new Cookie("Authorization", token);
             cookie.setDomain("localhost");
             cookie.setPath("/");
             response.addCookie(cookie);
             return new Result(true, StatusCode.OK, "登陆成功", authToken);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         return new Result(false, StatusCode.OK, "登陆失败");

     }
    /*@RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        try {
            if(StringUtils.isEmpty(username)){
                throw new RuntimeException("用户名不允许为空");
            }
            if(StringUtils.isEmpty(password)){
                throw new RuntimeException("密码不允许为空");
            }
            AuthToken authToken = authService.login(username, password, clientId, clientSecret);
            String token = authToken.getAccessToken();
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);
            return new Result(true, StatusCode.OK, "登陆成功", authToken);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return new Result(true, StatusCode.OK, "登陆失败");
    }*/
}
