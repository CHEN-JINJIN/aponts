package com.apon.chen.common;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author chen
 */
@Component
public class HttpRequest {
    public boolean httpUrlCheck(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("HEAD");
            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            // 处理异常，可以选择打印日志或抛出异常
//            e.printStackTrace();
            return false;
        }
    }
}
