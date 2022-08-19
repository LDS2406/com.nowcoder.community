package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {//不需要实现任何方法，只要继承一下
                                                                //泛型声明接口处理的实体类是什么，实体类中的主键是什么类型
    //这个父接口已经事先定义好了对es服务器访问的增删改查
}
