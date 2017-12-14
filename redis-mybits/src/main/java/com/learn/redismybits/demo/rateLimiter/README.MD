基于 Redis 实现分布式应用限流

2017-09-08 数据库开发
（点击上方公众号，可快速关注）

来源：冷冷gg
my.oschina.net/giegie/blog/1525931
如有好文章投稿，请点击 → 这里了解详情

限流的目的是通过对并发访问/请求进行限速或者一个时间窗口内的的请求进行限速来保护系统，一旦达到限制速率则可以拒绝服务。

前几天在DD的公众号，看了一篇关于使用 瓜娃 实现单应用限流的方案，参考《redis in action》 实现了一个jedis版本的，都属于业务层次限制。 实际场景中常用的限流策略：

Nginx接入层限流
按照一定的规则如帐号、IP、系统调用逻辑等在Nginx层面做限流
业务应用系统限流
通过业务代码控制流量这个流量可以被称为信号量，可以理解成是一种锁，它可以限制一项资源最多能同时被多少进程访问。

代码实现

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;
 
import java.util.List;
import java.util.UUID;
 
/**
* @email wangiegie@gmail.com
* @data 2017-08
*/
public class RedisRateLimiter {
    private static final String BUCKET = "BUCKET";
    private static final String BUCKET_COUNT = "BUCKET_COUNT";
    private static final String BUCKET_MONITOR = "BUCKET_MONITOR";
 
    static String acquireTokenFromBucket(
            Jedis jedis, int limit, long timeout) {
        String identifier = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Transaction transaction = jedis.multi();
 
        //删除信号量
        transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), "-inf".getBytes(), String.valueOf(now - timeout).getBytes());
        ZParams params = new ZParams();
        params.weightsByDouble(1.0,0.0);
        transaction.zinterstore(BUCKET, params, BUCKET, BUCKET_MONITOR);
 
        //计数器自增
        transaction.incr(BUCKET_COUNT);
        List<Object> results = transaction.exec();
        long counter = (Long) results.get(results.size() - 1);
 
        transaction = jedis.multi();
        transaction.zadd(BUCKET_MONITOR, now, identifier);
        transaction.zadd(BUCKET, counter, identifier);
        transaction.zrank(BUCKET, identifier);
        results = transaction.exec();
        //获取排名，判断请求是否取得了信号量
        long rank = (Long) results.get(results.size() - 1);
        if (rank < limit) {
            return identifier;
        } else {//没有获取到信号量，清理之前放入redis 中垃圾数据
            transaction = jedis.multi();
            transaction.zrem(BUCKET_MONITOR, identifier);
            transaction.zrem(BUCKET, identifier);
            transaction.exec();
        }
        return null;
    }
}

调用

测试接口调用
@GetMapping("/")
public void index(HttpServletResponse response) throws IOException {
    Jedis jedis = jedisPool.getResource();
    String token = RedisRateLimiter.acquireTokenFromBucket(jedis, LIMIT, TIMEOUT);
    if (token == null) {
        response.sendError(500);
    }else{
        //TODO 你的业务逻辑
    }
    jedisPool.returnResource(jedis);
}

优化

使用拦截器 + 注解优化代码

拦截器

@Configuration
static class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    private Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);
    @Autowired
    private JedisPool jedisPool;
 
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                     Object handler) throws Exception {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
 
                if (rateLimiter != null){
                    int limit = rateLimiter.limit();
                    int timeout = rateLimiter.timeout();
                    Jedis jedis = jedisPool.getResource();
                    String token = RedisRateLimiter.acquireTokenFromBucket(jedis, limit, timeout);
                    if (token == null) {
                        response.sendError(500);
                        return false;
                    }
                    logger.debug("token -> {}",token);
                    jedis.close();
                }
                return true;
            }
        }).addPathPatterns("/*");
    }
}

定义注解

/**
* @email wangiegie@gmail.com
* @data 2017-08
* 限流注解
*/
 
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int limit() default 5;
    int timeout() default 1000;
}

使用

@RateLimiter(limit = 2, timeout = 5000)
@GetMapping("/test")
public void test() {
}

并发测试

工具：apache-jmeter-3.2

说明： 没有获取到信号量的接口返回500，status是红色，获取到信号量的接口返回200，status是绿色。

当限制请求信号量为2，并发5个线程：


当限制请求信号量为5，并发10个线程：



总结

对于信号量的操作，使用事务操作。
不要使用时间戳作为信号量的排序分数，因为在分布式环境中，各个节点的时间差的原因，会出现不公平信号量的现象。
可以使用把这块代码抽成@rateLimiter注解，然后再方法上使用就会很方便啦
不同接口的流控，可以参考源码的里面RedisRateLimiterPlus,无非是每个接口生成一个监控参数
源码http://git.oschina.net/boding1/pig-cloud