package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Test
    public void testKafka(){
//生产者发消息是我们主动去调用去发的，希望什么时候发就什么时候调，消费者处理消息是被动的，一旦消息队列中有消息就自动处理
        kafkaProducer.sendMessage("test","你好");
        kafkaProducer.sendMessage("test","在吗");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer{//生产者
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){//当调用这个方法的时候会调用kafkaTemplate去发
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test"})//括号里是spring要关注和监听的主题

    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}
