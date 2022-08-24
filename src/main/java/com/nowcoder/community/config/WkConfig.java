package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkConfig {
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    //注入路径
    @Value("${wk.image.storage}")
    private String wkImageStorage;


    @PostConstruct//初始化，在服务启动的时候创建目录
    public void init(){//启动服务的时候会自动调用这个方法

        //创建wk图片目录
        File file = new File(wkImageStorage);
        if (!file.exists()){//目录不存在
            file.mkdir();
            logger.info("创建WK图片目录：" + wkImageStorage);
        }
    }
}
