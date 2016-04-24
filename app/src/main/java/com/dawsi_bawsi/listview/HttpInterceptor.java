package com.dawsi_bawsi.listview;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Spartiate on 19/02/2016.
 */
public class HttpInterceptor implements Interceptor {
    private static final String TAG = "HttpInterceptor";
    UploadResponse onUpload;

    public void setOnUpload(UploadResponse onUpload) {
        this.onUpload = onUpload;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (onUpload != null) {
            return onUpload.onUpload(chain);
        }
        return chain.proceed(chain.request());
    }

    public interface UploadResponse {
        int UNAUTHORIZED = 401;
        Response onUpload(Chain chain) throws IOException;
    }
}
