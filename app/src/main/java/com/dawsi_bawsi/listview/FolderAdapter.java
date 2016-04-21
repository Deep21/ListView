package com.dawsi_bawsi.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.File;
import java.util.List;

/**
 * Created by Spartiate on 21/04/2016.
 */
public class FolderAdapter extends BaseAdapter {
    List<FileModel> fileList;
    Context context;

    public FolderAdapter(List<FileModel> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_layout_folder, parent, false);
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
            viewHolder.folderTypeIcone = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.nom.setText(getItem(position).getFile().getName());
        return convertView;

    }

    class ViewHolder {
        TextView nom;
        ImageView folderTypeIcone;


    }

}
