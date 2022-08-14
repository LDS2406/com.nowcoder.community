package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPath(){
        return "/site/setting";
    }

    @Value("${community.path.domain}")
    private String domain;
    //从配置文件中注入上传路径
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    //得知道当前用户是什么
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    //上传头像
    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //判断文件名字是否正确
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);//从最后一个点往后截取
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //文件名没问题生成随机字符串来替换这个文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);//这是一个空文件
        try {
            headerImage.transferTo(dest);//将headerImage写入dest中,放到服务器中
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw  new RuntimeException("上传文件失败,服务器发生异常",e);
        }
        //更新当前用户头像的路径（web访问路径）
        //localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";//TODO 为什么使用重定向
    }

    //获取头像
    @RequestMapping(value = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //获取服务器的存放路径
        fileName = uploadPath + "/" + fileName;
        //获取文件的后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("image/"+suffix);
        try (
                //输入流是手动创建的，需要自己关闭。在这里生成的变量在编译的时候会自动加finally，在finally中关闭
                FileInputStream fis = new FileInputStream(fileName);//创建文件的输入流，读取文件得到输入流
        ){
            OutputStream os = response.getOutputStream();//获取字节流,这个输出流会被mvc自动关闭，因为response是由其管理的
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:"+e.getMessage());
        }
    }

    //更改密码
    @LoginRequired
    @RequestMapping(value = "/change",method = RequestMethod.POST)
    public String changePassword(String newPassword, String oldPassword, Model model){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.changePassword(user.getId(), newPassword, oldPassword);

        if (map == null || map.isEmpty()){

            return "redirect:/logout";
        }else {
            //当map中没有某个键时，get得到的是null
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return "/site/setting";
        }

    }

    //个人主页
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        return "/site/profile";
    }
}
