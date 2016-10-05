package com.platform;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.platform.shiro.dao.AppMapper;
import com.platform.shiro.entity.App;


/**
 * 基础Service层测试类
 * 
 * @Author LiuBao
 * @Version 2.0
 * @Date 2016年10月5日
 *
 */
@ContextConfiguration(locations = { "classpath:spring/spring-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseServiceAndDaoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceAndDaoTest.class);

	@Autowired
	private AppMapper appMapper;
	
	@Before
	public void init() throws Exception {
	}

	@After
	public void down() throws Exception {
	}

	@Test
	public void testSelectAll() throws Exception {
		Assert.assertNotNull(appMapper);
		List<App> selectAll = appMapper.selectAll();
		LOGGER.warn("查询结果为:{}",JSON.toJSONString(selectAll,true));
	}
	
	@Test
	public void testSelectAppIdByAppKey() throws Exception {
		Assert.assertNotNull(appMapper);
		String appKey="645ba616-370a-43a8-a8e0-993e7a590cf0";
		String appSecret="bb74abb6-bae0-47dd-a7b1-9571ea3a0f33";
		Long result = appMapper.selectAppIdByAppParam(appKey,appSecret);
		LOGGER.warn("查询结果为:{}",JSON.toJSONString(result,true));
	}

}
