package com.roukaixin.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author 不北咪
 * @date 2023/8/27 22:15
 */
@RequestMapping("/user")
@Controller
public class UserController {


    @GetMapping("/userinfo")
    @ResponseBody
    public Object info(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
