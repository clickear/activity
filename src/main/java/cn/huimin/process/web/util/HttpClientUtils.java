package cn.huimin.process.web.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 2016/4/11.
 */
public class HttpClientUtils {
    private static final Logger logger = Logger
            .getLogger(HttpClientUtils.class);

    private boolean beEnCode = false; //是否加密

    public HttpClientUtils() {
    }

    public HttpClientUtils(boolean beEnCode) {
        this.beEnCode = beEnCode;
    }

    public boolean isBeEnCode() {
        return beEnCode;
    }

    public void setBeEnCode(boolean beEnCode) {
        this.beEnCode = beEnCode;
    }

    public String getHttpParams(String url, JSONObject params) {
        if(beEnCode){
            //如果编码缺少包
            return null;
        } else {
            return getHttpParamsWithOutEncode(url, params);
        }
    }

    public String getHttpParamsByGet(String url, JSONObject params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = null;
        if (params != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?");
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() == null) continue;
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            String paramStr = stringBuilder.substring(0, stringBuilder.length() - 1);
            logger.info("接口调用请求URL:" + url + " " + "接口调用参数：" + paramStr);
            httpGet = new HttpGet(url + paramStr);
        } else {
            logger.info("调用参数为空时，接口调用请求URL:" + url);
            httpGet = new HttpGet(url);
        }
        try {
            long startTime = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(httpGet);
            long endTime = System.currentTimeMillis();
            logger.info("调用" + url + "花费时间(单位：毫秒)：" + (endTime - startTime));
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.error("接口调用异常", e);
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("接口调用异常", e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static String getHttpParamsWithOutEncode(String url, JSONObject params) {
        StringBuilder loggerBuilder = new StringBuilder();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost;

        httpPost = new HttpPost(url);
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList();
            if (params != null) {
                for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    Object value = stringObjectEntry.getValue();
                    nameValuePairs.add(new BasicNameValuePair(key, value instanceof String ? (String) value : String.valueOf(value)));
                    loggerBuilder.append(stringObjectEntry.getKey()).append(":").append(stringObjectEntry.getValue()).append(",");
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
            logger.info("接口调用请求URL:" + url + " " + "接口调用参数：" + loggerBuilder.toString());
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            logger.error("接口调用异常", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("接口调用异常", e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
