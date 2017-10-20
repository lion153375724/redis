package com.learn.redismybits.conf;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySource("classpath:redis.properties")//先注释配置codis后打开
public class JodisPoolProxy {
	
	private Logger logger = LoggerFactory.getLogger(JodisPoolProxy.class);
	@Autowired
    private Environment env;
	private static JedisResourcePool jedisPool;
	private static JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

    @Bean
    public JedisPoolConfig initJedisPoolConfig(){
        jedisPoolConfig.setMaxTotal(Integer.parseInt(env.getProperty("jedis.maxTotal")));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(env.getProperty("jedis.maxIdle")));
        jedisPoolConfig.setMinIdle(Integer.parseInt(env.getProperty("jedis.minIdle")));
        jedisPoolConfig.setNumTestsPerEvictionRun(Integer.parseInt(env.getProperty("jedis.numTestsPerEvictionRun")));
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Long.parseLong(env.getProperty("jedis.timeBetweenEvictionRunsMillis")));
        jedisPoolConfig.setMinEvictableIdleTimeMillis(Long.parseLong(env.getProperty("jedis.minEvictableIdleTimeMillis")));
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(Long.parseLong(env.getProperty("jedis.softMinEvictableIdleTimeMillis")));
        jedisPoolConfig.setMaxWaitMillis(Long.parseLong(env.getProperty("jedis.maxWaitMillis")));
        jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(env.getProperty("jedis.testOnBorrow")));
        jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(env.getProperty("jedis.testWhileIdle")));
        jedisPoolConfig.setTestOnReturn(Boolean.parseBoolean(env.getProperty("jedis.maxTotal")));
        jedisPoolConfig.setJmxEnabled(Boolean.parseBoolean(env.getProperty("jedis.testOnReturn")));
        jedisPoolConfig.setBlockWhenExhausted(Boolean.parseBoolean(env.getProperty("jedis.blockWhenExhausted")));
        jedisPoolConfig.setTestOnCreate(Boolean.parseBoolean(env.getProperty("jedis.testOnCreate")));
        return jedisPoolConfig;
    }

    @Bean
	public JedisResourcePool initRoundRobinJedisPool() {
    	jedisPool = RoundRobinJedisPool.create().poolConfig(jedisPoolConfig).curatorClient(env.getProperty("redis.zkAddr"), 30000).password(env.getProperty("redis.password")).zkProxyDir("/jodis/codis-gd").build();
		return jedisPool;
    }

	public Jedis getJedis() {
        for (int i = 1; i < 4; i++) {
          try {
            Jedis jedis = jedisPool.getResource();
            if (jedis.isConnected()) {
              return jedis;
            }
          } catch (Exception e) {
            logger.error("get jedis "+i+" time failer*****************************************");
          }
        }
		return null;
	}
}
