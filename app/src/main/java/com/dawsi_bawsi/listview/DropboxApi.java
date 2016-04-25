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
 * Created by Spartiate on 26/03/2016.
 */
public interface DropboxApi {

    @Multipart
    @Headers({
            "Content-Type: application/octet-stream",
            "Authorization: Bearer wORVBx-8aJ4AAAAAAAAG7re75xSZH-k3tw2sPXJu73YzkmpB-FJFeg_Ek-_QfNMe"
    })
    @POST("2/files/upload")
    Observable<Upload> uploadImage(@Part("hello\"; filename=\"hello.txt\" ") ProgressFileRequestBody file, @Header("Dropbox-API-Arg") String fileUploadParam);

    @GET("http://www.mocky.io/v2/571d3c83110000fa2c291078")

    Observable<Response<Upload>> testCase400();

    @GET("http://www.mocky.io/v2/571e69b00f00005b1a127cc5")
    Observable<Response<Upload>> testCase401();

    @GET("http://www.mocky.io/v2/571e7a6e0f0000aa1c127cd7")
    Observable<Response<Upload>> testCase200();

}
