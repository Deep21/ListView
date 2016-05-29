package com.dawsi_bawsi.listview.model;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Spartiate on 29/05/2016.
 */
public class FileModel extends File{
    public static final int EMPTY = 0;
    public static final int FULL = 100;
    private static final String TAG = "ExplorerFileModel";
    private boolean showProgressbar;
    private boolean isSelected;
    private boolean isDownloaded;
    private int position;


    public FileModel(String path) {
        super(path);
    }

    public boolean isShowProgressbar() {
        return showProgressbar;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<File> read() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Arrays.asList(listFiles());

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return Arrays.asList(listFiles());
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
        }
        return null;

    }
}
