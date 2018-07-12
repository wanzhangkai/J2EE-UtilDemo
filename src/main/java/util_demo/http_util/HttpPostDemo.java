package UtilDemo.HttpUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Bigone-K线
 * 官网分析：https://big.one/api/graphql
 * POST:
 * {
 * "operationName": null,
 * "variables": {
 * "marketId": "b7953385-109b-454f-b4da-b4a928712b70",
 * "period": "MIN5",
 * "startTime": "2018-07-04T07:26:21.000Z",
 * "endTime": "2018-07-10T07:27:21.000Z"
 * },
 * "query":
 * "query ($marketId: String!, $period: BarPeriod!, $startTime: DateTime, $endTime: DateTime) {\n  bars(marketUuid: $marketId, period: $period, startTime: $startTime, endTime: $endTime, order: DESC, limit: 1000) {\n    time\n    open\n    high\n    low\n    close\n    volume\n    __typename\n  }\n}\n"
 * }
 *
 * @author wanzhangkai@foxmail.com
 * @date 2018/7/10 16:33
 */
@Service
@Slf4j
public class HttpPostDemo {

    private static final String baseUrl = "https://big.one/api/graphql";
    private static Map<String, String> marketIdMap = new HashMap<>();

    // The user agent
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String CONTENT_TYPE = "application/json";
    // This object is used for sending the post request to plain
    private static HttpClient client = HttpClients.custom().build();

    public String getKline(Map<String, String> map) {
//        String limit = map.get("limit") != null ? map.get("limit") : "1000";
//        String since = map.get("since") != null ? map.get("since") : "0";
        String symbol = map.get("symbol").replace("_", "-").toUpperCase();
        String type = map.get("type") != null ? map.get("type") : "MIN1";
        String marketId = marketIdMap.get(symbol);  //ETH-USDT
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        String startTime = sdf1.format(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 60d);
        String endTime = sdf1.format(System.currentTimeMillis());
        String postJson = getPostJson(marketId, type, startTime, endTime);
        String response = "";
        try {
            response = httpPostClientSend(postJson, baseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //需要的顺序为：时 开 高 低 收 量
        List<List> finalResult = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("bars");
            for (int i = 0; i < jsonArray.size(); i++) {
                List<Object> tmp = new ArrayList<>();
                JSONObject jo = jsonArray.getJSONObject(i);
                String time = jo.getString("time");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = new Date();
                try {
                    date = sdf.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tmp.add(date.getTime());
                tmp.add(jo.getBigDecimal("open"));
                tmp.add(jo.getBigDecimal("high"));
                tmp.add(jo.getBigDecimal("low"));
                tmp.add(jo.getBigDecimal("close"));
                tmp.add(jo.getBigDecimal("volume"));
                finalResult.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("finalResult:" + finalResult);
        return finalResult.toString();
    }

    private static String getPostJson(String marketId, String type, String startTime, String endTime) {
        JSONObject jsonObject = new JSONObject() {{
            put("operationName", null);
            put("variables", new JSONObject() {{
                put("marketId", marketId);
                put("period", type);
                put("startTime", startTime);
                put("endTime", endTime);
            }});
            put("query", "query ($marketId: String!, $period: BarPeriod!, $startTime: DateTime, $endTime: DateTime) {\n  bars(marketUuid: $marketId, period: $period, startTime: $startTime, endTime: $endTime, order: DESC, limit: 1000) {\n    time\n    open\n    high\n    low\n    close\n    volume\n    __typename\n  }\n}\n");
        }};
        return jsonObject.toString();
    }

    //定时更新marketId
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public static void marketIdScheduled() {
        String text = HttpGetDemo.httpForGet1("https://big.one/api/v2/markets");
//        System.out.println("marketIdScheduled:" + text);
        JSONObject jsonObject = JSONObject.parseObject(text);
        JSONArray ja = jsonObject.getJSONArray("data");
        marketIdMap.clear();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            String uuid = jo.getString("uuid");
            String symbol = jo.getString("name");
            marketIdMap.put(symbol, uuid);
        }
    }

    @PostConstruct
    public void httpPostClientInit() {
        marketIdScheduled();

        //初始化配置
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(400);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(100);
        client = HttpClients.custom()
                // TODO: 2018/7/10 上线注释：代理
//                .setProxy(new HttpHost("localhost", 1080))
                .setConnectionManager(cm).build();
    }

    private static String httpPostClientSend(String postBody, String url) throws Exception {
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Content-Type", CONTENT_TYPE);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        // Send the post request and get the response
        HttpResponse response = client.execute(post);
        //处理返回状态
        int status = response.getStatusLine().getStatusCode();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        System.out.println("rd:"+rd.toString());
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println("result:" + result.toString());
        if (status == 200) {
//            log.info("请求成功");
        } else {
            log.info("请求失败");
        }
        return result.toString();
    }

    public static void main(String[] args) {
//        String time = "2018-07-10T10:15:00.000000Z";
//        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
//        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date date = new Date();
//        try {
//            date = df1.parse(time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(date.getTime());
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
//        System.out.println(sdf.format(System.currentTimeMillis() - 1000 * 60 * 60 * 8));

        HttpPostDemo b = new HttpPostDemo();
        marketIdScheduled();
        b.httpPostClientInit();
        Map<String, String> map = new HashMap<>();
        map.put("symbol", "BTC-USDT");
        map.put("type", "1min");
        b.getKline(map);
    }

}