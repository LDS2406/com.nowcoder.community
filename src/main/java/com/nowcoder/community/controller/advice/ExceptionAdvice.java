package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)//表示这个注解只扫描带有Controller注解的bean
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    //这个方法表示处理所有的错误情况
    @ExceptionHandler({Exception.class})//()表示处理哪些异常
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常" + e.getMessage());
        //每个element记录了一条异常的信息
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //给浏览器响应，重定向到错误页面
        //判断请求是普通请求还是异步请求，异步请求返回的是JSON，普通请求返回的是网页
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){//成立表示是异步请求，响应字符串
                                                //这里可以写json，向浏览器返回字符串会自动转换为json对象
                                                //plain，表示向浏览器返回的是普通的字符串，浏览器得到后需要人为地转换成json对象
            response.setContentType("application/plain;charset=utf-8");//声明字符集是utf-8支持中文
            //获取输出流输出字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        }else {//普通请求，重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
