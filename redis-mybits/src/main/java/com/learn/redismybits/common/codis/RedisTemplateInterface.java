package com.learn.redismybits.common.codis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.geo.GeoRadiusParam;

/** redis模板接口类
 * @author wjguo
 * datetime 2016年9月14日 上午10:59:03  
 *
 */
public interface RedisTemplateInterface {
	public String set(String key, String value);
	
	public String set(String key, String value, String nxxx, String expx, long time);
	
	public String get(String key);
	
	public Long exists(String... keys);
	
	public Boolean exists(String key);
	
	public Long del(String... keys);
	
	public Long del(String key);
	
	public String type(String key);
	
	public Set<String> keys(String pattern);
	
	public Long expire(String key, int seconds);
	
	public String setex(String key, int seconds, String value);
	
	public String set(byte[] key, byte[] value);
	
	public byte[] get(byte[] key);
	
	public Long hset(String key, String field, String value);
	
	public String hget(String key, String field);
	
	public String hmset(String key, Map<String, String> hash);
	
	public List<String> hmget(String key, String... fields);
	
	public Long hincrBy(String key, String field, long value);
	
	public Transaction multi();
	
	public String watch(String key);
	
	/**geo method**/
	
	/**
	 * GEOADD 命令每次可以添加一个或多个经纬度地理位置。 其中 location-set 为储存地理位置的集合， 
	 * 而 longitude 、 latitude 和 name 则分别为地理位置的经度、纬度、名字
	 * @param key
	 * @param memberCoordinateMap : 位置：map.put("三产", new GeoCoordinate(116.300101, 39.881244));
	 * @return
	 */
	public Long geoadd(String key,Map<String, GeoCoordinate> memberCoordinateMap);
	
	/**
	 * 输入位置的名字并取得位置的具体经纬度
	 * @param key
	 * @param members 名称
	 * @return
	 */
	public List<GeoCoordinate> geopos(String key,String... members);
	
	/**
	 *  计算差距的地点 location-x 和 location-y ， 以及储存这两个地点的地理位置集合。
	 * @param key
	 * @param member1
	 * @param member2
	 * @param unit
	 * @return
	 */
	public Double geodist(String key, String member1, String member2, GeoUnit unit);
	
	/**
	 * 以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素。
	 * @param key
	 * @param longitude
	 * @param latitude
	 * @param radius
	 * @param unit
	 * @param param
	 * @return
	 */
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit);
	
	
}
