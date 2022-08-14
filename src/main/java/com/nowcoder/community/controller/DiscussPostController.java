package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;//获取用户

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

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

    //获取帖子详情
    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page){//只要是实体类型作为一个参数声明在条件中，SpringMVC都会将这个bean存在Model中，在页面上就可以通过model获取对象
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",post);
        //查询帖子的作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //查询帖子点赞的数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount",likeCount);
        //查询当前登录用户是否对帖子进行点赞
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus",likeStatus);


        //增加逻辑，查询帖子的评论并且支持分页
        //评论分页信息
        page.setLimit(5);//设置每页显示5条评论
        page.setPath("/discuss/detail/" + id);
        page.setRows(post.getCommentCount());//总共多少条评论数据，从而算出总的页数||从帖子中取评论数

        //得到当前帖子的所有评论-->评论列表
        List<Comment> commentList =
                commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());//1表示查询帖子的数据
        //声明评论view object列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            //将commentList中的UserId转换为user对象
            for (Comment comment : commentList){//遍历集合,将得到的数据装到VO中
                //一个评论的VO
                Map<String,Object> commentVo = new HashMap<>();//用来统一封装呈现给页面的数据
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //评论的点赞数量
                long commentLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentLikeCount",commentLikeCount);
                //当前用户对评论的点赞状态
                int commentLikeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentLikeStatus",commentLikeStatus);

                //查评论的评论返回给页面进行显示-->回复列表
                List<Comment> replyList =                                       //对评论的哪一个帖子进行回复
                        commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //声明回复的VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //回复的作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));

                        //回复的点赞
                        long replyLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replyLikeCount",replyLikeCount);
                        //当前用户对回复的点赞状态
                        int replyLikeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replyLikeStatus",replyLikeStatus);

                        //回复的目标，对谁进行回复-->分为对评论的回复 || 对谁的回复进行回复
                        User targetUser
                                = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",targetUser);
                        //每次循环结束将一条回复和用户的map放入list
                        replyVoList.add(replyVo);
                    }
                }
                //回复的相关信息
                commentVo.put("replys",replyVoList);

                //帖子的评论的回复数量显示在回复之后                    对评论进行回复          对那一条具体的评论进行回复
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);

                //每次循环结束将一条评论 |评论的用户 |回复 |回复的用户 |回复的对象 放入List中
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
