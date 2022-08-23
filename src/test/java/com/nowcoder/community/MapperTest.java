package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
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
        List<DiscussPost> postList = discussPostMapper.selectDiscussPosts(149,0,10,0);
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

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMessageMapper(){
        //测试会话列表
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages){

            System.out.println(message);
        }

        //测试会话数量
        int conversationCount = messageMapper.selectConversationCount(111);
        System.out.println(conversationCount);

        //查询某个会话的详细私信
        List<Message> letters = messageMapper.selectLetters("111_112", 0, 10);
        for (Message letter : letters){
            System.out.println(letter);
        }

        //查询某个会话的私信的数量
        int letterCount = messageMapper.selectLetterCount("111_112");
        System.out.println(letterCount);

        //查询未读消息的数量
        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }
}
