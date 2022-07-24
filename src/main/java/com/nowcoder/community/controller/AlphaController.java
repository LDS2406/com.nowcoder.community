package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest req, HttpServletResponse res){
        //获取请求数据
        System.out.println(req.getMethod());
        System.out.println(req.getServletPath());//请求路径
        Enumeration<String> headerNames = req.getHeaderNames();//得到所有请求行的key
        while (headerNames.hasMoreElements()){//是否还有更多的元素
            String name = headerNames.nextElement();
            String value = req.getHeader(name);
            System.out.println(name+"--->"+value);
        }
        System.out.println(req.getParameter("code"));
        //返回响应数据
        res.setContentType("text/html;charset=utf-8");//设置返回数据的类型:网页类型的文本，网页支持中文
        try (
                PrintWriter writer = res.getWriter();//获取输出流

        ){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get请求
    //  /students?current=1&limit=20    --->查询条件，参数拼接到路径中
    @RequestMapping(path = "/student",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "students";
    }

    // /student/123 --->参数成为路径的一部分
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") Integer id){
        System.out.println(id);
        return "一个学生id";
    }

    //post请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,Integer age){
        System.out.println(name+"--->"+age);
        return "success";
    }

    //向浏览器响应动态html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","的实力");//往模型传动态的值
        mav.addObject("age", 54);
        mav.setViewName("/demo/view");//设置模板的路径(templates的下级目录demo)和名字
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){//model这个对象是由DispatchServlet创建的，往这个对象传数据，dispatchServlet也能得到
        model.addAttribute("name","南京邮电大学");
        model.addAttribute("age",80);
        return "/demo/view";//这里返回的是路径
    }

    //响应json数据（异步请求）
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody//返回的是json，所以要加这个注解,不加的话会认为返回的是html
    public Map<String,Object> getEmp(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","领导");
        map.put("age",44);
        map.put("salary",5000);
        return map;
    }
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name","领导");
        map1.put("age",44);
        map1.put("salary",5000);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name","事务");
        map2.put("age",47);
        map2.put("salary",9000);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("name","jk");
        map3.put("age",44);
        map3.put("salary",5000);
        Map<String, Object> map4 = new HashMap<>();
        map4.put("name","dk");
        map4.put("age",44);
        map4.put("salary",5000);
        list.add(map1);
        list.add(map2);
        list.add(map3);
        list.add(map4);

        return list;
    }

}
