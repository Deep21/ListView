package com.dawsi_bawsi.listview;

import java.io.File;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel {
    boolean showProgressbar;
    String filename;
    long size;
    boolean isSelected;
    File file;
    boolean isFile;
    private boolean isDownloaded;
    int position;

    public FileModel(String name, long size) {
        this.filename = name;
        this.size = size;
    }

    public FileModel(File f) {
        this.file = f;
        isFile = file.isFile();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isShowProgressbar() {
        return showProgressbar;
    }

    public void setShowProgressbar(boolean showProgressbar) {
        this.showProgressbar = showProgressbar;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

}