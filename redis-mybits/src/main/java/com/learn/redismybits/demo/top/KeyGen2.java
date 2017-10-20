package com.learn.redismybits.demo.top;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p>用于管理 Redis 中 Key</p>
 *
 * @author 三产
 * @version 1.0
 * @date 2017-09-03
 * @QQGroup 213732117
 * @website http://www.coderknock.com
 * @copyright Copyright 2017 拿客 coderknock.com  All rights reserved.
 * @since JDK 1.8
 */
public enum KeyGen2 {
    /**
     * 获取标签排行榜的 key
     */
    TAG_TOP_SCORE {
        private final String key = "TAG_TOP_SCORE";

        public String genKey(Object tag) {
            return key + split + String.valueOf(tag);
        }

        public String genKeyWithDate(Object tag, LocalDateTime time) {
            return key + split + time.format(monthSdf) + split + String.valueOf(tag);
        }
    },
    /**
     * 获取排行榜的 key 天
     */
    DATE_TOP_SCORE {
        private final String key = "DATE_TOP_SCORE";

        public String genDateKey(LocalDateTime time) {
            return key + split + time.format(sdf);
        }
    },

    /**
     * 获取排行榜的 key 按时间
     */
    WEEK_TOP_SCORE {
        private final String key = "WEEK_TOP_SCORE";

        public String genDateKey(LocalDateTime time) {
            Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
            aCalendar.setTime(new Date(time.getLong(ChronoField.MILLI_OF_SECOND)));
            //设置周一为一周的开始
            aCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            return key + split + time.format(monthSdf) + split + aCalendar.get(Calendar.WEEK_OF_MONTH);
        }
    },

    /**
     * 获取排行榜的 key 按时间
     */
    MONTH_TOP_SCORE {
        private final String key = "MONTH_TOP_SCORE";

        public String genDateKey(LocalDateTime time) {
            return key + split + time.format(monthSdf);
        }
    },

    /**
     * 获取排行榜的 key 按时间
     */
    GEO {
        private final String key = "GEO";

        public String genKey(Object obj) {
            return key + split + String.valueOf(obj);
        }
    },
    /**
     * 获取排行榜的 key 按时间
     */
    GEO_TIME {
        private final String key = "GEO_TIME";

        public String genKey(Object groupId) {
            return key + split + String.valueOf(groupId);
        }
    };


    private final static DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter monthSdf = DateTimeFormatter.ofPattern("yyyy-MM");
    private final static String split = ":";

    public String genKey(Object obj) {
        throw new AbstractMethodError();
    }

    public String genDateKey(LocalDateTime time) {
        throw new AbstractMethodError();
    }

    public String genKeyWithDate(Object obj, LocalDateTime time) {
        throw new AbstractMethodError();
    }
}
