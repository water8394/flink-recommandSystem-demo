package com.demo;

import com.demo.client.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 *
 * @author wangxc
 * @date: 2019/7/19 下午10:46
 *
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisClientTest {

	@Resource
	private RedisClient redisClient;

	@Test
	public void testGet() {
		redisClient.getData("1");
	}

	@Test
	public void testSet() {
		redisClient.setData("1", "1");
	}

}
