package com.dawsi_bawsi.listview.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dawsi_bawsi.listview.DropboxApi;
import com.dawsi_bawsi.listview.ExplorerFragment;
import com.dawsi_bawsi.listview.R;
import com.dawsi_bawsi.listview.eventbus.FragmentSelectEvent;
import com.dawsi_bawsi.listview.fragments.DirectoryListFragment;
import com.dawsi_bawsi.listview.fragments.FileFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends BaseAbstractActivity implements ExplorerFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    FrameLayout frameLayout;
    DropboxApi dropboxApi;


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
    public void lunchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ExplorerFragment explorerFragment = (ExplorerFragment) getSupportFragmentManager().findFragmentByTag(ExplorerFragment.TAG);
        if (explorerFragment == null) {
            fragmentTransaction.add(R.id.frame_layout, ExplorerFragment.newInstance(), ExplorerFragment.TAG).commit();
        } else {
            fragmentTransaction.replace(R.id.frame_layout, explorerFragment, ExplorerFragment.TAG).commit();
        }
    }

    @Subscribe
    public void onFragmentSwitch(FragmentSelectEvent fragmentEvent) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentEvent.getFragmentName()) {
            case FileFragment.TAG:
                FileFragment fileFragment = (FileFragment) getSupportFragmentManager().findFragmentByTag(FileFragment.TAG);
                if (fileFragment == null) {
                    fragmentTransaction
                            .replace(R.id.frame_layout, FileFragment.newInstance(fragmentEvent.getAbsolutePath()), FileFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                } else {
                    fragmentTransaction.replace(R.id.frame_layout, fileFragment, FileFragment.TAG).commit();
                }

                break;

            case DirectoryListFragment.TAG:
                DirectoryListFragment directoryListFragment = (DirectoryListFragment) getSupportFragmentManager().findFragmentByTag(DirectoryListFragment.TAG);
                if (directoryListFragment == null) {
                    fragmentTransaction
                            .replace(R.id.frame_layout, DirectoryListFragment.newInstance(fragmentEvent.getAbsolutePath()), DirectoryListFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                } else {
                    fragmentTransaction.replace(R.id.frame_layout, directoryListFragment, DirectoryListFragment.TAG).commit();
                }
                break;

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
