package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    //调用dao,将BeatDao注入给service
    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("构造器AlphaService");
    }

    @PostConstruct//表示这个方法会在构造器之后调用
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy//表示这个方法会在销毁对象之前调用
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
       return alphaDao.select();
    }
}
