package com.dawsi_bawsi.listview;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Spartiate on 26/03/2016
 */
public interface DropboxApi {

    @Multipart
    @Headers({
            "Content-Type: application/octet-stream",
            "Authorization: Bearer wORVBx-8aJ4AAAAAAAALNkOR74PnYr6pWMXIQKe-1-euueut8mzUwyW2pJC0nrZ7"
    })

    @POST("2/files/upload")
    Observable<Response<Upload>> uploadImage(@Part("hello\"; filename=\"hello.txt\" ") ProgressFileRequestBody file, @Header("Dropbox-API-Arg") String fileUploadParam, @Header("pos") int pos);


}
