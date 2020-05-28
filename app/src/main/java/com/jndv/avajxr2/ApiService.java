package com.jndv.avajxr2;

import com.jndv.avajxr2.bean.BaseEntity;
import com.jndv.avajxr2.bean.ChatMessageToken;
import com.jndv.avajxr2.bean.PhoneInfo;
import com.jndv.avajxr2.bean.WeatherInfo;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 测试接口使用的是聚合数据提供的免费API
 * 地址：https://www.juhe.cn/
 */
public interface ApiService {
    //    用Retrofit需要后台接口遵循REST风格的请求
    //    请求示例：http://apis.juhe.cn/mobile/get?phone=1351103&key=您申请的KEY
    //    基本地址：http://apis.juhe.cn
    //    功能地址：/mobile/get
    //    请求参数：?phone=1351103&key=790c971125********190725627e5ca5

    String BASE_URL = "http://apis.juhe.cn/";
    String BASE_URL2 = "http://123.131.131.38:7020";

    //网络请求时长
    int HTTP_TIME = 5000;

    /**
     * 接口1：获取来电归属地, API一定是以 "/" 开头，这是Retrofit规范
     */
    @GET("mobile/get")
    Observable<BaseEntity<PhoneInfo>> getPhoneMsg(@Query("phone") String phone, @Query("key") String key);

    /**
     * 接口2：获取天气信息
     */
    @GET("/weather/index")
    Observable<BaseEntity<WeatherInfo>> getWeather(@QueryMap Map<String, String> options);

    /**
     * 接口3：请求token
     */
    @POST("/jndv/generateToken")
    Observable<BaseEntity<ChatMessageToken>> getChatMessageToken(@Body RequestBody requestBody);
}