package com.dawsi_bawsi.listview;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.dawsi_bawsi.listview.model.Upload;

import retrofit2.Response;
import rx.functions.Action1;

/**
 * Created by Spartiate on 03/05/2016.
 */
public class ErrorAction implements Action1<Response<Upload>>{
    Context cx;

    public ErrorAction(Context context) {
        this.cx = context;
    }

    @Override
    public void call(Response<Upload> uploadResponse) {
        switch (uploadResponse.code()) {
            case HttpInterceptor.UploadResponse.UNAUTHORIZED:
                AlertDialog.Builder unauthorizedBuilder = new AlertDialog.Builder(cx);
                unauthorizedBuilder.setTitle("Echec de connexion");
                unauthorizedBuilder.setMessage("Votre session à expiré");
                unauthorizedBuilder.setCancelable(true);
                AlertDialog alert11 = unauthorizedBuilder.create();
                alert11.show();
                break;

            case HttpInterceptor.UploadResponse.BADREQUEST:
                AlertDialog.Builder badRequestBuilder = new AlertDialog.Builder(cx);
                badRequestBuilder.setTitle("Echec de connexion");
                badRequestBuilder.setMessage("Votre session à expiré");
                badRequestBuilder.setCancelable(true);
                AlertDialog alert1 = badRequestBuilder.create();
                alert1.show();
                break;

            case HttpInterceptor.UploadResponse.INSUFFICIENT_STORAGE:
                AlertDialog.Builder STORAGE_Builder = new AlertDialog.Builder(cx);
                STORAGE_Builder.setTitle("Problème de stockage");
                STORAGE_Builder.setMessage("Vous avez atteint la limite de stockage");
                STORAGE_Builder.setCancelable(true);
                AlertDialog storage = STORAGE_Builder.create();
                storage.show();
                break;
        }
    }
}
