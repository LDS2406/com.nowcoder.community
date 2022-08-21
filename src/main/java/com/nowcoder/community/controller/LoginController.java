package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @Autowired
    private Producer kaptchaProducer;
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){//验证码需要在服务器端验证，要有session

        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        /*//将验证码传入session
        session.setAttribute("kaptcha",text);*/

        //验证码归属
        String kaptchaOwner = CommunityUtil.generateUUID();//客户端临时凭证
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);//将凭证存在cookie中，发给客户端
        cookie.setMaxAge(60);//cookie有效时间
        cookie.setPath(contextPath);//有效路径
        response.addCookie(cookie);

        //将验证码存在redis中
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);//在redis中存验证码要有key
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);//设置有效时间60s


        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);//springMVC会自动关闭这个response流
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){//Model是用来存数据，声明的User对象接收传入的参数，只要页面传入的值与User属性匹配，SpringMVC会自动将这个值注入给这个对象
        Map<String, Object> map = userService.register(user);
        //map是空就表示在service层没有出现异常，即注册成功，数据库中已插入了一条刚刚注册的数据，往model中存数据，返回给view
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已向你的邮箱发送激活邮件,请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int activation = userService.activation(userId, code);
        if (activation == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号可以正常使用");
            model.addAttribute("target","/login");
        }else if (activation == ACTIVATION_REPEAT){
            model.addAttribute("msg","您的账号已经激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @Value("${server.servlet.context-path}")//写的是application.properties中的key
    private String contextPath;//接收值的属性

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,//接收传入的数据
                        Model model/*,HttpSession session*/,HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner){//返回数据，将数据放在Model中，从session中取验证码;在登录成功后，把ticket发给客户端用cookie进行保存，需要创建HttpServletResponse
        //检查验证码
        /*String kaptcha = (String) session.getAttribute("kaptcha");*///从session中取验证码

        //从redis中取验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){//判断从cookie中取的值有没有失效
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);//生成键
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);//从键中取值
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            //将验证码和用户传入的code相比
            return "/site/login";
        }
        //检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            //成功时，生成登录凭证，发放给客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);//路径不要写死，从application.properties注入
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            //失败时，跳转回登录页
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";//重定向默认是get请求
    }
}
