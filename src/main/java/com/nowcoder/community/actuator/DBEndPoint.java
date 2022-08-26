package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")//给端点取id
public class DBEndPoint {
    private static final Logger logger = LoggerFactory.getLogger(DBEndPoint.class);
    //通过连接池连接
    @Autowired
    private DataSource dataSource;

    @ReadOperation//表示这个方法是通过get请求来访问，这个端点只能通过get请求来访问
    public String checkConnection(){
        try (Connection connection = dataSource.getConnection();) {
            return CommunityUtil.getJSONString(0,"获取连接成功！");
        }catch (SQLException e){
            logger.error("获取连接失败" + e.getMessage());
            return CommunityUtil.getJSONString(1,"获取连接失败！");
        }
    }
}
