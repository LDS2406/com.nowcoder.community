package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling//这个注解启动定时任务
@EnableAsync
public class ThreadPoolConfig {
}
