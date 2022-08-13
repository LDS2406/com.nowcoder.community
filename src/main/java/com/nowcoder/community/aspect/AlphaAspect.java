package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component//将方面组件声明为bean让容器来管理
//@Aspect//表示这是一个方面组件
public class AlphaAspect {//这个方面组件要定义两个内容
    //1.定义切点，代码要织入到哪些bean的哪个位置
                        //*表示方法的返回值 后面的表示service包下的所有类的所有方法 括号里表示所有的参数
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")//注解定义切点,括号里描述哪些哪些bean哪些方式是要处理的目标
    public void pointcut(){

    }

    //2.定义通知，来明确解决问题
    @Before("pointcut()")//在切点之前做处理织入代码
    public void before(){
        System.out.println("before");
    }
    @After("pointcut()")//在切点之后织入代码
    public void after(){
        System.out.println("after");
    }
    //在有了返回值以后处理逻辑
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")//在抛异常后处理
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }
    //在切点前后都做处理
    @Around("pointcut()")
                        //参数表示连接点，程序织入的部位
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed();//调用目标组件的方法，目标组件可能会有返回值
        System.out.println("around after");
        return obj;
    }
}
