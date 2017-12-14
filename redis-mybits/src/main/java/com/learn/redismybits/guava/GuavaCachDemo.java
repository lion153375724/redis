package com.learn.redismybits.guava;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class GuavaCachDemo {
	private static ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
	.setNameFormat("pool-localcache-async-%s").build());
	private LoadingCache<String,Man> loadingCache;
	Logger logger = LoggerFactory.getLogger("LoadingCache");

    //loadingCache
    public void InitLoadingCache() {
        //指定一个如果数据不存在获取数据的方法
        CacheLoader<String, Man> cacheLoader = new CacheLoader<String, Man>() {
        	/**
        	 * guava第一次查询缓存没有，会到mysql查询数据,第二次会直接从缓存取
        	 */
            @Override
            public Man load(String key) throws Exception {
                //模拟mysql操作
                
                logger.info("LoadingCache测试 从mysql查询出数据,加载缓存ing...(1s)");
                Thread.sleep(1000);
                
                Man tmpman = new Man();
                tmpman.setId(key);
                tmpman.setName("其他人");
                if (key.equals("001")) {
                    tmpman.setName("张三");
                }
                if (key.equals("002")) {
                    tmpman.setName("李四");
                }
                logger.info("LoadingCache测试 从mysql加载缓存成功");
                //从数据库return值，guava会加载对应的key及return值到缓存
                return tmpman;
            }
            
            /**
             * 当调用refresh方法刷新缓存时,Guava先使用旧的缓存oldValue提供对外的查询,然后异步重新构建新的缓存
             */
            @Override
            public ListenableFuture<Man> reload(String key,
					Man oldValue) throws Exception {
				ListenableFutureTask<Man> task = ListenableFutureTask
						.create(new Callable<Man>() {
							@Override
							public Man call() throws Exception {
								try {
									//Man obj= load(key);
									Man tmpman = new Man();
							        tmpman.setId(key);
							        tmpman.setName("002李四变成了王五");
									loadingCache.put(key, tmpman);
									logger.info("reload 构建缓存成功！");
									return tmpman;
								} catch (Exception e) {
									logger.info("reload 构建缓存失败！");
								}
								return oldValue;
							}
						});
				pool.execute(task);
				return task;

			}
        };
        //缓存数量为1，为了展示缓存删除效果
       loadingCache = CacheBuilder.newBuilder().maximumSize(100).build(cacheLoader);
    }
    //获取数据，如果不存在返回null
    public Man getIfPresentloadingCache(String key){
        return loadingCache.getIfPresent(key);
    }
    //获取数据，如果数据不存在则通过cacheLoader获取数据，缓存并返回
    public Man getCacheKeyloadingCache(String key){
        try {
            return loadingCache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    //直接向缓存put数据
    public void putloadingCache(String key,Man value){
        
        logger.info("put key :{} value : {}",key,value.getName());
        loadingCache.put(key,value);
    }
    
    //清除缓存
    public void removeloadingCache(String key){
    	logger.info("remove key:{}",key);
    	loadingCache.invalidate(key);
    }

    //刷新缓存
    public void refreshloadingCache(String key){
    	logger.info("refresh key :{}" + key);
    	loadingCache.refresh(key);
    }
}
