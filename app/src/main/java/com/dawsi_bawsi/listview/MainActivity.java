package com.dawsi_bawsi.listview;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements FolderFragment.OnFragmentInteractionListener{
    public static final String BASE_URL = "https://content.dropboxapi.com/";
    private static final String TAG = "MainActivity";
    private static final boolean NOT_UPLOADED = true;
    FrameLayout frameLayout;

    public HttpInterceptor getHttpInterceptor() {
        return httpInterceptor;
    }

    @Override
    public void onBackPressed() {
        FolderFragment folderFragment = (FolderFragment)getSupportFragmentManager().findFragmentByTag(FolderFragment.TAG);
        if(folderFragment !=null){
            if(folderFragment.isVisible()){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Attention !");
                builder1.setMessage("Vous allez annuler la tâche en cours ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });



                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        super.onBackPressed();
    }

    HttpInterceptor httpInterceptor;
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
                .build()
                .create(DropboxApi.class);
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
        FolderFragment folderFragment = (FolderFragment)getSupportFragmentManager().findFragmentByTag(FolderFragment.TAG);
        if(folderFragment == null){
            fragmentTransaction.add(R.id.frame_layout, FolderFragment.newInstance(), FolderFragment.TAG).commit();
        }else{
            fragmentTransaction.replace(R.id.frame_layout, folderFragment, FolderFragment.TAG).commit();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
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
    public void onCreateFolderFragment(String absolutePath) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, FolderFragment.newInstance(absolutePath), FolderFragment.TAG).addToBackStack(null).commit();

    }
}
