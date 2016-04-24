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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "FileFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FileAdaptor fileAdaptor;
    ListView listView;
    // TODO: Rename and change types of parameters
    private int position;
    private OnFragmentInteractionListener mListener;

    public FileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FolderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileFragment newInstance(int i) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putInt("position", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + position);
        MainActivity mainActivity = (MainActivity) getActivity();
        String name = mainActivity.getFiles()[position].getName();
        File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mainActivity.getFiles()[position].getName() + "/").listFiles();
        Log.d(TAG, "onStart: " + name);
        List<FileModel> fileModels = new ArrayList<>();
        for(File f : files){
            Log.d(TAG, "onStart: " + f.getName());
            FileModel fileModel = new FileModel(f);
            fileModels.add(fileModel);
        }
        fileAdaptor = new FileAdaptor(getContext(), fileModels);
        listView.setAdapter(fileAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        Log.d(TAG, "onStart: " + listView);
    }

    public void getFiles(int position) {

        // return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mainActivity.getFiles()[position].getName() + "/");
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
            position = getArguments().getInt("position");
            Toast.makeText(getContext(), "" + listView, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        void onFragmentInteraction();
    }
}
