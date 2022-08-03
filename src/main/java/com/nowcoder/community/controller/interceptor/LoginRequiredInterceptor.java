package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //首先判断拦截的目标Object是不是一个方法，是方法就处理，静态资源就不用拦截
        if (handler instanceof HandlerMethod){//拦截的是一个方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;//转型
            Method method = handlerMethod.getMethod();//获取拦截到的方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//从方法中取注解
            if (loginRequired != null && hostHolder.getUser() == null){//表示当前方法需要登录，但又没有登录
                response.sendRedirect(request.getContextPath() + "/login");//重定向
                return false;//拒绝后续的请求，强制到登录界面
            }
        }
        return true;
    }
}
