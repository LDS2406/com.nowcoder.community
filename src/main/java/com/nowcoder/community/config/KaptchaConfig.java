package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration//表示这是一个配置类
public class KaptchaConfig {
    @Bean//声明一个bean，这个bean会被spring容器管理,在服务器启动的时候会自动被装配到容器中
    public Producer kaptchaProducer(){//方法名就是bean的名字
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789qwertyuiopasdfghjklzxcvbnm");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();//Producer接口的实现类
        Config config = new Config(properties);//给kaptcha传入参数和配置，将参数封装到config对象中,给config对象中传入properties对象，也就是map
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
