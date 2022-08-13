package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    //查询当前用户的会话列表(和别人的对话信息)，针对每个会话返回一条最新的私信
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    //查询当前用户的会话数量
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    //查询某个会话所包含的私信列表(和某个人的聊天记录)
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    //查询某个会话所包含的私信数量(和某个人聊天记录的数量)
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询未读私信的数量(拼接conversationId查的是某个会话的未读数量，不拼表示查所有的未读数量)
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    @Autowired
    private SensitiveFilter sensitiveFilter;
    //发送私信（添加私信）
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    //更改消息状态（把未读私信改为已读）
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
}
