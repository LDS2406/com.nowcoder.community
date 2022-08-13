package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    //定义第三方bean
    @Bean
                                                    //在定义一个bean时，方法中声明这样的参数Spring容器会自动将这个bean注入
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){//要将哪个对象装配到容器中，就返回这个对象，方法名就是bean的名字
        //利用template访问数据库，需要创建连接，连接是由连接工厂创建的，需要将连接工厂注入给template才能访问数据库
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //配置序列化方式（配置数据访问的方式）
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());//返回一个能够序列化字符串的序列化器
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();//触发生效
        return template;
    }

}
