package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/*
* 运行程序，spring会自动扫描这个bean，将其装配到容器中
* */

@Repository("beta")
public class AlphaDaoImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
