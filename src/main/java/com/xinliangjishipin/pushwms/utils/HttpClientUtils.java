package com.xinliangjishipin.pushwms.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("text/xml; charset=UTF-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build();

    public Response post(String url, String json, String transDataType) throws IOException {
        RequestBody body = RequestBody.create("json".equals(transDataType) ? JSON : XML, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response xmlPost(String url, Map map) throws IOException {
        FormBody.Builder params=new FormBody.Builder();
        for (Object key : map.keySet()) {
            params.add(key.toString(), map.get(key).toString());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(params.build())
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
