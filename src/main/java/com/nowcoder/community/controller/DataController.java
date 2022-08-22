package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    //打开统计页面
    @RequestMapping(value = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    //统计网站UV
    @RequestMapping(value = "/data/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){//告诉服务器将字符串转为日期格式
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);//将传入的日期再传给页面保留数据
        model.addAttribute("uvEndDate",end);

        //return "/site/admin/data";返回给模板，模板返回给dispatchServlet
        return "forward:/data";//当前方法只能将请求处理一半，还需要转发给另一个方法继续处理请求，转发是再一个请求中完成的，这个方法接收的是post请求，请求在处理的过程中请求类型是不变的，所以上述方法需要支持post请求
    }

    //统计网站DAU
    @RequestMapping(value = "/data/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {//告诉服务器将字符串转为日期格式
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);//将传入的日期再传给页面保留数据
        model.addAttribute("dauEndDate", end);

        return "forward:/data";
    }
}
