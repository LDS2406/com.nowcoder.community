package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper//这个注解让spring容器扫描这个接口，实现自动装配
public interface DiscussPostMapper {

    //动态sql，这个id是0的时候不用拼
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);//offset是起始行行号

    //@Param注解用于给参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);//当需要动态拼接条件，且这个方法只有一个参数，参数必须取别名

    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);

}
