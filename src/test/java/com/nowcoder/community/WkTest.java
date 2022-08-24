package com.nowcoder.community;

import java.io.IOException;

public class WkTest {
    public static void main(String[] args) {
        String cmd = "d:/program Files/wkhtmltopdf/bin/wkhtmltoimage --quality 75  https://www.nowcoder.com e:/nowcoder/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);//把命令提交给本地的操作系统，剩下的由操作系统执行，操作系统执行的命令和当前主程序是异步的并发的
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
