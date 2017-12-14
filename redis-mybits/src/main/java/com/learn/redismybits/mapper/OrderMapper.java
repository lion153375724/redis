package com.learn.redismybits.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.learn.redismybits.common.MyMapper;
import com.learn.redismybits.dto.Order;

@Mapper
public interface OrderMapper extends MyMapper<Order> {
	
	/**
	 * 统计该 用户的交易总额
	 * @param uid
	 * @return
	 */
	Double countPriceByUid(String uid);
}
