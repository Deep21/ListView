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

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FolderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FolderFragment extends Fragment {
    public static final String TAG = "FolderFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FileAdapter folderAdapter;
    ListView listView;
    Subscription sub;
    private File[] files;
    private String absolutePath;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public FolderFragment() {
        // Required empty public constructor
    }

    public static FolderFragment newInstance() {
        FolderFragment fragment = new FolderFragment();
        return fragment;
    }

    public static FolderFragment newInstance(String absolutePath) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putString("absolutePath", absolutePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        if (sub != null)
            sub.unsubscribe();
        super.onDestroy();

    }

    private void upload(int position, String path) {
        final int pos = position;
        Gson gson = new Gson();
        File file = folderAdapter.getItem(position).getFile();
        String paths = "/CV/" + file.getName();
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
        sub = ((MainActivity)getActivity()).dropboxApi.uploadImage(requestBody, params)
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
    public void onStart() {
        super.onStart();
        //premier lancement
        if (getArguments() != null) {
            List<FileModel> fileModels = new ArrayList<>();
            absolutePath = getArguments().getString("absolutePath");
            File[] files = new File(absolutePath).listFiles();
            for (File file : files) {
                FileModel fileModel = new FileModel(file);
                fileModels.add(fileModel);
            }
            folderAdapter = new FileAdapter(getContext(), fileModels);
            listView.setAdapter(folderAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: " + folderAdapter.getItem(position).getFile().getAbsolutePath());
                    File f = new File(folderAdapter.getItem(position).getFile().getAbsolutePath());
                    //TODO Refactor
                    if (f.isFile() != true && f.listFiles().length > 0) {
                        if (mListener != null) {
                            mListener.onCreateFolderFragment(f.getAbsolutePath());
                        }
                    }
                    // cas d'un fichier
                    else{
                        Log.d(TAG, "onItemClick: " + "fichier");
                    }
                    //TODO Refactor


                }
            });
        }
        //deuxième lancement
        else {
            List<FileModel> fileModels = new ArrayList<>();
            files = read();
            for (File f : files) {
                FileModel fileModel = new FileModel(f);
                fileModels.add(fileModel);
            }
            folderAdapter = new FileAdapter(getContext(), fileModels);
            listView.setAdapter(folderAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String absolutePath = folderAdapter.getItem(position).getFile().getAbsolutePath();
                    if (new File(absolutePath).listFiles().length > 0) {
                        if (mListener != null) {
                            mListener.onCreateFolderFragment(absolutePath);
                        }
                    }

                }
            });
        }
    }

    void upload() {
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: " + listView);
        if (getArguments() != null) {
/*            Log.d(TAG, "onCreate: " + folderAdapter);
            folderAdapter = new FileAdapter(getContext(),fileModels);
            listView.setAdapter(folderAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = folderAdapter.getItem(position).getFile().getName();
                    if (getFileByName(name).listFiles().length > 0) {
                        if (mListener != null) {
                            Log.d(TAG, "onItemClick: " + "files");
                            mListener.onCreateFolderFragment(name);
                        }
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onCreateFolderFragment(String fileName);

    }


}
