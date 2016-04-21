package com.dawsi_bawsi.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.io.File;
import java.util.List;

/**
 * Created by Spartiate on 21/04/2016.
 */
public class FolderAdapter extends BaseAdapter {
    List<FileModel> fileList;

    public FolderAdapter(List<FileModel> fileList) {
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public FileModel getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
