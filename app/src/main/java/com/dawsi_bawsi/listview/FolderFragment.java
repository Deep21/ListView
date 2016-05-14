package com.dawsi_bawsi.listview;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class FolderFragment extends Fragment {
    public static final String TAG = "FolderFragment";
    private static final String ABSOLUTE_PATH = "absolutePath";
    private static final String ARG_PARAM2 = "param2";
    private static final boolean NOT_UPLOADED = true;
    FileAdapter folderAdapter;
    ListView listView;
    Subscription sub;
    private File[] files;
    private String absolutePath;
    private OnFragmentInteractionListener mListener;

    public FolderFragment() {
    }

    public static FolderFragment newInstance(String absolutePath) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putString(ABSOLUTE_PATH, absolutePath);
        fragment.setArguments(args);
        return fragment;
    }

    public static FolderFragment newInstance() {
        FolderFragment fragment = new FolderFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: " + "on destroy");
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {
        if (sub != null)
            sub.unsubscribe();
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    public void multiUpload(List<FileModel> fileModels) {
        //TODO lors du upload si on reviens on arrière : on prévien l'utilisateur
        //TODO mettre le pourcentage dans la barre de upload et le Mo
        //TODO cancel request
        DropboxApi dropboxapi = ((MainActivity) getActivity()).dropboxApi;
        HttpInterceptor httpInterceptor = ((MainActivity) getActivity()).getHttpInterceptor();
        List<Observable<Response<Upload>>> observables = new ArrayList<>();
        if (fileModels != null) {
            for (final FileModel f : fileModels) {
                File file = f.getFile();
                String path = "/CV/" + file.getName();
                final UploadParam uploadParam = new UploadParam();
                uploadParam.setPath(path);
                uploadParam.setAutorename(true);
                uploadParam.setMute(false);
                Mode mode = new Mode();
                mode.setTag("add");
                uploadParam.setMode(mode);
                Gson gson = new Gson();
                String params = gson.toJson(uploadParam);
                ProgressFileRequestBody requestBody = new ProgressFileRequestBody(file, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress(f.position, (int) num);
                    }
                });
                httpInterceptor.setPosition(f.position);
                observables.add(dropboxapi.uploadImage(requestBody, params, f.position).subscribeOn(Schedulers.io()));
            }
        }

        sub = Observable.merge(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new ErrorAction(getContext()))
                .doOnEach(new Action1<Notification<? super Response<Upload>>>() {
                    @Override
                    public void call(Notification<? super Response<Upload>> notification) {
                        Log.d(TAG, "notification: " + notification);
                    }
                })
                .subscribe(new Action1<Response<Upload>>() {
                    @Override
                    public void call(Response<Upload> uploadResponse) {
                        refreshListView(Integer.parseInt(uploadResponse.raw().headers().get("position")));
                        Request t = uploadResponse.raw().request();
                        Log.d(TAG, "uploadResponse: " + uploadResponse.raw().headers().get("position"));
                        Log.d(TAG, "uploadResponse + request: " + t.headers().get("pos"));
                    }
                });

    }

    public void refreshListView(int pos) {
        if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
            int positionInListView = pos - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            folderAdapter.getItem(pos).setIsDownloaded(true);
            folderAdapter.getView(pos, v, listView);
            Log.d(TAG, "refreshListView: ");
        }
    }


    public void publishProgress(int position, int progress) {
        Log.d(TAG, "publishProgress not in the: ");

        if (position >= listView.getFirstVisiblePosition() && position <= listView.getLastVisiblePosition()) {
            Log.d(TAG, "publishProgress: ");
            int positionInListView = position - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            ProgressBar p = (ProgressBar) v.findViewById(R.id.progressBar);
            p.setProgress(0);
            p.setProgress(progress);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        //deuxième lancement
        if (getArguments() != null) {
            List<FileModel> fileModels = new ArrayList<>();
            absolutePath = getArguments().getString(ABSOLUTE_PATH);
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
                    final int pos = position;
                    File f = new File(folderAdapter.getItem(position).getFile().getAbsolutePath());
                    //TODO Refactor
                    if (f.isDirectory() && f.listFiles().length > 0) {
                        if (mListener != null) {
                            mListener.onCreateFolderFragment(f.getAbsolutePath());
                        }
                    }
                    // cas d'un fichier
                    else if (f.isFile()) {
                        if (folderAdapter.getItem(position).isDownloaded() != NOT_UPLOADED) {

                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setTitle("Attention !");
                            builder1.setMessage("Vous venez d'uploadé le même fichier, Voulez vous recommencez ?");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Oui",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
                                                int positionInListView = pos - listView.getFirstVisiblePosition();
                                                View v = listView.getChildAt(positionInListView);
                                                folderAdapter.getItem(pos).setIsDownloaded(false);
                                                folderAdapter.getView(pos, v, listView);
                                            }
                                            // upload(pos);
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
                    //TODO Refactor

                }
            });
        }
        //premier lancement
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
                    File f = new File(absolutePath);
                    //Je vérifie si c'est un dossier et que ce dossier contient des fichiers
                    if (f.isDirectory() && f.listFiles().length > 0) {
                        if (mListener != null) {
                            mListener.onCreateFolderFragment(absolutePath);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        Log.d(TAG, "onCreateView: " + savedInstanceState);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            List<Integer> fileModels = folderAdapter.integers;
            Log.d(TAG, "onOptionsItemSelected: " + fileModels);
            //multiUpload(fileModels);
            return true;
        }

        if (id == R.id.checkBox1) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void onCreateFolderFragment(String fileName);

    }


}
