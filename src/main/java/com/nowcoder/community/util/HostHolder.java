package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/*
* 这个工具起到一个容器的作用，持有用户的信息，用于代替session对象
* */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();//以线程为key存取值

    //往线程中存取user
    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //线程结束时清理
    public void clear(){
        users.remove();
    }
}
