package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//这个注解是用来标识方法是否需要在登录的状态下才能访问

@Target(ElementType.METHOD)//这个注解可以写在方法之上，拦截器拦截的是方法
@Retention(RetentionPolicy.RUNTIME)//表示程序运行时才有效
public @interface LoginRequired {
    //不用写任何内容，只起到一个标识的作用，有这个标记标识只有登录才能访问，没有这个标记就随便
}
