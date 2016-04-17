package com.dawsi_bawsi.listview;

import java.io.File;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel {
    boolean showProgressbar;
    String filename;
    long size;
    int fileIcone;
    int progress;
    FileType fileType;
    File file;
    int icone;
    private boolean isDownloaded;
    public FileModel(String name, long size, int icone) {
        this.filename = name;
        this.size = size;
        this.icone = icone;
    }
    public FileModel(int icone, File f) {
        this.file = f;
        this.icone = icone;
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

    public void getIconeByFileType(String filename) {
        switch (filename) {
            case "jgp":
                //fileIcone =
                break;

            case "png":
                break;

            case "txt":
                break;

            case "pdf":
                break;

            case "jpeg":
                break;

        }
    }

}