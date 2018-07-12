package UtilDemo.HttpUtil;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Http-Get请求工具类
 */
public class HttpGetDemo {
    public static String httpForGet(String url) throws IOException {
        Document jsoup = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).get();
        Elements title = jsoup.select("body");
        return title.get(0).text();
    }

    public static String httpForGet1(String url) {
        try {
            Document jsoup = Jsoup.connect(url).timeout(3000).ignoreContentType(true).maxBodySize(0).get();
            Elements title = jsoup.select("body");
            return title.get(0).text();
        } catch (IOException e) {
            try {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 1080));
                Document jsoup = Jsoup.connect(url).proxy(proxy).ignoreContentType(true).maxBodySize(0).get();
                Elements title = jsoup.select("body");
                return title.get(0).text();
            } catch (IOException e2) {
                return "获取数据失败";
            }
        }
    }

    // 获取ip地址
    public static String getInternetIp() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements()) {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()) {
                    ip = addrs.nextElement();
                    if (ip != null && ip instanceof Inet4Address && ip.isSiteLocalAddress()) {
                        return ip.getHostAddress();
                    }
                }
            }
            // 没有ip就返回UUId
            return UUID.randomUUID().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

/*    public static Map<String, String> httpForGet2(String url) {
        Map<String, String> map = new HashMap<>();
        try {
            Unirest.setProxy(null);
            HttpResponse<String> response = Unirest.get(url).asString();
            map.put("data", response.getBody().toString());
            map.put("code", response.getStatus() + "");
            return map;
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            map.put("data", "coinmarket服务器错误");
            map.put("code", "400");
            return map;
        }
    }*/

/*    public static String httpForPostByForm(String... params) {
        try {
            String url = params[0];
            String param = params[1];
            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type",
                            params.length > 2 ? params[2] : "application/x-www-form-urlencoded")
                    .body(param).asString();
            return response.getBody().toString();
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "获取数据失败";
        }
    }*/

    /**
     *
     * @param params url appkey method app_master_secret
     * @return
     */
    // public static String getSign(String ...params) {
    // long timestamp = System.currentTimeMillis();
    // String url = params[0];
    // String postBosy = "{\"appkey\":" + params[1] +",\"timestamp\":" + timestamp +
    // ",\"type\":\"broadcast\",\"payload\":{\"body\":{\"ticker\":\"Hello
    // World\",\"title\":\"你好\",\"text\":\"来自友盟推送\",\"after_open\":\"go_app\"},\"display_type\":\"notification\"}";
    // String method = params[2];
    // String app_master_secret = params[3];
    //
    // return Code.MD5(method+url+postBosy+app_master_secret);
    // }
}
