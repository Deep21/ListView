package com.dawsi_bawsi.listview;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Spartiate on 19/02/2016.
 */
public class HttpInterceptor implements Interceptor {
    private static final String TAG = "HttpInterceptor";
    UploadResponse onUpload;

    public HttpInterceptor(Context context){
    }

    public interface UploadResponse {
        int UNAUTHORIZED = 401;
        int BADREQUEST = 400;
        int INSUFFICIENT_STORAGE = 507;
        void onUpload(Response r) throws IOException;
    }

    public void setOnUpload(UploadResponse onUpload) {
        this.onUpload = onUpload;
    }

    public void setPosition(int position){
        Log.d(TAG, "setPosition: " + position);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
       // Log.d(TAG, "intercept: " + chain.request().newBuilder().tag("eef").build());
        //Request request1 = chain.request().newBuilder().build();
        String position = chain.request().header("pos");
        Request request = chain.request();
        Response r = chain.proceed(request);
        Response re = r.newBuilder().addHeader("position", position).request(chain.request().newBuilder().tag("ef").build()).build();

        if (onUpload != null) {
            // new Handler(Looper.getMainLooper()).post(() -> eventBus.post(new AuthenticationErrorEvent()));
             onUpload.onUpload(r);
        }
        return re;
    }


}
