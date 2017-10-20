排行榜实现

功能说明
1、用redis存储用户
2、对用户进行评分
3、每个用户对一个用户只能评分一次
4、用户分类(打标签)
5、对分类用户进行月排行(TAG_TOP_SCORE来记录)
6、根据分数对用户进行日排行、周排行、月排行(用DATA_TOP_SCORE\WEEK_TOP_SCORE\MONTH_TOP_SCORE等key来记录，第次有新的分数产生分别往这三个key进行zincrby操作)

数据结构选择
1、用map存储用户
2、用set记录用户只能评分一次
3、用redis的incr来实现用户id的自增
4、用zset来记录用户的分数
5、用set来记录标签里有哪里用户
