package com.dawsi_bawsi.listview;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel {
    String filename;
    long size;
    int photoId;
    int progress;
    private boolean isSelected;
    private boolean isDownloaded;

    public FileModel(String name, long size) {
        this.filename = name;
        this.size = size;
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
}