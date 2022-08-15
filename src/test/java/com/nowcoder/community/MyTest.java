package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MyTest {
    @Test
    public void test1(){
        List<Map<String, Object>> myList = new ArrayList<>();
        Map<String, Object> myMap1 = new HashMap<>();
        Map<String, Object> myMap2 = new HashMap<>();
        myMap1.put("aaa",111);
        myMap1.put("bbb",222);
        myMap1.put("ccc",333);
        myMap2.put("555","ggg");
        myList.add(myMap1);
        myList.add(myMap2);
        for (Map<String, Object> map : myList){
            System.out.println(map);
            map.put("yyy",999);
        }
        System.out.println(myList);
    }

    @Test
    public void test2(){
        List<String> myList2 = new ArrayList<>();
        myList2.add("aaa");
        myList2.add("bbb");
        myList2.add("ccc");
        for (String str : myList2){
            System.out.println(str);
        }
    }
}
