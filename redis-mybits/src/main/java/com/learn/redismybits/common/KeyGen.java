package com.learn.redismybits.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 用于管理redis中的key
 * @author jason
 * @createTime 2017年10月13日上午10:43:18
 */
public enum KeyGen {
	/**
	 * 获取标签数据的key
	 */
	TAG_TOP_SCORE{
		private final String key = "TAG_TOP_SCORE";
		
		//生成key: TAG_TOP_SCORE:A /TAG_TOP_SCORE:B 
		public String genKey(Object target){
			return key + split + target;
		}
		
		//生成key: TAG_TOP_SCORE:2017-10:A / TAG_TOP_SCORE:2017-10:B 
		public String genKeyWithDate(Object target,Date date){
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM");
			return key + split + sdf.format(date) + split + target;
		}
	},
	
	/**
	 * 获取日排行榜的key
	 */
	DATE_TOP_SCORE{
		private final String key = "DATE_TOP_SCORE";
		
		//生成key:DATE_TOP_SCORE:2017-10-16
		public String genKeyByDate(Date date){
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");  
			return key + split + sdf.format(date);
		}
	},
	
	/**
	 * 获取周标签排行榜的key
	 */
	WEEK_TOP_SCORE{
		private final String key = "WEEK_TOP_SCORE";
		
		//生成key:WEEK_TOP_SCORE:2017-10:1 / WEEK_TOP_SCORE:2017-10:2
		public String genKeyByDate(Date date){
			//Date date = sdf.parse(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return key + split + sdf.format(date) + split + calendar.get(Calendar.WEEK_OF_MONTH);
		}
	},
	
	/**
	 * 获取月标签排行榜的key
	 */
	MONTH_TOP_SCORE{
		private final String key = "MONTH_TOP_SCORE";
		
		//生成key:MONTH_TOP_SCORE:2017-10 / MONTH_TOP_SCORE:2017-11
		public String genKeyByDate(Date date){
			//Date date = sdf.parse(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			return key + split + sdf.format(date);
		}
	},
	
	/**
	 * 用户member的key
	 */
	MEMBER_MAP{
		private final String key = "MEMBER_MAP";
		
		//生成的key：MEMBER_MAP:1 / MEMBER_MAP:2
		public String genKey(Object target){
			return key + split + target;
		}
	},
	
	/**
	 * 获取已打过分的key
	 */
	SCORED_MEMBER_SET{
		private final String key = "SCORED_MEMBER_SET";
		
		//生成的key：SCORED_MEMBER_SET:1 / SCORED_MEMBER_SET:2
		public String genKey(Object target){
			return key + split + target;
		}
	},
	/**
	 * 获取已打过分的key
	 */
	TAG_MEMBER_SET{
		private final String key = "TAG_MEMBER_SET";
		
		//生成的key：TAG_MEMBER_SET:1 / SCORED_MEMBER_SET:2
		public String genKey(Object target){
			return key + split + target;
		}
	},
	/**
	 * geo key
	 */
	GEO_TOUR{
		private final String key = "geo_tour";
		
		public String genKey(){
			return key;
		} 
	}
	;
	
	private final static String split = ":";
	
	public String genKey(){
		throw new AbstractMethodError();
	}
	
	public String genKey(Object target){
		throw new AbstractMethodError();
	}
	
	public String genKeyWithDate(Object target,Date date){
		throw new AbstractMethodError();
	}
	
	public String genKeyByDate(Date date){
		throw new AbstractMethodError();
	}
}
