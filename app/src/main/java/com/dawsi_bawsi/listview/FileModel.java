package com.dawsi_bawsi.listview;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel {
    String filename;
    long size;
    int fileIcone;
    int progress;
    private boolean isDownloaded;
    FileType fileType;

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    int icone;

    public FileModel(String name, long size, int icone) {
        this.filename = name;
        this.size = size;
        this.icone = icone;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public void getIconeByFileType(String filename){
        switch (filename){
            case "jgp" :
                //fileIcone =
                break;

            case "png" :
                break;

            case "txt" :
                break;

            case "pdf" :
                break;

            case "jpeg" :
                break;

        }
    }

}