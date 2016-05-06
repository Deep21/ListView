package com.dawsi_bawsi.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
    public static final int FILE_VIEW = 0;
    public static final int FOLDER_VIEW = 1;

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

                case FILE_VIEW:
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.list_layout, parent, false);
                    viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    viewHolder.txt1 = (TextView) convertView.findViewById(R.id.title2);
                    viewHolder.done = (ImageView) convertView.findViewById(R.id.done);
                    viewHolder.fileTypeIcone = (ImageView) convertView.findViewById(R.id.img);
                    viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                    convertView.setTag(viewHolder);
                    break;


                case FOLDER_VIEW:
                    LayoutInflater folderInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = folderInflater.inflate(R.layout.list_layout_folder, parent, false);
                    viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
                    viewHolder.folderTypeIcone = (ImageView) convertView.findViewById(R.id.img);
                    convertView.setTag(viewHolder);
                    break;
            }

        }
        viewHolder = (ViewHolder) convertView.getTag();

        if(getItemViewType(position) == FILE_VIEW){

            viewHolder.done.setImageResource(R.drawable.upload);
            //On initialise les vues par défaut
            if(getItem(position).isShowProgressbar()){
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }

            if (getItem(position).isDownloaded() != true) {
                viewHolder.progressBar.setProgress(0);
                viewHolder.txt1.setText(getItem(position).getFile().getName());
                //viewHolder.done.setVisibility(View.INVISIBLE);
            }
            //Upload terminé
            else if (getItem(position).isDownloaded()) {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.done.setImageResource(R.drawable.right);
                viewHolder.done.setVisibility(View.VISIBLE);

            }

            //TODO
            //Ajout de tableau ds le adaptor pour multi upload
            //puis a lexterieur de ladaptor récupérer la liste et uploader
        }
        else if(getItemViewType(position) == FOLDER_VIEW){
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.nom.setText(getItem(position).getFile().getName());
        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        if(getItem(position).isFile){
            return FILE_VIEW;
        }
        else if(getItem(position).getFile().isDirectory()){
            return FOLDER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolder {
        TextView txt1;
        ImageView fileTypeIcone;
        ImageView done;
        ProgressBar progressBar;
        TextView nom;
        ImageView folderTypeIcone;
        CheckBox checkBox;

    }


}
