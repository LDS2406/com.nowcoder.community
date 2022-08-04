package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //定义常量替换敏感词
    private static final String REPLACEMENT = "*";

    //前缀树
    private class TrieNode{

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //关键词结束标识 描述前缀树的某一个结点,是否被标记为敏感值
        private boolean isKeywordEnd = false;

        //定义子结点（key是下级字符，value是下级结点）
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //往子节点装数据
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

    //根据敏感词初始化前缀树

    //初始化根结点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct//表示这是一个初始化方法，当容器实例化这个bean后，这个方法会被自动调用，这个bean在服务启动的时候会初始化，所以在服务启动的时候这个方法就会被调用
    public void init(){

        try (//开启的资源会在编译的时候在finally中自动关闭
             InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//类加载器，从target/class目录中去加载资源，在程序编译后所有的文件都会在class中包括配置文件
             //把字节流转换成字符流，字符流转换成缓冲流效率会更高
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            //读取敏感词
            String keyword;
            while ((keyword = reader.readLine()) != null){//读取到了一行数据
                //将敏感词添加前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败"+e.getMessage());
        }

    }

    //将敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i<keyword.length(); i++){
            char c = keyword.charAt(i);//不断读取这一行数据中的字符
            TrieNode subNode = tempNode.getSubNode(c);//获取根结点有无为c的子结点
            if (subNode == null){//没有就新建一个
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束循环标志
            if (i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //检索过滤敏感词
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (begin < text.length()){//下标是0~n-1
            char c = text.charAt(position);
            //跳过符号,防止干扰
            if (isSymbol(c)){
                //若指针1处于根结点，将此符号计入结果不过滤，让指针2向后走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头还是中间，指针3都向后走一步
                position++;
                continue;//跳过符号进行下一轮循环
            }
            //检查下级结点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //让begin进入下一个位置
                position = ++begin;
                //指针1重新指向根结点
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd()){
                //发现了敏感词，将begin到position字符串替换掉
                sb.append(REPLACEMENT);
                //让position进入下一个位置
                begin = ++position;
                //指针1重新指向根结点
                tempNode = rootNode;
            }else {
                //在检测的途中，没有检测完，需要继续检查
                if (position < text.length()-1){
                    position++;
                }
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号，跳过符号
    private boolean isSymbol(Character c){
        //0x2e80--0x9fff是东亚文字范围，在这个文字范围之外认为是符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);//判断是普通字符返回true
    }

}
