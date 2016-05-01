package com.dawsi_bawsi.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Spartiate on 13/03/2016.
 */
public class FileAdapter extends BaseAdapter {
    private static final String TAG = "FileAdapter";
    List<FileModel> persons;
    Context context;

    public FileAdapter(Context context, List<FileModel> objects) {
        this.persons = objects;
        this.context = context;
    }

    public List<FileModel> getPersons() {
        return persons;
    }

    public void setPersons(List<FileModel> persons) {
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public FileModel getItem(int position) {
        return persons.get(position);
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

            switch (getItemViewType(position)){

                case 0:
                    Log.d(TAG, "getView: " + "folder");
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.list_layout, parent, false);
                    viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    viewHolder.txt1 = (TextView) convertView.findViewById(R.id.title2);
                    viewHolder.done = (ImageView) convertView.findViewById(R.id.done);
                    viewHolder.fileTypeIcone = (ImageView) convertView.findViewById(R.id.img);
                    convertView.setTag(viewHolder);
                    break;

                case 1:
                    Log.d(TAG, "getView: " + "folder");
                    LayoutInflater folderInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = folderInflater.inflate(R.layout.list_layout_folder, parent, false);
                    viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
                    viewHolder.folderTypeIcone = (ImageView) convertView.findViewById(R.id.img);
                    convertView.setTag(viewHolder);
                    break;
            }

        }
        viewHolder = (ViewHolder) convertView.getTag();
        //viewHolder.fileTypeIcone.setImageResource((getItem(position).isFile) ?  R.drawable.document : R.drawable.folder);
        //Upload non uploadé
        //On initialise les vues par défaut

        if(getItemViewType(position) == 0){
            Log.d(TAG, "getView: " + "file");
            if(getItem(position).isShowProgressbar()){
                viewHolder.progressBar.setVisibility(View.VISIBLE);//on cache le progressbar
            }

            if (getItem(position).isDownloaded() != true) {
                viewHolder.progressBar.setProgress(getItem(position).progress);
                viewHolder.txt1.setText(getItem(position).getFile().getName());
                viewHolder.done.setVisibility(View.INVISIBLE);

            }//Upload terminé
            else if (getItem(position).isDownloaded()) {
                //viewHolder.progressBar.setProgress(getItem(position).progress);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.done.setVisibility(View.VISIBLE);

            }
        }
        else if(getItemViewType(position) == 1){
            Log.d(TAG, "getView: " + "folder");
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.nom.setText(getItem(position).getFile().getName());
        }



        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).isFile){
            return 0;
        }
        if(getItem(position).getFile().isDirectory()){
            return 1;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolder {
        TextView txt1;
        TextView txt2;
        ImageView fileTypeIcone;
        ImageView done;
        ProgressBar progressBar;
        TextView nom;
        ImageView folderTypeIcone;

    }


}
