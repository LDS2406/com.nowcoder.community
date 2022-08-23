package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    //调用dao,将BeatDao注入给service
    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public AlphaService(){
        System.out.println("构造器AlphaService");
    }

    @PostConstruct//表示这个方法会在构造器之后调用
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy//表示这个方法会在销毁对象之前调用
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
       return alphaDao.select();
    }

    //虚拟业务-->注册用户|新用户自动发帖
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("111111"+user.getSalt()));
        user.setEmail("alpah@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);//插入完数据以后，mybatis会从数据库得到id放入对象的userId中

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello!");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";
    }

    //编程式事务,需要注入bean，是spring自动创建，自动装配到容器中，直接注入即可，利用这个bean执行sql，就能保证事务性
    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        //执行sql                 这里面要传入一个接口，以匿名的方式实现 泛型的类型和方法的类型一样
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            //这个方法是由transactionTemplate底层自动调的，在方法中实现我们想要的逻辑，在调用的时候实现事务管理
            public Object doInTransaction(TransactionStatus status) {//返回类型和泛型类型一致
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("111111"+user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);//插入完数据以后，mybatis会从数据库得到id放入对象的userId中

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好!");
                post.setContent("新人报道！！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";

            }
        });
    }

    @Async//让该方法在多线程环境下被异步调用
    public void execute1(){//启动线程调用这个方法和主线程是同步执行的
        logger.debug("execute1");
    }

    //何时执行 执行频率
    @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){//会自动调用这个方法
        logger.debug("execute2");
    }

}
