package com.atguigu.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;

public class RedisUtil {

        private JedisPool jedisPool;

        public void initPool(String host,int port ,int database){
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(200);
            poolConfig.setMaxIdle(30);
            poolConfig.setBlockWhenExhausted(true);
            poolConfig.setMaxWaitMillis(10*1000);
            poolConfig.setTestOnBorrow(true);
            jedisPool=new JedisPool(poolConfig,host,port,20*1000);
        }
        public Jedis getJedis(){
            Jedis jedis = jedisPool.getResource();
            return jedis;
        }


    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 尝试获取分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        System.out.println(result);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

}
