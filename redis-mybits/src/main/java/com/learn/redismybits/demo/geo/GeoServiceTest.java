package com.learn.redismybits.demo.geo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;

import com.alibaba.fastjson.JSON;
import com.learn.redismybits.RedisMybitsApplication;
import com.learn.redismybits.common.KeyGen;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class GeoServiceTest {

	@Autowired
	private GeoService geoService;
	private String key = KeyGen.GEO_TOUR.genKey();
	
	@Test
	public void init(){
		geoService.init();
	}
	
	@Test
	public void geoAdd(){
		Map<String,GeoCoordinate> memberCoordinateMap = new HashMap<String,GeoCoordinate>();
		memberCoordinateMap.put("赵六", new GeoCoordinate(116.461225, 39.925014));
		System.out.println(geoService.geoAdd(key, memberCoordinateMap));
	}
	
	@Test
	public void geopos(){
		List<GeoCoordinate> list = geoService.geopos(key, "赵六","李四");
		for(GeoCoordinate geo : list){
			System.out.println(geo.getLatitude()+":" + geo.getLongitude());
		}
	}
	
	@Test
	public void geodist(){
		System.out.println(geoService.geodist(key, "赵六", "李四", GeoUnit.KM));
	}
	
	@Test
	public void around(){
		List<GeoRadiusResponse> list = geoService.around(key, "赵六", 2000.00);
		System.out.println(JSON.toJSONString(list));
		for(GeoRadiusResponse resp : list){
			System.out.println(resp.getMember());
		}
	}
	
	@Test
	public void delete(){
		
	}
}
