package util_demo.common_util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 保留有效位数
 *
 * @author wanzhangkai@foxmail.com
 * @date 2018/6/5 16:01
 */
public class NumberDemo {

    //处理小数
    public  void  priceUtil(){
        BigDecimal b1 = new BigDecimal(Double.toString(0.000005611), new MathContext(2, RoundingMode.DOWN));
        System.out.println(b1);
    }

    //完美处理小数问题
    public String priceCutUtil(Double d){
        String price = null;
        String priceTmp = String.valueOf(d);
        int indexTmp = -1;
        int spot = -1;
        if (priceTmp.contains("E")) {
            price = String.valueOf(d);
        } else {
            for (int i = 0; i < priceTmp.length(); i++) {
                if (priceTmp.charAt(i) != '0' && priceTmp.charAt(i) != '.' && indexTmp == -1) {
                    indexTmp = i;
                }
                if (priceTmp.charAt(i) == '.' && spot == -1) {
                    spot = i;
                }
            }
            if (indexTmp < spot) {
                try {
                    price = priceTmp.substring(indexTmp, spot + 3);
                } catch (Exception e) {
                    price = priceTmp;
                }
            } else {
                try {
                    price = priceTmp.substring(0, indexTmp + 2);
                } catch (Exception e) {
                    price = priceTmp;
                }
            }
        }
        return price;
    }

    public static void main(String[] args) {
        NumberDemo n = new NumberDemo();
        System.out.println(n.priceCutUtil(456.56120023));
        System.out.println(n.priceCutUtil(0.00000056));
        System.out.println(n.priceCutUtil(0.002345));
        System.out.println(n.priceCutUtil(356.000));
    }

}