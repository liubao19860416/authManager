//package com.platform.redis;
//import org.apache.shiro.cache.Cache;
// 
//public class RedisShiroCacheManager implements ShiroCacheManager {
// 
//    private RedisManager redisManager;
// 
//    public RedisManager getRedisManager() {
//        return redisManager;
//    }
// 
//    public void setRedisManager(RedisManager redisManager) {
//        this.redisManager = redisManager;
//    }
// 
//    @Override
//    public <K, V> Cache<K, V> getCache(String name) {
//        return new JedisShiroCache<K, V>(redisManager, name);
//    }
// 
//    @Override
//    public void destroy() {
//        redisManager.init();
//        redisManager.flushDB();
//    }
// 
//}