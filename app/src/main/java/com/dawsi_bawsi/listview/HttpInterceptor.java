package com.dawsi_bawsi.listview;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Spartiate on 19/02/2016.
 */
public class HttpInterceptor implements Interceptor {
    private static final String TAG = "HttpInterceptor";
    int position;


    public HttpInterceptor(Context context) {

    }

    public void setPosition(int position) {
        this.position = position;
        Log.d(TAG, "setPosition: " + position);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Log.d(TAG, "intercept: " + chain.request().newBuilder().tag("eef").build());
        //Request request1 = chain.request().newBuilder().build();
        // String position = chain.request().header("pos");
        Request request = chain.request();
        //Request request = chain.request().newBuilder().tag(position).build();
        Response r = chain.proceed(request);
        Log.d(TAG, "intercept: " + r);
        //Response overridedResponse = r.newBuilder().addHeader("position", position).request(request).build();
        return r;
    }

    public interface UploadResponse {
        int UNAUTHORIZED = 401;
        int BADREQUEST = 400;
        int INSUFFICIENT_STORAGE = 507;
    }

}

