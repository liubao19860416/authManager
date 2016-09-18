package com.platform;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Redis测试类
 * 
 * @author Liubao
 * @2016年4月16日
 * 
 */
public class SpringRedisClientTest {
    
    private ApplicationContext applicationContext;
    private ShardedJedisPool shardedJedisPool1;
    private ShardedJedisPool shardedJedisPool2;
    private ShardedJedisPool shardedJedisPool3;

    @Before
    public void before() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("spring/spring-redis2.xml");
        shardedJedisPool1 = (ShardedJedisPool) applicationContext.getBean("shardedJedisPool1");
        shardedJedisPool2 = (ShardedJedisPool) applicationContext.getBean("shardedJedisPool2");
        shardedJedisPool3 = (ShardedJedisPool) applicationContext.getBean("shardedJedisPool3");
    }

    /**
     * @deprecated
     * 不推荐使用
     * 保存到db=0中去了?最后一个添加的机器ip有效,且使用对应的默认的dbIndex为0;
     * 为什么${redis1.name}未起作用
     */
    @Test
    @Deprecated
    public void test01() {
        // 从池中获取一个Jedis对象
        ShardedJedis jedis = shardedJedisPool1.getResource();
        String keys = "name1";
        String value = "Liubao1";
        // 删数据
        jedis.del(keys);
        // 存数据
        jedis.set(keys, value);
        // 取数据
        String v = jedis.get(keys);
        System.out.println(v);
        // 释放对象池
        //shardedJedisPool1.returnResource(jedis);
        shardedJedisPool1.returnResourceObject(jedis);

        assertEquals(value, v);
    }
    
    /**
     * 推荐使用1
     * 保存到db=2中去了,最后一个添加的机器ip有效,且使用了其对应的dbIndex.
     */
    @Test
    public void test02() {
        // 从池中获取一个Jedis对象
        ShardedJedis jedis = shardedJedisPool2.getResource();
        String keys = "name2";
        String value = "Liubao2";
        // 删数据
        jedis.del(keys);
        // 存数据
        jedis.set(keys, value);
        // 取数据
        String v = jedis.get(keys);
        System.out.println(v);
        // 释放对象池
        //shardedJedisPool2.returnResource(jedis);
        shardedJedisPool2.returnResourceObject(jedis);
        
        assertEquals(value, v);
    }
    /**
     * 推荐使用2
     * 保存到db=1中去了,第一个添加的机器ip有效,且使用了其对应的dbIndex.
     */
    @Test
    public void test03() {
        // 从池中获取一个Jedis对象
        ShardedJedis jedis = shardedJedisPool3.getResource();
        String keys = "name3";
        String value = "Liubao3";
        // 删数据
        jedis.del(keys);
        // 存数据
        jedis.set(keys, value);
        // 取数据
        String v = jedis.get(keys);
        System.out.println(v);
        // 释放对象池
        //shardedJedisPool3.returnResource(jedis);
        shardedJedisPool3.returnResourceObject(jedis);
        
        assertEquals(value, v);
    }
    
    
}
