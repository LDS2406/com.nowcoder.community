package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;//获取用户

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody//返回的是字符串不是网页
    public String addDiscussPost(String title,String content){
        //发帖的前提是登录，得获取user
        User user = hostHolder.getUser();
        if (user == null){//此时还未登录，返回提示
            return CommunityUtil.getJSONString(403,"您还未登录!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }
}
