package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //实现点赞的业务方法
    public void like(int userId,int entityType,int entityId,int entityUserId){
        //需要在方法中再加一个维度来记录数量，在业务中执行两次更新的操作，所以在整个业务中要保证事务性，通过编程式事务来解决

/*        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //判断用户是否在集合中以此来确定是点赞还是取消点赞
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }*/
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //在方法内部实现逻辑
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);//以实体为key
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);//以user为key,方法参数中的userId是点赞的人，而这里要传入的是被赞的人
                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);//判断当前用户是否给实体点赞
                //上面的查询要放在事务执行过程之外

                operations.multi();//开启事务

                //执行两次修改操作
                if (isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);//取消赞
                    operations.opsForValue().decrement(userLikeKey);//用户被赞-1
                }else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();//执行(提交)事务
            }
        });

    }

    //查询实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)? 1 : 0;
    }

    //查询某个用户获得赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
