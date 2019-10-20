package com.atguigu.gmall.manage.redissonTest;

import com.atguigu.gmall.util.RedisUtil;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

@Controller
public class RedissonController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("testRedisson")
    public String testRedisson(){
        Jedis jedis = redisUtil.getJedis();
        return null;
    }
}
