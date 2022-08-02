package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Test
    public void testTextMail(){
        mailClient.sendMail("lds2406@163.com","TEST","这是测试");
    }

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHtmlMail(){
        //利用context对象给demo模板传参
        Context context = new Context();
        //把传给模板的变量存到对象中
        context.setVariable("username","sunday");

        //调用模板引擎生成网页
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("lds2406@163.com","html-test",process);
    }
}
