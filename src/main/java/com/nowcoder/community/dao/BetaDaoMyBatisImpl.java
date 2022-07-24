package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class BetaDaoMyBatisImpl implements BeatDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
