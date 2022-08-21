package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {//定义消费者方法,参数用来接收相关数据
        if (record == null || record.value() == null){
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);//内容不为空，将内容中的JSON字符串恢复为对象
        if (event == null){//record有值但还原不回来
            logger.error("消息格式错误");
            return;
        }
        //数据对了去发送站内通知，构造message数据，插入到message表中
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);//消息发布者
        message.setToId(event.getEntityUserId());//消息接收者
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();//存content中的内容
        content.put("userId",event.getUserId());//这个事件是由谁触发的
        content.put("entityType",event.getEntityType());//是点赞还是评论等
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String,Object> entry : event.getData().entrySet()){//遍历key-value集合,每次得到key-value
                content.put(entry.getKey(),entry.getValue());//把event中的map内容放到content中
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);//内容不为空，将内容中的JSON字符串恢复为对象
        if (event == null){//record有值但还原不回来
            logger.error("消息格式错误");
            return;
        }
        //从事件消息中得到帖子id，查询出帖子，将帖子存到es服务器中
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    //消费删除事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeletePost(ConsumerRecord record){
        if (record == null || record.value() == null){
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);//内容不为空，将内容中的JSON字符串恢复为对象
        if (event == null){//record有值但还原不回来
            logger.error("消息格式错误");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

}
