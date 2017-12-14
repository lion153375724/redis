package com.learn.redismybits.guava;

public class Test {
	public static void main(String[] args){
        GuavaCachDemo cachDemo = new GuavaCachDemo();
        System.out.println("使用loadingCache");
        cachDemo.InitLoadingCache();

        System.out.println("使用loadingCache get 001方法  第一次加载");
        Man man = cachDemo.getCacheKeyloadingCache("001");
        System.out.println(man);

        
        System.out.println("\n使用loadingCache get 002方法  第一次加载");
        man = cachDemo.getCacheKeyloadingCache("002");
        System.out.println(man);
        
        cachDemo.refreshloadingCache("002");

        
        System.out.println("\n使用loadingCache get002方法  已加载过");
        man = cachDemo.getCacheKeyloadingCache("002");
        System.out.println(man);

        
        /*Man tmpman = new Man();
        tmpman.setId("003");
        tmpman.setName("其他人");
        cachDemo.putloadingCache("003", tmpman);
        
        System.out.println("\n使用loadingCache get 003方法  已加载过");
        man = cachDemo.getCacheKeyloadingCache("003");
        System.out.println(man);

        
        System.out.println("\n使用loadingCache getIfPresent方法  已加载过");
        man = cachDemo.getIfPresentloadingCache("001");
        System.out.println(man);*/

      
    }

}
