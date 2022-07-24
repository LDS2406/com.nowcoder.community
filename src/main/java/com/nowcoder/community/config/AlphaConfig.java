package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration//表示这个类是一个配置类，不是普通的类
public class AlphaConfig {//这个配置用来装配第三方的bean


    /*
    * 这个方法返回的对象会被装配到容器中，作为bean被spring管理
    * */
    @Bean//用这个注解来定义第三方的bean，方法名simpleDateFormat就是bean的名字
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
