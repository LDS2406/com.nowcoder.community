package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成激活码（随机字符串）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密（只能加密不能解密）
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //处理json字符串,将参数封装成json对象，将json转换成字符串
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map != null){
            for (String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    //方法重载
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    //方法重载
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }
}
