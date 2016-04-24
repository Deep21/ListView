package com.dawsi_bawsi.listview;

import java.io.File;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel {
    String filename;
    long size;
    int fileIcone;
    int progress;
    File file;
    int icone;
    private boolean isDownloaded;

    public FileModel(String name, long size, int icone) {
        this.filename = name;
        this.size = size;
        this.icone = icone;
    }

    public FileModel(File f) {
        this.file = f;
        this.icone = icone;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }


}