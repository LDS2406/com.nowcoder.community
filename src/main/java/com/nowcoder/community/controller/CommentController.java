package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
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
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

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

        //添加评论后通知用户，触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())//事件是谁发出的,当前用户给别人评论
                .setEntityType(comment.getEntityType())//评论事件的具体类型
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);//用来链接到帖子详情页面
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());//评论的目标
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        //以上取完event数据
        //调producer发布消息
        eventProducer.fireEvent(event);//调完之后当前线程立刻去执行页面的响应，消息的发布由消息队列去完成

        //给帖子评论才会触发发帖事件
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())//谁触发的事件
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            //触发事件要eventProducer
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
