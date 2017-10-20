本文主要讲述如何在java里头使用redis进行cas操作。其实呢，redis不像memcached那样显示地支持cas操作，不过它有事务的概念。

准备
  ● redis的docker搭建
  ● SpringBoot应用之分布式缓存
redis的乐观锁支持

Redis通过使用WATCH, MULTI, and EXEC组成的事务来实现乐观锁(注意没有用DISCARD),Redis事务没有回滚操作。在SpringDataRedis当中通过RedisTemplate的SessionCallback中来支持(否则事务不生效)。discard的话不需要自己代码处理，callback返回null，成的话，返回非null，依据这个来判断事务是否成功(没有抛异常)。