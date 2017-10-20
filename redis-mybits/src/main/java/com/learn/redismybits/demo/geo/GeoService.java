package com.learn.redismybits.demo.geo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;

import com.learn.redismybits.common.KeyGen;
import com.learn.redismybits.common.codis.JodisTemplate;

@Service
public class GeoService {

	@Autowired
	private JodisTemplate jodisTemplate;
	
	private String key = KeyGen.GEO_TOUR.genKey();
	
	/**
	 * 初始化geo数据
	 */
	public void init(){
		Map<String, GeoCoordinate> memberCoordinateMap = new HashMap<String,GeoCoordinate>();
		memberCoordinateMap.put("三产", new GeoCoordinate(116.300101, 39.881244));
        memberCoordinateMap.put("张三", new GeoCoordinate(116.403963, 39.915119));
        memberCoordinateMap.put("李四", new GeoCoordinate(116.194428, 39.995692));
        memberCoordinateMap.put("王五", new GeoCoordinate(116.278905, 40.000224));
        
		jodisTemplate.geoadd(key, memberCoordinateMap);
	}
	
	/**
	 * 新增geo数据
	 * @param key
	 * @param memberCoordinateMap
	 * @return
	 */
	public Long geoAdd(String key,Map<String, GeoCoordinate> memberCoordinateMap){
		return jodisTemplate.geoadd(key, memberCoordinateMap);
	}
	
	/**
	 * 根据名称获取具体的经纬度
	 * @param members
	 * @return
	 */
	public List<GeoCoordinate> geopos(String key,String... members){
		return jodisTemplate.geopos(key, members);
	}
	
	/**
	 * 查询某个人到某个人的距离
	 * @param key
	 * @param member1
	 * @param member2
	 * @param unit
	 * @return
	 */
	public Double geodist(String key, String member1, String member2, GeoUnit unit){
		return jodisTemplate.geodist(key, member1, member2, unit);
	}
	
	/**
	 *  查询某个人附近的人
	 * @param key
	 * @param member 某个人
	 * @param radius 多少范围内
	 * @return
	 */
	public List<GeoRadiusResponse> around(String key,String member,Double radius ){
		List<GeoCoordinate> geoCoordinateList = jodisTemplate.geopos(key, member);
		GeoCoordinate geoCoordinate = geoCoordinateList.get(0);
		double lon = geoCoordinate.getLongitude();
		double lat = geoCoordinate.getLatitude();
		return jodisTemplate.georadius(key, lon, lat, radius, GeoUnit.KM);
	}
	
	public void delPos(String key,String...members){
		
	}
}
