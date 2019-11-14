package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName LoginRedirect
 * @Description 用户登陆
 * @Author sanbai5
 * @Date 10:21 2019/10/11
 * @Version 2.1
 **/
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

    @GetMapping("/login")
    public String login(@RequestParam(value = "FROM", required = false) String from, Model model) {
        model.addAttribute("from", from);
        return "login";
    }
}
