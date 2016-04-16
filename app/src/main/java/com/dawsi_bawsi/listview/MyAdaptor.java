package com.dawsi_bawsi.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Spartiate on 13/03/2016.
 */
public class MyAdaptor extends ArrayAdapter<FileModel> {
    private static final String TAG = "MyAdaptor";
    int resource;

    public List<FileModel> getPersons() {
        return persons;
    }

    public void setPersons(List<FileModel> persons) {
        this.persons = persons;
    }

    List<FileModel> persons;
    Context context;

    public MyAdaptor(Context context, int resource, List<FileModel> objects) {
        super(context, resource, objects);
        this.persons = objects;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public int getPosition(FileModel item) {

        return super.getPosition(item);
    }


    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public FileModel getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return persons.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            Log.d(TAG, "getView : null " + position + convertView);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_layout, parent, false);
            viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
            viewHolder.txt1 = (TextView)convertView.findViewById(R.id.title2);
            viewHolder.done = (ImageView)convertView.findViewById(R.id.done);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        //Upload non uploadé
        //On initialise les vues par défaut
        if(!getItem(position).isDownloaded()){
            viewHolder.progressBar.setProgress(getItem(position).progress);
            viewHolder.txt1.setText(getItem(position).filename);
            viewHolder.done.setVisibility(View.INVISIBLE);

        }//Upload terminé
        else if(getItem(position).isDownloaded()){
            //viewHolder.progressBar.setProgress(getItem(position).progress);
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.done.setVisibility(View.VISIBLE);

        }

        return convertView;
    }

    class ViewHolder{
        TextView txt1;
        TextView txt2;
        ImageView imageView;
        ImageView done;
        ProgressBar progressBar;

    }


}
