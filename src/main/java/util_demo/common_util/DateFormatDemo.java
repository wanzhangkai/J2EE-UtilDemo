package util_demo.common_util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author wanzhangkai@foxmail.com
 * @date 2018/5/4 14:36
 */
public class DateFormatDemo {

    public static void main(String[] args) {
        //获得服务器时间
        long t = System.currentTimeMillis();

        //获得日期
        Date date = new Date();
        System.out.println(date.getTime());

        //简单时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String time1 = sdf.format(date.getTime());
        System.out.println(time1);

        System.out.println(sdf.format(t));

    }
}