package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    //关注
    public void follow(int userId, int entityType, int entityId){
        //要存关注的目标和粉丝,一项业务有两次存储要保证事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                //在有了key的情况下做两次存储操作
                operations.multi();//开启事务

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());//某个用户关注的目标
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());//某个实体的粉丝

                return operations.exec();
            }
        });
    }

    //取消关注
    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                //在有了key的情况下做两次存储操作
                operations.multi();//开启事务

                operations.opsForZSet().remove(followeeKey, entityId);//某个用户关注的目标
                operations.opsForZSet().remove(followerKey,userId);//某个实体的粉丝

                return operations.exec();
            }
        });
    }

    //查询关注的实体目标的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //判断当前用户是否关注某个实体
    public boolean hasFollowed(int userId, int entityType, int entityId){
        //判断当前用户的关注目标中是否有这个key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);//关注目标的key
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;//不空表示关注
    }
}
