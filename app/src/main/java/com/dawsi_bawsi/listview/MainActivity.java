package com.dawsi_bawsi.listview;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dawsi_bawsi.listview.DropboxApi;
import com.dawsi_bawsi.listview.ExplorerFragment;
import com.dawsi_bawsi.listview.HttpInterceptor;
import com.dawsi_bawsi.listview.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ExplorerFragment.OnFragmentInteractionListener {
    public static final String BASE_URL = "https://content.dropboxapi.com/";
    private static final String TAG = "MainActivity";
    FrameLayout frameLayout;
    HttpInterceptor httpInterceptor;
    DropboxApi dropboxApi;
    OkHttpClient client;
    public HttpInterceptor getHttpInterceptor() {
        return httpInterceptor;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public DropboxApi getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpInterceptor = new HttpInterceptor(this);
        client = new OkHttpClient.Builder()
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

    @Subscribe
    public void onEventBackgroundThread(MyMessageEvent myEvent){
        //client.dispatcher().cancelAll();
        Log.d(TAG, "onEventBackgroundThread: " + myEvent.pos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dropboxApi = getRetrofit();
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        lunchFragment();
    }

    /**
     * Lance explorer Fragment
     */
    public void lunchFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ExplorerFragment explorerFragment = (ExplorerFragment)getSupportFragmentManager().findFragmentByTag(ExplorerFragment.TAG);
        if(explorerFragment == null){
            fragmentTransaction.add(R.id.frame_layout, ExplorerFragment.newInstance(), ExplorerFragment.TAG).commit();
        }else{
            fragmentTransaction.replace(R.id.frame_layout, explorerFragment, ExplorerFragment.TAG).commit();
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
    public void onCreateFolderFragment(String absolutePath) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.frame_layout, ExplorerFragment.newInstance(absolutePath), ExplorerFragment.TAG)
                .addToBackStack(null)
                .commit();

    }
}
