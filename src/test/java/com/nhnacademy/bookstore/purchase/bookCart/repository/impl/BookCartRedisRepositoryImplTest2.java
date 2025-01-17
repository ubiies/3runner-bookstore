package com.nhnacademy.bookstore.purchase.bookCart.repository.impl;

import com.nhnacademy.bookstore.purchase.bookCart.dto.response.ReadBookCartGuestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookCartRedisRepositoryImplTest2 {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BookCartRedisRepositoryImpl bookCartRedisRepository;

    @BeforeEach
    public void setUp() {
        //redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void testConnection() {
        String pong = redisTemplate.getConnectionFactory().getConnection().ping();
        assertThat(pong).isEqualTo("PONG");
    }
}