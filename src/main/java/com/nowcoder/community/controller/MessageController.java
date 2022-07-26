package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(value = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表以及最新一条消息
        List<Message> conversationList =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        //在会话列表界面会有未读消息等额外内容的提示
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message message : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                //某个会话包含几条消息
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //某个会话的未读消息数量
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                /*要显示当前用户和谁进行会话，显示from_id----(如果当前用户是消息的发起者，那目标用户就是消息的接收者)
                (就是某个会话有两个用户，要确定这两个用户谁是处在当前的登录状态)*/
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查所有会话的未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",unreadNoticeCount);


        return "/site/letter";
    }

    //私信详情
    @RequestMapping(value = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表(也就是和某一个人的所有聊天记录)
        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        //在私信列表中也有用户头像等额外信息
        List<Map<String,Object>> letters = new ArrayList<>();
        if (lettersList != null){
            for (Message message : lettersList){
                Map<String,Object> map = new HashMap<>();//用来封装数据
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //查询私信目标
        model.addAttribute("target",getLetterTarget(conversationId));

        //将私信列表中未读的消息提取出来设置为已读（当打开私信详情页面就表示已经读取了消息）
        List<Integer> ids = getLetterIds(lettersList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    //从某个会话的消息集合中提取未读的消息id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message : letterList){
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){//用户是会话的接收方（消息是别人发送过来的）并且消息状态是未读
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    //封装一个方法，查询私信目标
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

    //发送私信
    @RequestMapping(value = "/letter/send",method = RequestMethod.POST)
    @ResponseBody//请求是异步的
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if (target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId()<message.getToId()){//小的拼在前面
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setCreateTime(new Date());
        message.setContent(content);

        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    //显示系统通知列表
    @RequestMapping(value = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();//获取当前用户

        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);//查询最新消息
        if (message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());//去除转义字符
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);//将JSON字符串还原为对象放在map中

            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);//查询消息数
            messageVo.put("count",count);

            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);//查询未读消息
            messageVo.put("unread",unread);
            model.addAttribute("commentNotice",messageVo);
        }

        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);//最新消息
        if (message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());//去除转义字符
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);//将JSON字符串还原为对象放在map中

            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);

            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread",unread);
            model.addAttribute("likeNotice",messageVo);
        }

        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);//最新消息
        if (message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());//去除转义字符
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);//将JSON字符串还原为对象放在map中

            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count",count);

            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread",unread);
            model.addAttribute("followNotice",messageVo);
        }

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",unreadNoticeCount);

        return "/site/notice";
    }

    //显示通知详情
    @RequestMapping(value = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null){
            for (Message notice : noticeList){
                Map<String, Object> map = new HashMap<>();
                map.put("notice",notice);//存的是通知详情

                //获取notice内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));

                //通知的作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));//在user表中是sysytem

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()){//集合非空
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}

