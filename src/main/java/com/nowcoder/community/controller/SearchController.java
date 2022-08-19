package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;//搜到帖子后要查帖子的作者是谁
    @Autowired
    private LikeService likeService;//搜到帖子后要查帖子的点赞数

    //search?keyword
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {

        page.setPath("/search?keyword=" + keyword);
        page.setRows((int) elasticsearchService.findDiscussPostCount(keyword));
        //搜索帖子
        List<DiscussPost> searchResult = elasticsearchService.searchDiscussPost(keyword, page.getOffset(), page.getLimit());//这两个数据是怎么获得的,在Page entity中有默认值
        //聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null){
            for (DiscussPost post : searchResult){
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post",post);
                //作者
                map.put("user",userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        return "/site/search";
    }

}
