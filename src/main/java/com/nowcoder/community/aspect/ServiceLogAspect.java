package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //1.声明切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){

    }

    //2.在切点一开始织入程序
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){//joinPoint表示程序织入的位子
        //记录日志格式：用户【IP地址】，在【时间】，访问了【com.nowcoder.community.service.xxx()】
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();//获取request对象
        String ip = request.getRemoteHost();//通过request获取用户IP
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();//得到目标的类型名
        logger.info("用户" + ip + ",在" + now + ",访问了" + target + ".");
    }
}
