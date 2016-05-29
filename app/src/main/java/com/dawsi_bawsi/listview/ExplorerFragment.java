package com.dawsi_bawsi.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dawsi_bawsi.listview.eventbus.RxPublishSubjectCancelEvent;
import com.dawsi_bawsi.listview.eventbus.CheckBoxEvent;
import com.dawsi_bawsi.listview.eventbus.FragmentSelectEvent;
import com.dawsi_bawsi.listview.activities.MainActivity;
import com.dawsi_bawsi.listview.model.FileModel;
import com.dawsi_bawsi.listview.model.Upload;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import retrofit2.Response;
import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class ExplorerFragment extends Fragment {
    public static final String TAG = "ExplorerFragment";
    private static final String ABSOLUTE_PATH = "absolutePath";
    private static final boolean NOT_UPLOADED = true;
    ExplorerAdapter explorerAdapter;
    ListView listView;
    Subscription sub;
    List<Integer> positions = new ArrayList<>();
    List<Observable<Response<Upload>>> observables;
    private Map<Integer, PublishSubject<Object>> publishSubjectMap;
    private File[] files;
    private String absolutePath;


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
    public void onDestroy() {
        if (sub != null)
            sub.unsubscribe();
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    public void multiUpload(List<Integer> positions) {
        //TODO lors du upload si on reviens on arrière : on prévien l'utilisateur
        //TODO mettre le pourcentage dans la barre de upload et le Mo
        DropboxApi dropboxapi = ((MainActivity) getActivity()).dropboxApi;
        observables = new ArrayList<>();
        publishSubjectMap = new HashMap<>();
        if (positions != null) {
            for (final int position : positions) {
                File file = explorerAdapter.getItem(position);
                String params = DropboxGsonUtility.uploadParams(file);
                ProgressFileRequestBody requestBody = new ProgressFileRequestBody(file, "application/octet-stream", new ProgressFileRequestBody.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress(position, (int) num);
                    }
                });
                PublishSubject<Object> control = PublishSubject.create();
                publishSubjectMap.put(position, control);
                Observable<Response<Upload>> cancellableRestrofitObservable = dropboxapi.uploadImage(requestBody, params, position)
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
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            Log.d(TAG, "call: ");
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
        }
    }


    public void publishProgress(int position, int progress) {
        if (position >= listView.getFirstVisiblePosition() && position <= listView.getLastVisiblePosition()) {
            int positionInListView = position - listView.getFirstVisiblePosition();
            View v = listView.getChildAt(positionInListView);
            ExplorerAdapter.ViewHolder viewHolder = (ExplorerAdapter.ViewHolder) v.getTag();
            ProgressBar progressBar = viewHolder.progressBar;
            progressBar.setProgress(0);
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onStop() {
        if (sub != null)
            sub.unsubscribe();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        //premier lancement
            List<FileModel> fileModels = new ArrayList<>();
            files = read();
            if (files != null) {
                for (File f : files) {
                    FileModel fileModel = new FileModel(f);
                    fileModels.add(fileModel);
                }
                explorerAdapter = new ExplorerAdapter(getContext(), fileModels);
                listView.setAdapter(explorerAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String absolutePath = explorerAdapter.getItem(position).getAbsolutePath();
                        File f = new File(absolutePath);
                        //Je vérifie si c'est un dossier et que ce dossier contient des fichiers
                        if (f.isDirectory() && f.listFiles().length > 0) {
                            FragmentSelectEvent fragmentEvent = new FragmentSelectEvent();
                            fragmentEvent.setFragementName("DirectoryListFragment");
                            fragmentEvent.setAbsolutePath(f.getAbsolutePath());
                            EventBus.getDefault().post(fragmentEvent);
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
    public void onCancelEvent(RxPublishSubjectCancelEvent event) {

        observables.get(0).doOnRequest(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.d(TAG, "onREquest: ");
            }
        });
        //publishSubjectMap.get(event.getPosition()).onNext("cancel");
        //Log.d(TAG, "onCancelEvent: " + publishSubjectList.get(1));
    }

    @Subscribe
    public void onCheckBoxEvent(CheckBoxEvent event) {
        if (event.isSetIsSelected())
            positions.add(event.getPosition());

        else if (event.isSetIsSelected() != true)
            positions.remove(event.getPosition());

        Log.d(TAG, "onCheckBoxEvent: " + positions);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    public File[] read() {
        ExplorerFileModel explorerFileModel = new ExplorerFileModel(Environment.getExternalStorageDirectory().getAbsolutePath());
        File[] file = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            Log.d(TAG, "read: " + f.listFiles());
            Log.d(TAG, "read: " + Environment.getExternalStorageDirectory().getAbsolutePath());
            Log.d(TAG, "read: " + f);
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
            // Log.d(TAG, "onOptionsItemSelected:" + publishSubjectMap.get(8).hasObservers());
            Log.d(TAG, "onOptionsItemSelected: hascompleted" + publishSubjectMap.get(8).hasCompleted());
            publishSubjectMap.get(8).doOnCompleted(new Action0() {
                @Override
                public void call() {
                    Log.d(TAG, "doOnCompleted: ");
                }
            });

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
