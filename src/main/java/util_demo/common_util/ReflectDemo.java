package util_demo.common_util;

/**
 * @author wanzhangkai@foxmail.com
 * @date 2018/6/27 11:22
 */
public class ReflectDemo {

    public static void main(String[] args) {
        String className = "summary_of_knowledge.reflect_demo.Test";
        Class cl = null;
        try {
            cl = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Test o = (Test) cl.newInstance();
            System.out.println(o);
            System.out.println(o.test());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}