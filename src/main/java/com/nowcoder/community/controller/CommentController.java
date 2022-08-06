package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    //增加评论并更新评论数量
    //在评论完之后回到的页面是帖子详情页，需要用到帖子的id，所以在路径中也加入
    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
                                                                                //参数中从页面接收的有content、entityType、entityId,并不完整，需要补充userId，status等
    private String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, Model model){
        //设置comment中缺少的参数
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
