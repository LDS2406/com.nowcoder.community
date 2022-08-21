package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.在请求开始之初，通过cookie得到凭证ticket(登录后才能取到)，通过凭证找到用户，并将用户暂存起来
        String ticket = CookieUtil.getValue(request,"ticket");
        if (ticket != null){//表示登录过了，那就查询出用户
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //查询凭证--判断是否有效status，是否超时expiredSeconds                                     这里表示超时时间晚于当前时间
            if (loginTicket != null && loginTicket.getStatus() != 1 && loginTicket.getExpired().after(new Date())){
                //根据用户凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                /*
                * 在本次请求中持有用户，也就是在后面的处理业务中随时都有可能会用，将user暂存一下，随时可以用到
                * 浏览器访问服务器是多对一的，一个服务器能处理多个请求，是并发的，每个浏览器访问服务器，服务器会创建一个独立的线程来
                * 处理请求，所以服务器在处理请求的时候是一个多线程的环境。所以在存用户的时候要考虑多线程的情况，如果只是简单的存到一个工具或者是容器中
                * 在并发的情况下会产生冲突。
                * 想要把数据存到一个地方让多线程并发访问都没有问题要考虑线程的隔离，每个线程单独存一份，他们相互不影响-->ThreadLocal
                * 将user存在ThreadLocal中，在多线程之间隔离存这个对象，这个逻辑封装到一个工具中，其他对象也可以调用
                * */
                hostHolder.setUser(user);

                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));//存入SecurityContext
            }
        }
        return true;//如果是false表示后面不执行
    }

    //在模板引擎调用之前将User存在Model中
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    //在整个请求结束时，将hostHolder里的user清除
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
//        SecurityContextHolder.clearContext();
    }
}
