package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
//这个注解标识的类就表示这是一个配置文件

/*
* 正式运行程序运行的就是这个类，一运行就以它为配置类来执行程序，在测试的程序中也希望使用这个配置类，希望和正式环境的配置类是一样的
*
* */
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
        /*
         这个方法底层自动创建了spring容器
         创建了之后会自动扫描包下的某些bean，将bean装配到容器中
        */
    }

}
