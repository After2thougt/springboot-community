package com.bee.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.bee.community.dao")
@SpringBootApplication
public class CommunityApplication {

    public static void main(String[] args) {
        System.out.println("Hello, World");
        SpringApplication.run(CommunityApplication.class, args);
    }

}
