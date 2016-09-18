package com.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

public class RedisShiroCache<K, V> implements Cache<K, V> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RedisShiroManager cache;
	private String keyPrefix = "shiro_redis_session:";

	public String getKeyPrefix() {
		return this.keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public RedisShiroCache(RedisShiroManager cache) {
		if (cache == null) {
			throw new IllegalArgumentException("Cache argument cannot be null.");
		}
		this.cache = cache;
	}

	public RedisShiroCache(RedisShiroManager cache, String prefix) {
		this(cache);

		this.keyPrefix = prefix;
	}

	private byte[] getByteKey(K key) {
		if ((key instanceof String)) {
			String preKey = this.keyPrefix + key;
			return preKey.getBytes();
		}
		return SerializationUtils.serialize(key);
	}

	public V get(K key) throws CacheException {
		try {
			if (key == null) {
				return null;
			}
			byte[] rawValue = this.cache.get(getByteKey(key));

			return (V) SerializationUtils.deserialize(rawValue);
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public V put(K key, V value) throws CacheException {
		try {
			this.cache.set(getByteKey(key), SerializationUtils.serialize(value));
			return value;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public V remove(K key) throws CacheException {
		try {
			Object previous = get(key);
			this.cache.del(getByteKey(key));
			return (V) previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public void clear() throws CacheException {
		try {
			this.cache.flushDB();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public int size() {
		try {
			Long longSize = new Long(this.cache.dbSize().longValue());
			return longSize.intValue();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public Set<K> keys() {
		try {
			Set<byte[]> keys = this.cache.keys(this.keyPrefix + "*");
			if (CollectionUtils.isEmpty(keys)) {
				return Collections.emptySet();
			}
			Set newKeys = new HashSet();
			for (byte[] key : keys) {
				newKeys.add(key);
			}
			return newKeys;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public Collection<V> values() {
		try {
			Set<byte[]> keys = this.cache.keys(this.keyPrefix + "*");
			if (!CollectionUtils.isEmpty(keys)) {
				List values = new ArrayList(keys.size());
				for (byte[] key : keys) {
					Object value = get((K) key);
					if (value != null) {
						values.add(value);
					}
				}
				return Collections.unmodifiableList(values);
			}
			return Collections.emptyList();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}
}