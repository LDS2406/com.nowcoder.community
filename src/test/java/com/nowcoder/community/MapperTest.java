package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        User user1 = userMapper.selectByName("liubei");
        System.out.println(user1);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("11111");
        user.setEmail("136@qq.com");
        user.setSalt("abc");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateStatus(){
        userMapper.updateStatus(150,1);
        userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        userMapper.updatePassword(150,"5555");
        User user = userMapper.selectById(150);
        System.out.println(user);
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPosts(){
        List<DiscussPost> postList = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post : postList){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testinsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);


    }
    @Test
    public void testSelect(){
        LoginTicket select = loginTicketMapper.selectByTicket("abc");
        System.out.println(select);

        loginTicketMapper.updateStatus("abc",1);

        select = loginTicketMapper.selectByTicket("abc");
        System.out.println(select);
    }
}
