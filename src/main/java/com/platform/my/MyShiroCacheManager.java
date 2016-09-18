package com.platform.my;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * 自定义整合SpringCache类
 * 
 * @Author LiuBao
 * @Version 2.0
 * @Date 2016年9月18日
 *
 */
public class MyShiroCacheManager implements CacheManager {

	private static final Logger logger = LoggerFactory.getLogger(MyShiroCacheManager.class);
	
	//org.springframework.data.redis.cache.RedisCacheManager
	private org.springframework.cache.CacheManager cacheManager;
	
	public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		logger.debug("获取名称为:{}的RedisCache实例", name);
		org.springframework.cache.Cache springCache = cacheManager.getCache(name);
		return new SpringCacheWrapper(springCache);
	}

	class SpringCacheWrapper implements Cache {
		private org.springframework.cache.Cache springCache;

		public SpringCacheWrapper(org.springframework.cache.Cache springCache) {
			super();
			this.springCache = springCache;
		}

		@Override
		public Object get(Object key) throws CacheException {
			Object value = springCache.get(key);
			if (value instanceof SimpleValueWrapper) {
				return ((SimpleValueWrapper) value).get();
			}
			return value;
		}

		@Override
		public Object put(Object key, Object value) throws CacheException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object remove(Object key) throws CacheException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void clear() throws CacheException {
			// TODO Auto-generated method stub

		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Set keys() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection values() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}