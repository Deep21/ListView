package com.dawsi_bawsi.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FolderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "FolderFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FolderAdapter folderAdapter;
    private File[] files;
    ListView listView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    public FolderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FolderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderFragment newInstance() {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<FileModel> fileModels = new ArrayList<>();
        files = read();
        for (File f : files) {
            FileModel fileModel = new FileModel(f);
            fileModels.add(fileModel);
        }
        folderAdapter = new FolderAdapter(fileModels, getContext());
        listView.setAdapter(folderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = folderAdapter.getItem(position).getFile().getName();
                File[] f = getFileByName(name).listFiles();
                if (f.length > 0) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(position);
                    }
                }

            }
        });
    }

    public File getFileByName(String fileName) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + "/");
    }

    public File[] read() {
        File[] file = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return f.listFiles();

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "onOptionsItemSelected: " + "can  read");

        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
        }
        return file;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int i);
    }
}
