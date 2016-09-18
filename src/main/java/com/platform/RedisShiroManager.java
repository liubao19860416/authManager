package com.platform;

import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

public class RedisShiroManager {
	private String cacheName;
	private CacheManager cacheManager;

	public CacheManager getCacheManager() {
		return this.cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void init() {
	}

	public BlgroupCache getCache() {
		BlgroupCache cache = null;
		if (this.cacheManager != null) {
			cache = (BlgroupCache) this.cacheManager.getCache(getCacheName());
		}

		return cache;
	}

	public byte[] get(byte[] key) {
		Cache.ValueWrapper result = getCache().get(key);
		if (result != null) {
			return (byte[]) result.get();
		}

		return null;
	}

	public byte[] set(byte[] key, byte[] value) {
		getCache().put(key, value);

		return value;
	}

	public void del(byte[] key) {
		getCache().evict(key);
	}

	public void flushDB() {
		getCache().flushDB();
	}

	public Long dbSize() {
		return getCache().dbSize();
	}

	public Set<byte[]> keys(String pattern) {
		return getCache().keys(pattern);
	}

	public String getCacheName() {
		return this.cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public int getExpire() {
		return getCache().getExpire();
	}
}