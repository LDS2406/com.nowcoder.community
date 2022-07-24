package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper//加注解让spring容器装配这个bean
public interface UserMapper {
    //在这里写crud方法，再写上对应的配置文件(所需要的sql)，mybatis底层会自动写实现类

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
