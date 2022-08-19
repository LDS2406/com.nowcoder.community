package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import com.nowcoder.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
* 对AlphaInterceptor进行配置，不然Spring也不知道这个组件拦截哪些请求
* */
@Configuration//这个注解表示这个类是配置类，这个配置类要实现接口,而不是简单装配一个bean
public class WebMvcConfig implements WebMvcConfigurer {
    //在配置类中要配拦截器，将拦截器注入，在实现的接口的某一个方法中注册拦截器
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {//利用传入的对象注册interceptor
        registry.addInterceptor(alphaInterceptor)//这么写表示拦截器拦截一切请求
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")//表示哪些项目不用拦截，static目录下的所有css文件都要排除掉
                .addPathPatterns("/register","/login");//添加明确拦截哪些路径
        //"/**/*.css"项目和域名可以忽略，直接从static这一级开始配

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }


    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;


}
