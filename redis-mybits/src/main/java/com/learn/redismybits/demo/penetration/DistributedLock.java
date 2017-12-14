package com.learn.redismybits.demo.penetration;

public interface DistributedLock {
	/** 
     * 获取分布式锁 
     * @return 
     */  
    boolean dLock();  
  
    /** 
     * 在一定时间内获取分布式锁 ,-1的话一直执行完，走到所有线程获取锁
     * @param time 
     * @return 
     */  
    boolean dLock(long time);  
  
    /** 
     * 释放分布式锁 
     */  
    void unDLock();  
}
