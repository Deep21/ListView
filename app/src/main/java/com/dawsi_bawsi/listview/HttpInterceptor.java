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
    public interface UploadResponse {
        int UNAUTHORIZED = 401;
        int BADREQUEST = 400;
        int INSUFFICIENT_STORAGE = 507;
        void onUpload(Response r) throws IOException;
    }
    public void setOnUpload(UploadResponse onUpload) {
        this.onUpload = onUpload;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response r = chain.proceed(chain.request());
        if (onUpload != null) {
            // new Handler(Looper.getMainLooper()).post(() -> eventBus.post(new AuthenticationErrorEvent()));
             onUpload.onUpload(r);
        }
        return r;
    }


}
