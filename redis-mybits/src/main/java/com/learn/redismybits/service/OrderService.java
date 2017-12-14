package com.learn.redismybits.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.learn.redismybits.demo.penetration.CacheLoadable;
import com.learn.redismybits.demo.penetration.CacheServiceTemplate;
import com.learn.redismybits.dto.Order;
import com.learn.redismybits.mapper.OrderMapper;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private CacheServiceTemplate cacheServiceTemplate;
	private Lock lock = new ReentrantLock();
	public Double countPriceByUid(String uid){
		
		//方式1：普通的这种操作，如果缓存过期，会全部走数据库查询，造成雪蹦
		/*Double price = null;
		ValueOperations ops = redisTemplate.opsForValue();
		String jason = String.valueOf(ops.get("price_" + uid));
		if(!StringUtils.isEmpty(jason) && !jason.equals("null")){
			logger.info("----------------------find by Cache-------------------");
			price = JSON.parseObject(jason, Double.class);
		}else{
			price = orderMapper.countPriceByUid(uid);
			logger.info("----------------------find by DB-------------------");
			System.out.println(JSON.toJSONString(price));
			ops.set("price_" + uid, JSON.toJSONString(price));
		}
		return price;*/
		
		//防雪蹦处理开始
		//方案1、互斥锁
		ValueOperations ops = redisTemplate.opsForValue();
		String jasonStr = String.valueOf(ops.get("price_" + uid));
		Double price = null;
		
		if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
			logger.info("----------------------find by Cache-------------------");
			price = JSON.parseObject(jasonStr, Double.class);
			return price;
		}else{
			/*synchronized (this) {
				jasonStr = String.valueOf(ops.get("price_" + uid));
				if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
					logger.info("----------------------find by Cache-------------------");
					price = JSON.parseObject(jasonStr, Double.class);
					return price;
				}else{
					logger.info("----------------------find by DB-------------------");
					price = orderMapper.countPriceByUid(uid);
					//设置缓存
					ops.set("price_" + uid, JSON.toJSONString(price),1000, TimeUnit.SECONDS);
					return price;
				}
			}*/
			
			//普通锁
			/*try{
				lock.lock();
				jasonStr = String.valueOf(ops.get("price_" + uid));
				if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
					logger.info("----------------------find by Cache-------------------");
					price = JSON.parseObject(jasonStr, Double.class);
					return price;
				}else{
					logger.info("----------------------find by DB-------------------");
					price = orderMapper.countPriceByUid(uid);
					//设置缓存
					ops.set("price_" + uid, JSON.toJSONString(price),1000, TimeUnit.SECONDS);
					return price;
				}
			}catch (Exception e){
				
			}finally{
				lock.unlock();
			}
			return price;*/
			
			
			//zk分布式锁
			/*String ips = "10.17.1.234:2181,10.17.1.235:2181,10.17.1.236:2181";  
			DistributedLock zkLock = new DefaultDistributedLock2(ips, Thread.currentThread().getId() + "");  
			try {
				if(zkLock.dLock(2000)){
					long start = System.currentTimeMillis();
					//业务处理
					logger.debug(Thread.currentThread().getName()+":抢锁成功,执行相关业务流程Start");
					jasonStr = String.valueOf(ops.get("price_" + uid));
					if(!StringUtils.isEmpty(jasonStr) && !jasonStr.equals("null")){
						logger.info(Thread.currentThread().getName()+"----------------------find by Cache-------------------");
						price = JSON.parseObject(jasonStr, Double.class);
						return price;
					}else{
						logger.info(Thread.currentThread().getName()+"----------------------find by DB-------------------");
						price = orderMapper.countPriceByUid(uid);
						//设置缓存
						ops.set("price_" + uid, JSON.toJSONString(price),1000, TimeUnit.SECONDS);
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@:"+(System.currentTimeMillis()-start));
						return price;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(Thread.currentThread().getName()+"####################");
			}finally{
				zkLock.unDLock();
			}
			System.out.println(Thread.currentThread().getName()+"####################price"+price);
			return price;*/
			
			//封闭后调用
			return cacheServiceTemplate.findCache("price_" + uid, 1000, TimeUnit.SECONDS, 
									new TypeReference<Double>(){}, 
									new CacheLoadable<Double>(){
										@Override
										public Double load() {
											Double price = orderMapper.countPriceByUid(uid);
											System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
											return price;
										}
										
				
			});
			
			//return price;
		}
		
		
		
		
		//方案2：永不过期,value值为:value,逻辑过期时间
		/*ValueOperations ops = redisTemplate.opsForValue();
		String jasonStr = String.valueOf(ops.get("price_" + uid));
		Long logicTimeout = redisTemplate.getExpire("price_" + uid, TimeUnit.MILLISECONDS);
		if(logicTimeout <= System.currentTimeMillis()){
			//过期了,开始重构
		}*/
		
		//方案3：设置过期时间=固定过期+随机时间
		//防雪蹦处理结束
		
		//缓存穿透开始
		//方案1：布隆过滤器
		/*List<Order>orderList = orderMapper.selectAll();
		//构造一个布隆过滤器
		BloomFilter bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), orderList.size()+2000);
		for(Order order : orderList){
			bf.put(order.getUid());
		}
		
		//如果没有该值，直接返回
		if(!bf.mightContain(uid)){
			return null;
		}else{
			
		}*/
		//return orderMapper.countPriceByUid(uid);
	}
	
	public void save(Order order) {
        if (order.getId() != null) {
        	orderMapper.updateByPrimaryKey(order);
        } else {
        	orderMapper.insert(order);
        }
    }
	
}
