package com.learn.redismybits.common.codis;

import org.springframework.stereotype.Service;

import com.learn.redismybits.common.SerializeUtil;


@Service
public class JodisTemplate extends AbstractJodisTemplate {

	/**
	 * redis 序列化转成object
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(byte[] key) {
		byte[] bytes = super.get(key);
		Object obj =null;
		if (bytes!=null && bytes.length>0) {
			obj= SerializeUtil.unserialize(bytes);
		}
		return obj;
	}
}
