Redis实战SpringBoot版本之投票服务

功能说明
  ● 用redis来存储文章
  ● 对文章投票
  ● 每个用户只能投一次
  ● 文章按投票和时间排行
  ● 文章打标签
数据结构选择
  ● 文章采用hash存储
  ● 用redis的incr来实现文章的自增id
  ● 用set来记录每篇文章已投票的用户
  ● 用zset存储文章与得分，创建时间
  ● 用set来存储标签里有哪些文章