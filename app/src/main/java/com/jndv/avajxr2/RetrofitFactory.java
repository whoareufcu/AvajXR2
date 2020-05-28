package com.jndv.avajxr2;

import com.jndv.avajxr2.bean.ChatMessageToken;
import com.jndv.avajxr2.bean.PhoneInfo;
import com.jndv.avajxr2.bean.WeatherInfo;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private final ApiService mApiService;

    private static RetrofitFactory mRetrofitFactory;

    private RetrofitFactory() {
        //创建日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设定日志级别
        //创建OkHttpClient
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiService.HTTP_TIME, TimeUnit.SECONDS)
                .readTimeout(ApiService.HTTP_TIME, TimeUnit.SECONDS)
                .writeTimeout(ApiService.HTTP_TIME, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)//添加拦截器
                .build();

        //创建Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava2转换器
                .client(mOkHttpClient)
                .build();

        //创建接口实现类
        mApiService = retrofit.create(ApiService.class);

    }
    // 懒汉式单例模式
    public static RetrofitFactory getInstence() {
        if (mRetrofitFactory == null) {
            // 静态方法，使用当前类本身充当进程锁
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null)
                    mRetrofitFactory = new RetrofitFactory();
            }
        }
        return mRetrofitFactory;
    }
    //请求网络的API接口类
    private ApiService API() {
        return mApiService;
    }

    //构建API接口实现方法
    public void getPhoneInfo(String phone, String key, BaseObserver<PhoneInfo> scheduler) {
                API()
                .getPhoneMsg(phone, key)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scheduler);
    }
    //构建API接口实现方法
    public void getWeatherInfo(Map<String ,String> map, BaseObserver<WeatherInfo> scheduler) {
                API()
                .getWeather(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scheduler);
    }

    public void getChatMsgToken(RequestBody requestBody, BaseObserver<ChatMessageToken>observer){
        API()
                .getChatMessageToken(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}

