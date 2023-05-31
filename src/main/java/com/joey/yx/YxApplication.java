package com.joey.yx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author joey
 * @version 1.0
 * @description 启动类
 * @date 2023/3/31 11:05
 */
@SpringBootApplication
@ServletComponentScan
@Slf4j
@EnableScheduling
public class YxApplication {
    public static void main(String[] args) {
        SpringApplication.run(YxApplication.class, args);
        log.info("项目启动成功");
    }
}
