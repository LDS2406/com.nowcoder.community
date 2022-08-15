package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    //存帖子和评论实体的赞
    //like:entity:entityType:entityId ---> set(userId)点赞的用户
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户的赞
    //like:user:userId-->int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体--->A关注B
    //followee:userId:entityType(userId表示关注的人A，entityType关注的是哪个实体比如是人还是帖子B)--->zset(entityId,now)entityId表示被关注的人
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体的粉丝
    //follower:entityType:entityId--->zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
