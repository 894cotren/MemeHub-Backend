package com.voracityrat.memehubbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.voracityrat.memehubbackend.mapper")
//下面这注解可以让我们在程序里方法里获得对象的增强类来使用
@EnableAspectJAutoProxy(exposeProxy = true)
public class MemeHubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemeHubBackendApplication.class, args);
    }

}
