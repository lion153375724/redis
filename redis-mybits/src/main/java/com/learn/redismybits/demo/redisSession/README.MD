通过redis实现分布式session共享


问题：如果报以下错误：

spring-session中间件需要依赖redis2.8.0以上版本，并且需要开启：notify-keyspace-events  gxE；
如果spring-session使用的是redis集群环境，且redis集群环境没有开启Keyspace notifications功能，则应用启动时会抛出如下异常：
Context initialization failed org.springframework.beans.factory.BeanCreationException: Error creating bean with name'enableRedisKeyspaceNotificationsInitializer' defined in classpath resource [org/springframework/session/data/redis/config/annotation/web/http/RedisHttpSessionConfiguration.class]: Invocation of initmethod failed; nested exception isjava.lang.IllegalStateException: Unable to configure Redis tokeyspace notifications. See http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedeventCausedby: redis.clients.jedis.exceptions.JedisDataException: ERR unknown command config

官方文档解决方案：
https://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent

官方文档中说Keyspace notifications功能默认是关闭的（默认地，Keyspace 时间通知功能是禁用的，因为它或多或少会使用一些CPU的资源），
我们需要打开它。打开的方法也很简单，配置属性：notify-keyspace-events gxE。

也可以执行：
redis-cli config set notify-keyspace-events Egx

也可以：
@Bean
public static ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
}