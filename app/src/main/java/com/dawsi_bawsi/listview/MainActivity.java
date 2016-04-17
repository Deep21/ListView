package com.dawsi_bawsi.listview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
        listView = (ListView) findViewById(R.id.listView);
        File[] f = readFiles();
        personList = new ArrayList<>();
        for (File file : f) {
            Log.d(TAG, "onCreate: " + file.getName());
            FileModel fileModel = new FileModel(file.getName(), file.getTotalSpace());
            personList.add(fileModel);
        }
        myAdaptor = new MyAdaptor(MainActivity.this, R.layout.list_layout, personList);
        listView.setAdapter(myAdaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                if (myAdaptor.getItem(position).isDownloaded() != NOT_UPLOADED) {
                    File img = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/huge.jpg");
                    ProgressFileRequestBody requestBody = new ProgressFileRequestBody(img, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            Log.d(TAG, "transferred: " + num);
                            publishProgress(pos, (int) num);
                        }
                    });
                    String params = "{ \"path\": \"/CV/huge.jpg\", \"autorename\": true, \"mute\": false, \"mode\": { \".tag\": \"add\"} }";

                    upload(requestBody, params, pos);

                } else {
                    //TODO

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Vouslez vous enoyer ce fichier");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oui",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
                                        int positionInListView = pos - listView.getFirstVisiblePosition();
                                        View v = listView.getChildAt(positionInListView);
                                        myAdaptor.getItem(pos).setIsDownloaded(false);
                                        myAdaptor.getView(pos, v, listView);
                                    }

                                    File img = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/huge.jpg");

                                    ProgressFileRequestBody requestBody = new ProgressFileRequestBody(img, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
                                        @Override
                                        public void transferred(long num) {
                                            Log.d(TAG, "transferred: " + num);
                                            publishProgress(pos, (int) num);
                                        }
                                    });
                                    String params = "{ \"path\": \"/CV/huge.jpg\", \"autorename\": true, \"mute\": false, \"mode\": { \".tag\": \"add\"} }";


                                    Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                    upload(requestBody, params, pos);
                                }
                            });

                    builder1.setNegativeButton(
                            "Non",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                }


                //download(pos);


            }

        });

    }

    private void upload(ProgressFileRequestBody progressFileRequestBody, String params, int position) {
        final int pos = position;
        sub = dropboxApi.uploadImage(progressFileRequestBody, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Upload>() {
                    @Override
                    public void call(Upload upload) {
                        Log.d(TAG, "call: " + upload);
                        if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
                            int positionInListView = pos - listView.getFirstVisiblePosition();
                            View v = listView.getChildAt(positionInListView);
                            myAdaptor.getItem(pos).setIsDownloaded(true);
                            myAdaptor.getView(pos, v, listView);
                        } else {
                            myAdaptor.getItem(pos).setIsDownloaded(true);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        sub.unsubscribe();
        super.onPause();
    }


    public File[] readFiles() {
        File[] file = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/");
            file = f.listFiles();
            return file;

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "onOptionsItemSelected: " + "can  read");

        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
        }
        return file;
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
