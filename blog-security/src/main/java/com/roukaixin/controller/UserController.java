package com.roukaixin.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author pankx
 * @date 2023/8/27 22:15
 */
@RequestMapping("/user")
@Controller
public class UserController {


    @GetMapping("/userinfo")
    @ResponseBody
    public DefaultOAuth2User info(){
        DefaultOAuth2User details = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details;
    }
}
