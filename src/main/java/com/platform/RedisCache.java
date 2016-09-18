//package com.platform;
//
//import java.util.Arrays;
//import java.util.Set;
//import java.util.concurrent.Callable;
//import org.springframework.cache.Cache;
//import org.springframework.cache.Cache.ValueWrapper;
//import org.springframework.cache.support.SimpleValueWrapper;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.util.Assert;
//
//public class RedisCache
//  implements BlgroupCache
//{
//  private static final int PAGE_SIZE = 128;
//  private String name;
//  private RedisTemplate template;
//  private byte[] prefix;
//  private byte[] setName;
//  private byte[] cacheLockName;
//  private long WAIT_FOR_LOCK = 300L;
//  private long expiration = 3600L;
//
//  public RedisCache()
//  {
//  }
//
//  public RedisCache(String name, RedisTemplate<? extends Object, ? extends Object> template)
//  {
//    Assert.hasText(name, "non-empty cache name is required");
//    this.name = name;
//    this.template = template;
//
//    StringRedisSerializer stringSerializer = new StringRedisSerializer();
//
//    this.setName = stringSerializer.serialize(name + "~keys");
//    this.cacheLockName = stringSerializer.serialize(name + "~lock");
//  }
//
//  public RedisCache(String name, RedisTemplate<? extends Object, ? extends Object> template, long expirationCfg)
//  {
//    Assert.hasText(name, "non-empty cache name is required");
//    this.name = name;
//    this.template = template;
//
//    StringRedisSerializer stringSerializer = new StringRedisSerializer();
//
//    this.setName = stringSerializer.serialize(name + "~keys");
//    this.cacheLockName = stringSerializer.serialize(name + "~lock");
//    this.expiration = expirationCfg;
//  }
//
//  public RedisCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template, long expiration)
//  {
//    Assert.hasText(name, "non-empty cache name is required");
//    this.name = name;
//    this.template = template;
//    this.prefix = prefix;
//    this.expiration = expiration;
//
//    StringRedisSerializer stringSerializer = new StringRedisSerializer();
//
//    this.setName = stringSerializer.serialize(name + "~keys");
//    this.cacheLockName = stringSerializer.serialize(name + "~lock");
//  }
//
//  public String getName() {
//    return this.name;
//  }
//
//  public Object getNativeCache()
//  {
//    return this.template;
//  }
//
//  public <T> T get(final Object key, Callable<T> arg1) {
//    return this.template.execute(new Object()
//    {
//      public T doInRedis(RedisConnection connection) throws DataAccessException {
//        RedisCache.this.waitForLock(connection);
//        byte[] bs = connection.get(RedisCache.this.computeKey(key));
//        Object value = RedisCache.this.template.getValueSerializer() != null ? RedisCache.this.template.getValueSerializer().deserialize(bs) : bs;
//
//        return bs == null ? null : value;
//      }
//    }
//    , true);
//  }
//
//  public Cache.ValueWrapper get(final Object key)
//  {
//    return (Cache.ValueWrapper)this.template.execute(new Object()
//    {
//      public Cache.ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
//        RedisCache.this.waitForLock(connection);
//        byte[] bs = connection.get(RedisCache.this.computeKey(key));
//        Object value = RedisCache.this.template.getValueSerializer() != null ? RedisCache.this.template.getValueSerializer().deserialize(bs) : bs;
//
//        return bs == null ? null : new SimpleValueWrapper(value);
//      }
//    }
//    , true);
//  }
//
//  public <T> T get(Object key, Class<T> type)
//  {
//    Cache.ValueWrapper wrapper = get(key);
//    return wrapper == null ? null : wrapper.get();
//  }
//
//  public void put(Object key, Object value)
//  {
//    final byte[] keyBytes = computeKey(key);
//    final byte[] valueBytes = convertToBytesIfNecessary(this.template.getValueSerializer(), value);
//
//    this.template.execute(new RedisCallback()
//    {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        RedisCache.this.waitForLock(connection);
//
//        connection.multi();
//
//        connection.set(keyBytes, valueBytes);
//        connection.zAdd(RedisCache.this.setName, 0.0D, keyBytes);
//
//        if (RedisCache.this.expiration > 0L) {
//          connection.expire(keyBytes, RedisCache.this.expiration);
//
//          connection.expire(RedisCache.this.setName, RedisCache.this.expiration);
//        }
//        connection.exec();
//
//        return null;
//      }
//    }
//    , true);
//  }
//
//  public Cache.ValueWrapper putIfAbsent(Object key, final Object value)
//  {
//    final byte[] keyBytes = computeKey(key);
//    final byte[] valueBytes = convertToBytesIfNecessary(this.template.getValueSerializer(), value);
//
//    return toWrapper(this.template.execute(new Object()
//    {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        RedisCache.this.waitForLock(connection);
//
//        Object resultValue = value;
//        boolean valueWasSet = connection.setNX(keyBytes, valueBytes).booleanValue();
//        if (valueWasSet) {
//          connection.zAdd(RedisCache.this.setName, 0.0D, keyBytes);
//          if (RedisCache.this.expiration > 0L) {
//            connection.expire(keyBytes, RedisCache.this.expiration);
//
//            connection.expire(RedisCache.this.setName, RedisCache.this.expiration);
//          }
//        } else {
//          resultValue = RedisCache.this.deserializeIfNecessary(RedisCache.this.template.getValueSerializer(), connection.get(keyBytes));
//        }
//
//        return resultValue;
//      }
//    }
//    , true));
//  }
//
//  private Cache.ValueWrapper toWrapper(Object value)
//  {
//    return value != null ? new SimpleValueWrapper(value) : null;
//  }
//
//  public void evict(Object key) {
//    final byte[] k = computeKey(key);
//
//    this.template.execute(new RedisCallback() {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        connection.del(new byte[][] { k });
//
//        connection.zRem(RedisCache.this.setName, new byte[][] { k });
//        return null;
//      }
//    }
//    , true);
//  }
//
//  public void clear()
//  {
//    this.template.execute(new RedisCallback()
//    {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        if (connection.exists(RedisCache.this.cacheLockName).booleanValue()) {
//          return null;
//        }try {
//          connection.set(RedisCache.this.cacheLockName, RedisCache.this.cacheLockName);
//
//          int offset = 0;
//          boolean finished = false;
//          Set keys;
//          do {
//            keys = connection.zRange(RedisCache.this.setName, offset * 128, (offset + 1) * 128 - 1);
//
//            finished = keys.size() < 128;
//            offset++;
//            if (!keys.isEmpty())
//              connection.del((byte[][])keys.toArray(new byte[keys.size()][]));
//          }
//          while (!finished);
//
//          connection.del(new byte[][] { RedisCache.this.setName });
//          return null;
//        }
//        finally {
//          connection.del(new byte[][] { RedisCache.this.cacheLockName });
//        }
//      }
//    }
//    , true);
//  }
//
//  private byte[] computeKey(Object key)
//  {
//    byte[] keyBytes = convertToBytesIfNecessary(this.template.getKeySerializer(), key);
//
//    if ((this.prefix == null) || (this.prefix.length == 0)) {
//      return keyBytes;
//    }
//
//    byte[] result = Arrays.copyOf(this.prefix, this.prefix.length + keyBytes.length);
//    System.arraycopy(keyBytes, 0, result, this.prefix.length, keyBytes.length);
//
//    return result;
//  }
//
//  private boolean waitForLock(RedisConnection connection) {
//    boolean foundLock = false;
//    boolean retry;
//    do {
//      retry = false;
//      if (connection.exists(this.cacheLockName).booleanValue()) {
//        foundLock = true;
//        try {
//          Thread.sleep(this.WAIT_FOR_LOCK);
//        } catch (InterruptedException ex) {
//          Thread.currentThread().interrupt();
//        }
//        retry = true;
//      }
//    }
//    while (retry);
//
//    return foundLock;
//  }
//
//  private byte[] convertToBytesIfNecessary(RedisSerializer<Object> serializer, Object value)
//  {
//    if (value == null) {
//      return new byte[0];
//    }
//
//    if ((serializer == null) && ((value instanceof byte[]))) {
//      return (byte[])value;
//    }
//
//    return serializer.serialize(value);
//  }
//
//  private Object deserializeIfNecessary(RedisSerializer<byte[]> serializer, byte[] value)
//  {
//    if (serializer != null) {
//      return serializer.deserialize(value);
//    }
//
//    return value;
//  }
//
//  public void put(Object key, Object value, final int expire)
//  {
//    final byte[] keyBytes = computeKey(key);
//    final byte[] valueBytes = convertToBytesIfNecessary(this.template.getValueSerializer(), value);
//
//    this.template.execute(new RedisCallback()
//    {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        RedisCache.this.waitForLock(connection);
//
//        connection.multi();
//
//        connection.set(keyBytes, valueBytes);
//        connection.zAdd(RedisCache.this.setName, 0.0D, keyBytes);
//
//        if (RedisCache.this.expiration > 0L) {
//          connection.expire(keyBytes, expire);
//
//          connection.expire(RedisCache.this.setName, expire);
//        }
//        connection.exec();
//
//        return null;
//      }
//    }
//    , true);
//  }
//
//  public void flushDB()
//  {
//    this.template.execute(new RedisCallback() {
//      public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        connection.flushDb();
//        return null;
//      }
//    }
//    , true);
//  }
//
//  public Long dbSize()
//  {
//    Long dbsize = null;
//    try {
//      dbsize = (Long)this.template.execute(new RedisCallback() {
//        public Long doInRedis(RedisConnection connection) throws DataAccessException {
//          return connection.dbSize();
//        }
//      }
//      , true);
//    }
//    catch (Exception localException)
//    {
//    }
//
//    return dbsize;
//  }
//
//  public Set<byte[]> keys(String pattern)
//  {
//    Set keys = null;
//
//    final byte[] keyBytes = pattern.getBytes();
//    try
//    {
//      keys = (Set)this.template.execute(new RedisCallback() {
//        public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
//          return connection.keys(keyBytes);
//        }
//      }
//      , true);
//    }
//    catch (Exception localException)
//    {
//    }
//
//    return keys;
//  }
//
//  public int getExpire()
//  {
//    return (int)this.expiration;
//  }
//}