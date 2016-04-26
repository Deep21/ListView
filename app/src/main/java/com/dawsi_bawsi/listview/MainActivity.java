package com.dawsi_bawsi.listview;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://content.dropboxapi.com/";
    public static final int FINISH = 100;
    private static final String TAG = "MainActivity";
    private static final boolean NOT_UPLOADED = true;
    ListView listView;
    MyAdaptor myAdaptor;
    HttpInterceptor httpInterceptor;
    Subscription sub;
    List<FileModel> personList;
    DropboxApi dropboxApi;

    public DropboxApi getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpInterceptor = new HttpInterceptor();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DropboxApi.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dropboxApi = getRetrofit();
        upload();
    }


    private void upload() {
        sub = dropboxApi.testCaseStorageQuota507()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Response<Upload>>() {
                    @Override
                    public void call(Response<Upload> uploadResponse) {
                        Log.d(TAG, "onNext: doOnNext " + uploadResponse.code());
                        switch (uploadResponse.code()) {


                            case HttpInterceptor.UploadResponse.UNAUTHORIZED:
                                AlertDialog.Builder unauthorizedBuilder = new AlertDialog.Builder(MainActivity.this);
                                unauthorizedBuilder.setTitle("Echec de connexion");
                                unauthorizedBuilder.setMessage("Votre session à expiré");
                                unauthorizedBuilder.setCancelable(true);
                                AlertDialog alert11 = unauthorizedBuilder.create();
                                alert11.show();
                                break;

                            case HttpInterceptor.UploadResponse.BADREQUEST:
                                AlertDialog.Builder badRequestBuilder = new AlertDialog.Builder(MainActivity.this);
                                badRequestBuilder.setTitle("Echec de connexion");
                                badRequestBuilder.setMessage("Votre session à expiré");
                                badRequestBuilder.setCancelable(true);
                                AlertDialog alert1 = badRequestBuilder.create();
                                alert1.show();
                                break;

                            case HttpInterceptor.UploadResponse.INSUFFICIENT_STORAGE:
                                AlertDialog.Builder STORAGE_Builder = new AlertDialog.Builder(MainActivity.this);
                                STORAGE_Builder.setTitle("Problème de stockage");
                                STORAGE_Builder.setMessage("Vous avez atteint la limite de stockage");
                                STORAGE_Builder.setCancelable(true);
                                AlertDialog storage = STORAGE_Builder.create();
                                storage.show();
                                break;
                        }
                    }
                })
                .subscribe(new Action1<Response<Upload>>() {
                    @Override
                    public void call(Response<Upload> uploadResponse) {

                    }
                });
    }

    @Override
    protected void onPause() {
        if (sub != null)
            sub.unsubscribe();

        super.onPause();
    }


/*    private void download(final int positionInAdapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 100; i++) {
                    final int progress = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            publishProgress(positionInAdapter, progress);
                        }
                    });
                    System.out.println(i);
                    SystemClock.sleep(10);
                }
                Bundle bundle = new Bundle();
                Message message = handler.obtainMessage();
                bundle.putInt("position", positionInAdapter);
                message.setData(bundle);
                handler.sendMessage(message);

            }
        }).start();
    }*/

    public void refreshListView(int pos) {
        if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
            int positionInListView = pos - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            myAdaptor.getItem(pos).setIsDownloaded(true);
            myAdaptor.getView(pos, v, listView);
        } else {
            // myAdaptor.getItem(pos).setIsDownloaded(true);
        }
    }

    public void publishProgress(int position, int progress) {
        if (position >= listView.getFirstVisiblePosition() && position <= listView.getLastVisiblePosition()) {
            int positionInListView = position - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            ProgressBar p = (ProgressBar) v.findViewById(R.id.progressBar);
            p.setProgress(progress);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (sub != null) {
            sub.unsubscribe();
        }
        super.onDestroy();
    }
}
