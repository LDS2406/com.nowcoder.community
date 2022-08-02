package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
//启用CommunityApplication类作为配置类，此时运行的代码就以它为配置类

    /*
    *
    * ioc的核心是spring容器，这个容器是被自动创建的，哪一个类要得到这个spring容器就去实现这个接口
    * */
class CommunityApplicationTests implements ApplicationContextAware {//哪一个类想调用spring容器就实现这样的接口

   private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //applicationContext参数就是spring容器
        //当某一个类实现了ApplicationContextAware接口，重写了setApplicationContext方法，spring容器在扫描组件的时候
        //会检测到这样的bean调用这个set方法，把自身传进来,当程序启动时，applicationContext会被传进来，在其他地方就可以调用
        this.applicationContext = applicationContext;

    }
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);

        //用applicationContext这个容器去管理bean
        //从容器中获取自动装配的bean,根据类型获取
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());

        AlphaDao beta = applicationContext.getBean("beta", AlphaDao.class);
        System.out.println(beta.select());
    }

    @Test
    public void testBeanManage(){
        AlphaService bean = applicationContext.getBean(AlphaService.class);
        System.out.println(bean);
    }

    @Test
    public void testBeanConfig(){
        SimpleDateFormat bean = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(bean.format(new Date()));
    }

    //spring容器把BeatDao注入给beatDao这个属性，然后直接使用这个属性
    @Autowired
    @Qualifier("beta")//括号里写bean的名字
    private AlphaDao alphaDao;//当前bean依赖的是接口,底层是不直接和接口耦合的，降低耦合度

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Test
    public void testDI(){
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
    }
}
