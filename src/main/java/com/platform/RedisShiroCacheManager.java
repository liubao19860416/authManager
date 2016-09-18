package com.platform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisShiroCacheManager implements CacheManager {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisShiroCacheManager.class);

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap();
	private RedisShiroManager redisManager;
	private String keyPrefix = "shiro_redis_cache:";

	public String getKeyPrefix() {
		return this.keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		logger.debug("获取名称为:{}的RedisCache实例", name);

		Cache c = (Cache) this.caches.get(name);

		if (c == null) {
			this.redisManager.init();

			c = new RedisShiroCache(this.redisManager, this.keyPrefix);

			this.caches.put(name, c);
		}
		return c;
	}

	public RedisShiroManager getRedisManager() {
		return this.redisManager;
	}

	public void setRedisManager(RedisShiroManager redisManager) {
		this.redisManager = redisManager;
	}
}