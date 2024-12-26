package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register")
    public CommonResp<String> register() {
        log.info("register");
        return null;
    }

    @PostMapping("/login")
    public CommonResp<String> login(@RequestBody String body) {

        log.info("login");
        return null;
    }

    @PostMapping("/logout")
    public CommonResp<String> logout() {
        log.info("logout");
        return null;
    }
}

