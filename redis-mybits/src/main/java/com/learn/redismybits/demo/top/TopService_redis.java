package com.learn.redismybits.demo.top;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.learn.redismybits.common.KeyGen;

@Service
public class TopService_redis {

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 保存member
	 * 
	 * @param member
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public Long addMember(MemberBean member) throws IllegalArgumentException, IllegalAccessException {
		Long memberId = generateId();
		member.setId(memberId);
		Map<String,Object> memberMap = MemberToMap(member);
		String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId);
		HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
		hashOps.putAll(member_map_key, memberMap); //保存用户
		//给用户打标签
		addTag(memberId.toString(),member.getTag());
		//打分
		//addTop(memberId.toString(),memberId.toString(),member.getScore());
		return memberId;
	}
	
	private Long generateId(){
		ValueOperations<String, Long> valOps = redisTemplate.opsForValue();
		return valOps.increment("MEMBER_ID_SEQ", 1); //redis生成自增式id
	}

	private Map<String,Object> MemberToMap(MemberBean member) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = member.getClass().getDeclaredFields();
		Field field = null;
		for (int i = 0; i < fields.length; i++){
			
			field = fields[i];
			//属性不能是static和final
			if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())){
				field.setAccessible(true);//设置属性是可以访问的
				map.put(field.getName(), field.get(member).toString());
				System.out.println(field.getName()+":"+field.getType()+":"+field.get(member));
			}
		}
		return map;
	}

	/**
	 * 
	 * 
	 * 新增打分
	 */
	public void addTop(String memberId,String score_memberId,Integer score) {
		//记录投票人记录，一个用户对一个用户只允许投一次
		Long rs = scored_member(memberId,score_memberId); //如果成功返回1，否则不成功,表示已投过票
		if(1 == rs){
			MemberBean member = getMemberById(memberId);
			if(null != member){
				Date date = new Date();
				//归类tag的 key :TAG_DATA_SCORE:A
				String tag_top_score_key = KeyGen.TAG_TOP_SCORE.genKey(member.getTag());
				//归类tag 月排行key:生成key: TAG_TOP_SCORE:2017-10:A
				String tag_top_score_date_key =KeyGen.TAG_TOP_SCORE.genKeyWithDate(member.getTag(), date);
				//日排行key
				String data_top_score_key = KeyGen.DATE_TOP_SCORE.genKeyByDate(date);
				//周排行key
				String week_top_score_key = KeyGen.WEEK_TOP_SCORE.genKeyByDate(date);
				//月排行key
				String month_top_score_key = KeyGen.MONTH_TOP_SCORE.genKeyByDate(date);
				//获取会员key
				String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId); 
				
				//member字段score增加分数
				redisTemplate.opsForHash().increment(member_map_key, "score", score);
				
				redisTemplate.opsForZSet().incrementScore(tag_top_score_key, memberId, score);
				redisTemplate.opsForZSet().incrementScore(tag_top_score_date_key, memberId, score);
				redisTemplate.opsForZSet().incrementScore(data_top_score_key, memberId, score);
				redisTemplate.opsForZSet().incrementScore(week_top_score_key, memberId, score);
				//周记录，只保留7天
				redisTemplate.expire(week_top_score_key, 7, TimeUnit.DAYS);
				redisTemplate.opsForZSet().incrementScore(month_top_score_key, memberId, score);
			}
			
			
		}
		
	}

	/**
	 * 记录打分记录的人员
	 */
	public Long scored_member(String memberId,String score_memberId) {
		String scored_member_key = KeyGen.SCORED_MEMBER_SET.genKey(memberId);
		return redisTemplate.opsForSet().add(scored_member_key, score_memberId);
	}

	/**
	 * 给用户归类，打标签
	 */
	public void addTag(String memberId,String tag) {
		MemberBean member = getMemberById(memberId);
		if(null != member){
			//if(!tag.equals(member.getTag())){
				//获取会员key
				String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId); 
				//更新member的tag
				redisTemplate.opsForHash().put(member_map_key, "tag", tag);
				//删除原有tag_member_key的值
				String tag_member_key = KeyGen.TAG_MEMBER_SET.genKey(member.getTag());
				redisTemplate.opsForSet().remove(tag_member_key, memberId);
				//增加到新的tag
				tag_member_key = KeyGen.TAG_MEMBER_SET.genKey(tag);
				redisTemplate.opsForSet().add(tag_member_key, memberId);
				//新增到新tag_TOP_SCORE:TAG的值
				String tag_top_key = KeyGen.TAG_TOP_SCORE.genKey(tag);
				redisTemplate.opsForZSet().add(tag_top_key, memberId,member.getScore());
				//新增到新tag_TOP_SCORE:DATA:TAG的值
				String tag_top_date_key = KeyGen.TAG_TOP_SCORE.genKeyWithDate(tag, new Date());
				redisTemplate.opsForZSet().add(tag_top_date_key, memberId,member.getScore());
				
				//删除原有tag_TOP_SCORE:TAG的值
				tag_top_key = KeyGen.TAG_TOP_SCORE.genKey(member.getTag());
				redisTemplate.opsForZSet().remove(tag_top_key, memberId);
				
				//删除原有tag_TOP_SCORE:DATA:TAG的值
				tag_top_date_key = KeyGen.TAG_TOP_SCORE.genKeyWithDate(member.getTag(), new Date());
				redisTemplate.opsForZSet().remove(tag_top_date_key, memberId);
			}
		//}
	}

	public MemberBean getMemberById(String memberId) {
		String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId); //获取会员key
		Map<String,Object> data = redisTemplate.opsForHash().entries(member_map_key);
		MemberBean member = new MemberBean();
		try {
			BeanUtils.populate(member, data);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return member;
	}

	/**
	 * 删除人员
	 */
	public void delete(String memberId) {
		MemberBean member = getMemberById(memberId);
		if(null != member){
			Date date = new Date();
			//归类tag的 key :TAG_DATA_SCORE:A
			String tag_top_score_key = KeyGen.TAG_TOP_SCORE.genKey(member.getTag());
			//归类tag 月排行key:生成key: TAG_TOP_SCORE:2017-10:A
			String tag_top_score_date_key =KeyGen.TAG_TOP_SCORE.genKeyWithDate(member.getTag(), date);
			//日排行key
			String data_top_score_key = KeyGen.DATE_TOP_SCORE.genKeyByDate(date);
			//周排行key
			String week_top_score_key = KeyGen.WEEK_TOP_SCORE.genKeyByDate(date);
			//月排行key
			String month_top_score_key = KeyGen.MONTH_TOP_SCORE.genKeyByDate(date);
			
			//投标记录key
			String scored_member_key = KeyGen.SCORED_MEMBER_SET.genKey(memberId);
			//标签key
			String tag_member_key = KeyGen.TAG_MEMBER_SET.genKey(memberId);
			//人员key
			String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId);
			
			//开户事务
			redisTemplate.opsForZSet().remove(tag_top_score_key, memberId);
			redisTemplate.opsForZSet().remove(tag_top_score_date_key, memberId);
			redisTemplate.opsForZSet().remove(data_top_score_key, memberId);
			redisTemplate.opsForZSet().remove(week_top_score_key, memberId);
			redisTemplate.opsForZSet().remove(month_top_score_key, memberId);
			
			redisTemplate.opsForSet().remove(scored_member_key, memberId);
			redisTemplate.opsForSet().remove(tag_member_key, memberId);
			redisTemplate.delete(member_map_key);
			
		}
			
			
	}
	
	/**
	 * 根据key,分页查询
	 * @param page
	 * @param pageSize
	 * @param key
	 * @return
	 */
	private List<MemberBean> list_top_score(int page,int pageSize,String key) {
		int start = (page-1) * pageSize;
		int end = start + pageSize - 1;
		//Set<String> rsSet = redisTemplate.opsForZSet().range(key, start, end); //分数从小到大
		Set<String> rsSet = redisTemplate.opsForZSet().reverseRange(key, start, end); //分数从大到小
		//Set<String> rsSet2 = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
		
		List<MemberBean> rsList = rsSet.stream().map(new Function<String,MemberBean>(){
			@Override
			public MemberBean apply(String memberId) {
				String member_map_key = KeyGen.MEMBER_MAP.genKey(memberId);
				Map<String,Object> data = redisTemplate.opsForHash().entries(member_map_key);
				MemberBean bean = new MemberBean();
				try {
					BeanUtils.populate(bean, data);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return bean;
			}
			
		}).collect(Collectors.toList());
		return rsList;
	}

	/**
	 * 对分类用户进行月排行
	 */
	public List<MemberBean> listMemberByTag(int page,int pageSize,Object target) {
		String tag_top_key = KeyGen.TAG_TOP_SCORE.genKey(target);
		return list_top_score(page,pageSize,tag_top_key);
		
		
	}

	/**
	 * 所有记录的日排行
	 */
	public List<MemberBean> data_top_score(int page,int pageSize,Date date) {
		String date_top_key = KeyGen.DATE_TOP_SCORE.genKeyByDate(date);
		return list_top_score(page,pageSize,date_top_key);
	}

	/**
	 * 周排行
	 */
	public List<MemberBean> week_top_score(int page,int pageSize,Date date) {
		String week_top_key = KeyGen.WEEK_TOP_SCORE.genKeyByDate(date);
		return list_top_score(page,pageSize,week_top_key);
	}

	/**
	 * 月排行
	 */
	public List<MemberBean> month_top_score(int page,int pageSize,Date date) {
		String month_top_key = KeyGen.MONTH_TOP_SCORE.genKeyByDate(date);
		return list_top_score(page,pageSize,month_top_key);
	}
	
	

	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(KeyGen.TAG_TOP_SCORE.genKey("First"));
		System.out.println(KeyGen.TAG_TOP_SCORE.genKeyWithDate("First", date));
		System.out.println(KeyGen.DATE_TOP_SCORE.genKeyByDate(date));
		System.out.println(KeyGen.WEEK_TOP_SCORE.genKeyByDate(date));
		System.out.println(KeyGen.MONTH_TOP_SCORE.genKeyByDate(date));
	}
}
