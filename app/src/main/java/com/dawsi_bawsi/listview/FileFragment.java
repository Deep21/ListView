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


public class FileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "FileFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FileAdaptor fileAdaptor;
    ListView listView;
    // TODO: Rename and change types of parameters
    private String path;
    private OnFolderListener mListener;

    public FileFragment() {
        // Required empty public constructor
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                if (myAdaptor.getItem(position).isDownloaded() != NOT_UPLOADED) {
                    int positionInListView = pos - listView.getFirstVisiblePosition();
                    View v = listView.getChildAt(positionInListView);
                    myAdaptor.getItem(pos).setShowProgressbar(true);
                    myAdaptor.getView(pos, v, listView);
                    upload(pos);
                } else {
                    //TODO

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Attention !");
                    builder1.setMessage("Voulez vous uploadé ce fichier");
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
                                    upload(pos);
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

            }

        });*/
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FolderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileFragment newInstance(String path) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            path = getArguments().getString("path");
            File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path + "/").listFiles();
            List<FileModel> fileModels = new ArrayList<>();
            for (File f : files) {
                Log.d(TAG, "onStart: " + f.getName());
                FileModel fileModel = new FileModel(f);
                fileModels.add(fileModel);
            }
/*            fileAdaptor = new FileAdaptor(getContext(), fileModels);
            listView.setAdapter(fileAdaptor);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (fileAdaptor.getItem(position).getFile().isDirectory()) {
                        mListener.createFolderFragment();
                    }
                }
            });*/
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
        if (context instanceof OnFolderListener) {
            mListener = (OnFolderListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
    public interface OnFolderListener {
        // TODO: Update argument type and name
        void createFolderFragment();
    }
}
