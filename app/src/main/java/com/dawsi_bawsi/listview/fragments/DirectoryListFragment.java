package com.dawsi_bawsi.listview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dawsi_bawsi.listview.ExplorerAdapter;
import com.dawsi_bawsi.listview.R;
import com.dawsi_bawsi.listview.eventbus.FragmentSelectEvent;
import com.dawsi_bawsi.listview.model.FileModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DirectoryListFragment extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "DirectoryListFragment";
    private static final String ABSOLUTE_PATH = "ABSOLUTE_PATH";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ExplorerAdapter explorerAdapter;

    public DirectoryListFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param absolutePath Parameter 1.
     * @return A new instance of fragment DirectoryListFragment.
     */
    public static DirectoryListFragment newInstance(String absolutePath) {
        DirectoryListFragment fragment = new DirectoryListFragment();
        Bundle args = new Bundle();
        args.putString(ABSOLUTE_PATH, absolutePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ABSOLUTE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        File f = explorerAdapter.getItem(position);
        if (f.isDirectory() && f.listFiles().length > 0) {
            FragmentSelectEvent fragmentEvent = new FragmentSelectEvent();
            fragmentEvent.setAbsolutePath(f.getAbsolutePath());
            EventBus.getDefault().post(fragmentEvent);
        }
        // cas d'un fichier
        else if (f.isFile()) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            List<FileModel> fileModels = new ArrayList<>();
            String absolutePath = getArguments().getString(ABSOLUTE_PATH);
            File[] files = new File(absolutePath).listFiles();
            for (File file : files) {
                FileModel fileModel = new FileModel(file);
                fileModels.add(fileModel);
            }
            explorerAdapter = new ExplorerAdapter(getContext(), fileModels);
            setListAdapter(explorerAdapter);
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
