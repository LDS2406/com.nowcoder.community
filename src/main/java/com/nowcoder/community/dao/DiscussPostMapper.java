package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper//这个注解让spring容器扫描这个接口，实现自动装配
public interface DiscussPostMapper {

    //动态sql，这个id是0的时候不用拼
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);//offset是起始行行号

    //@Param注解用于给参数取别名
    int selectDiscussPostRows(@Param("userId") int userId);//当需要动态拼接条件，且这个方法只有一个参数，参数必须取别名

    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost selectDiscussPostById(int id);

    //更新帖子中的评论数量
    int updateCommentCount(int id,int commentCount);

    //改帖子类型
    int updateType(int id, int type);

    //改帖子状态
    int updateStatus(int id, int status);

    //更新帖子分数
    int updateScore(int id, double score);

}
