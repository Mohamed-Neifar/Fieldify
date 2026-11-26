package com.example.fieldify.network;

import android.content.Context;
import android.text.TextUtils;

import com.example.fieldify.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.213:8081/";
    private static Retrofit retrofit = null;
    private static SessionManager sessionManager = null;

    public static void init(Context context) {
        if (sessionManager == null && context != null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
    }

    public static Retrofit getRetrofitInstance(Context context) {
        init(context);
        if (retrofit == null) {
            Interceptor authHeaderInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                if (sessionManager != null && sessionManager.isLoggedIn()) {
                    String token = sessionManager.getToken();
                    if (!TextUtils.isEmpty(token)) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                }
                return chain.proceed(builder.build());
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authHeaderInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApiService getAuthApiService() {
        return getRetrofitInstance(null).create(AuthApiService.class);
    }

    public static FieldifyApiService getFieldifyApiService() {
        return getRetrofitInstance(null).create(FieldifyApiService.class);
    }

    public static FieldifyApiService getFieldifyApiService(Context context) {
        return getRetrofitInstance(context).create(FieldifyApiService.class);
    }

    public static String getFullImageUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.trim().isEmpty()) {
            return "";
        }
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        String base = BASE_URL.endsWith("/") ? BASE_URL.substring(0, BASE_URL.length() - 1) : BASE_URL;
        String path = relativeUrl.startsWith("/") ? relativeUrl : "/" + relativeUrl;
        return base + path;
    }
}
