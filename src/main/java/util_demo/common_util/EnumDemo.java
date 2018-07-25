package util_demo.common_util;

/**
 * @author wanzhangkai@foxmail.com
 * @date 2018/4/12 19:21
 */
public enum EnumDemo {

    SUCCESS(0, "SUCCESS"),
    ERROR(0, "ERROR");
     final int code;
     final String desc;

    EnumDemo(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static void main(String[] args) {
        System.out.println(EnumDemo.ERROR.desc);
        System.out.println(EnumDemo.SUCCESS.code);
    }

}