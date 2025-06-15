package com.voracityrat.memehubbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检测类
 * @author grey
 */
@RestController
@RequestMapping("/")
public class MainMemeHub {

    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    public String health(){
        return "ok";
    }
}
