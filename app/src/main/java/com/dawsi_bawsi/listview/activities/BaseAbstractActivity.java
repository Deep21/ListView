package com.dawsi_bawsi.listview.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dawsi_bawsi.listview.DropboxApi;
import com.dawsi_bawsi.listview.HttpInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Spartiate on 29/05/2016.
 */
public class BaseAbstractActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://content.dropboxapi.com/";
    protected HttpInterceptor httpInterceptor;
    protected OkHttpClient client;

    public DropboxApi getDropboxApi() {
        return dropboxApi;
    }

    public void setDropboxApi(DropboxApi dropboxApi) {
        this.dropboxApi = dropboxApi;
    }

    protected DropboxApi dropboxApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRetrofit();
    }

    public DropboxApi getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpInterceptor = new HttpInterceptor(this);
        client = new OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .addInterceptor(interceptor)
                .build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DropboxApi.class);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
