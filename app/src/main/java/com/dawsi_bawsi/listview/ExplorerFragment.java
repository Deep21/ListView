package com.dawsi_bawsi.listview;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dawsi_bawsi.listview.model.Upload;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import retrofit2.Response;
import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class ExplorerFragment extends Fragment {
    public static final String TAG = "ExplorerFragment";
    private static final String ABSOLUTE_PATH = "absolutePath";
    private static final String ARG_PARAM2 = "param2";
    private static final boolean NOT_UPLOADED = true;
    ExplorerAdapter explorerAdapter;
    ListView listView;
    Subscription sub;
    List<PublishSubject> publishSubjectList;
    private File[] files;
    private String absolutePath;
    private OnFragmentInteractionListener mListener;
    SparseIntArray positions = new SparseIntArray();
    public ExplorerFragment() {
    }

    public static ExplorerFragment newInstance(String absolutePath) {
        ExplorerFragment fragment = new ExplorerFragment();
        Bundle args = new Bundle();
        args.putString(ABSOLUTE_PATH, absolutePath);
        fragment.setArguments(args);
        return fragment;
    }

    public static ExplorerFragment newInstance() {
        ExplorerFragment fragment = new ExplorerFragment();
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


    public void multiUpload(SparseIntArray sparseIntArray) {
        //TODO lors du upload si on reviens on arrière : on prévien l'utilisateur
        //TODO mettre le pourcentage dans la barre de upload et le Mo
        DropboxApi dropboxapi = ((MainActivity) getActivity()).dropboxApi;
        HttpInterceptor httpInterceptor = ((MainActivity) getActivity()).getHttpInterceptor();
        List<Observable<Response<Upload>>> observables = new ArrayList<>();
        publishSubjectList = new ArrayList<>();
        if (sparseIntArray != null) {
            for(int i = 0; i < sparseIntArray.size(); i++){
                final int pos = sparseIntArray.get(i);
                File file = explorerAdapter.getItem(pos).getFile();
                String params = DropboxGsonUtility.uploadParams(file);
                ProgressFileRequestBody requestBody = new ProgressFileRequestBody(file, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress(pos, (int) num);
                    }
                });
                httpInterceptor.setPosition(pos);
                PublishSubject control = PublishSubject.create();
                publishSubjectList.add(control);
                Observable<Response<Upload>> cancellableRestrofitObservable = dropboxapi.uploadImage(requestBody, params, pos)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .takeUntil(control.asObservable());
                observables.add(cancellableRestrofitObservable);
            }
            Observable<Response<Upload>> mergedObservable = Observable.merge(observables);
            sub = mergedObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Response<Upload>>() {
                        @Override
                        public void call(Response<Upload> uploadResponse) {
                            Log.d(TAG, "doOnNext: " + uploadResponse);
                        }
                    })
                    .doOnEach(new Action1<Notification<? super Response<Upload>>>() {
                        @Override
                        public void call(Notification<? super Response<Upload>> notification) {
                            Log.d(TAG, "notification: " + notification);
                        }
                    })
                    .subscribe(new Observer<Response<Upload>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: " + e);
                        }

                        @Override
                        public void onNext(Response<Upload> uploadResponse) {
                            refreshListView(Integer.parseInt(uploadResponse.raw().headers().get("position")));
                            Request t = uploadResponse.raw().request();
                            Log.d(TAG, "uploadResponse: " + uploadResponse.raw().headers().get("position"));
                            Log.d(TAG, "uploadResponse + request: " + t.headers().get("pos"));
                        }
                    });

        }


    }

    public void refreshListView(int pos) {
        if (pos >= listView.getFirstVisiblePosition() && pos <= listView.getLastVisiblePosition()) {
            int positionInListView = pos - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            explorerAdapter.getItem(pos).setIsDownloaded(true);
            explorerAdapter.getView(pos, v, listView);
            Log.d(TAG, "refreshListView: ");
        }
    }


    public void publishProgress(int position, int progress) {
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        //deuxième lancement
        if (getArguments() != null) {
            List<FileModel> fileModels = new ArrayList<>();
            absolutePath = getArguments().getString(ABSOLUTE_PATH);
            File[] files = new File(absolutePath).listFiles();
            for (File file : files) {
                FileModel fileModel = new FileModel(file);
                fileModels.add(fileModel);
            }
            explorerAdapter = new ExplorerAdapter(getContext(), fileModels);
            listView.setAdapter(explorerAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final int pos = position;
                    File f = new File(explorerAdapter.getItem(position).getFile().getAbsolutePath());
                    //TODO Refactor
                    if (f.isDirectory() && f.listFiles().length > 0) {
                        if (mListener != null) {
                            mListener.onCreateFolderFragment(f.getAbsolutePath());
                        }
                    }
                    // cas d'un fichier
                    else if (f.isFile()) {
                        if (explorerAdapter.getItem(position).isDownloaded() != NOT_UPLOADED) {

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
                                                explorerAdapter.getItem(pos).setIsDownloaded(false);
                                                explorerAdapter.getView(pos, v, listView);
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
            explorerAdapter = new ExplorerAdapter(getContext(), fileModels);
            listView.setAdapter(explorerAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String absolutePath = explorerAdapter.getItem(position).getFile().getAbsolutePath();
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
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onCancelEvent(CancelEvent event) {
        Log.d(TAG, "onEvent: " + event.getPosition());
        publishSubjectList.get(event.getPosition()).onNext("cancel");
        boolean completed = publishSubjectList.get(event.getPosition()).hasCompleted();
        Log.d(TAG, "onCancelRequest: " + completed);
    }

    @Subscribe
    public void onCheckBoxEvent(CheckBoxEvent event) {
        Log.d(TAG, "onCheckBoxEvent: " + event.getPosition());
        positions.put(event.getPosition(), event.getPosition());
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
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    public File[] read() {
        ExplorerFileModel explorerFileModel = new ExplorerFileModel(Environment.getExternalStorageDirectory().getAbsolutePath());
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
        if (id == R.id.upload) {
            Log.d(TAG, "onOptionsItemSelected: " + positions);
            multiUpload(positions);
            return true;
        }

        if (id == R.id.checkbox) {
            Log.d(TAG, "onOptionsItemSelected: " + publishSubjectList);

/*            publishSubjectList.get(0).doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    Log.d(TAG, "call: " + "ok");
                    //publishSubjectList.get(0).onNext("cancel");
                }
            });*/
            if (sub != null && sub.isUnsubscribed() != true) {
                //Log.d(TAG, "onOptionsItemSelected: " + sub);
/*                publishSubjectList.get(0).onNext("cancel");
                publishSubjectList.get(1).onNext("cancel");*/
                //sub.unsubscribe();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void onCreateFolderFragment(String fileName);

    }


}
