package com.dawsi_bawsi.listview;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.gson.Gson;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements FolderFragment.OnFragmentInteractionListener{
    public static final String BASE_URL = "https://content.dropboxapi.com/";
    public static final int FINISH = 100;
    private static final String TAG = "MainActivity";
    private static final boolean NOT_UPLOADED = true;
    FrameLayout frameLayout;
    FileAdapter fileAdapter;
    HttpInterceptor httpInterceptor;
    Subscription sub;
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
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, FolderFragment.newInstance(), FolderFragment.TAG).commit();
    }

    private void upload(int position) {
        final int pos = position;
        Gson gson = new Gson();
        File file = fileAdapter.getItem(position).getFile();
        String path = "/CV/" + file.getName();
        UploadParam uploadParam = new UploadParam();
        uploadParam.setPath(path);
        uploadParam.setAutorename(true);
        uploadParam.setMute(false);
        Mode mode = new Mode();
        mode.setTag("add");
        uploadParam.setMode(mode);
        String params = gson.toJson(uploadParam);
        ProgressFileRequestBody requestBody = new ProgressFileRequestBody(file, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
            @Override
            public void transferred(long num) {
                Log.d(TAG, "transferred: " + num);
                //publishProgress(pos, (int) num);
            }
        });
        sub = dropboxApi.uploadImage(requestBody, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Upload>() {
                    @Override
                    public void call(Upload upload) {
                        //refreshListView(pos);
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

    @Override
    public void onCreateFolderFragment(String absolutePath) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, FolderFragment.newInstance(absolutePath), FolderFragment.TAG).addToBackStack(null).commit();

    }
}
