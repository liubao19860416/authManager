package com.platform;

import java.util.Set;
import org.springframework.cache.Cache;

public abstract interface BlgroupCache extends Cache {
	
	public abstract void put(Object paramObject1, Object paramObject2, int paramInt);

	public abstract void flushDB();

	public abstract Long dbSize();

	public abstract Set<byte[]> keys(String paramString);

	public abstract int getExpire();
}