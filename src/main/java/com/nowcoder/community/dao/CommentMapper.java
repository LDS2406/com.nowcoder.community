package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据实体来查询帖子，查询某一页的数据
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    //查询帖子总数来计算总的页数
    int selectCountByEntity(int entityType,int entityId);
}
