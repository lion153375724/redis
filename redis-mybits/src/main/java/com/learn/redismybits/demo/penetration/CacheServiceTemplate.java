package com.learn.redismybits.demo.penetration;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.learn.redismybits.mapper.OrderMapper;

@Service
public class CacheServiceTemplate {
	private static final Logger logger = LoggerFactory.getLogger(CacheServiceTemplate.class);
	
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	public <T> T findCache(String key,long expired,TimeUnit timeUnit,
						TypeReference<T> clazz,
						CacheLoadable<T> cacheLoadable){
		ValueOperations ops = redisTemplate.opsForValue();
		String jasonStr = String.valueOf(ops.get(key));
		
		if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
			logger.info("----------------------find by Cache-------------------");
			return JSON.parseObject(jasonStr, clazz);
		}else{
			//zk分布式锁
			String ips = "10.17.1.234:2181,10.17.1.235:2181,10.17.1.236:2181";  
			DistributedLock zkLock = new DefaultDistributedLock(ips, Thread.currentThread().getId() + "");  
			try {
				if(zkLock.dLock(2000)){
					long start = System.currentTimeMillis();
					//业务处理
					logger.debug(Thread.currentThread().getName()+":抢锁成功,执行相关业务流程Start");
					jasonStr = String.valueOf(ops.get(key));
					if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
						logger.info(Thread.currentThread().getName()+"----------------------find by Cache-------------------");
						return JSON.parseObject(jasonStr, clazz);
					}else{
						logger.info(Thread.currentThread().getName()+"----------------------find by DB-------------------");
						T result = cacheLoadable.load();
						System.out.println("CCCCCCCCCCCCCCCCCCCCCCC:"+JSON.toJSONString(result));
						ops.set(key, JSON.toJSONString(result), expired,timeUnit);
						return result;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(Thread.currentThread().getName()+"####################");
			}finally{
				zkLock.unDLock();
			}
		}
		return null;
	}
}
