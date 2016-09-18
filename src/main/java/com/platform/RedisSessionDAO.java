package com.platform;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

public class RedisSessionDAO extends AbstractSessionDAO {
	
	private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
	private RedisShiroManager redisManager;
	private String keyPrefix = "shiro_redis_session:";

	public void update(Session session) throws UnknownSessionException {
		saveSession(session);
	}

	private void saveSession(Session session) throws UnknownSessionException {
		if ((session == null) || (session.getId() == null)) {
			logger.error("session or session id is null");
			return;
		}

		byte[] key = getByteKey(session.getId());
		byte[] value = SerializationUtils.serialize(session);
		session.setTimeout(this.redisManager.getExpire() * 1000);
		this.redisManager.set(key, value);
	}

	public void delete(Session session) {
		if ((session == null) || (session.getId() == null)) {
			logger.error("session or session id is null");
			return;
		}
		this.redisManager.del(getByteKey(session.getId()));
	}

	public Collection<Session> getActiveSessions() {
		Set sessions = new HashSet();

		Set<byte[]> keys = this.redisManager.keys(this.keyPrefix + "*");
		if ((keys != null) && (keys.size() > 0)) {
			for (byte[] key : keys) {
				Session s = (Session) SerializationUtils.deserialize(this.redisManager.get(key));
				sessions.add(s);
			}
		}

		return sessions;
	}

	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		saveSession(session);
		return sessionId;
	}

	protected Session doReadSession(Serializable sessionId) {
		if (sessionId == null) {
			logger.error("session id is null");
			return null;
		}

		Session s = (Session) SerializationUtils.deserialize(this.redisManager.get(getByteKey(sessionId)));
		return s;
	}

	private byte[] getByteKey(Serializable sessionId) {
		String preKey = this.keyPrefix + sessionId;
		return preKey.getBytes();
	}

	public RedisShiroManager getRedisManager() {
		return this.redisManager;
	}

	public void setRedisManager(RedisShiroManager redisManager) {
		this.redisManager = redisManager;

		this.redisManager.init();
	}

	public String getKeyPrefix() {
		return this.keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
}