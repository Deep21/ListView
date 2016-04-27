package com.dawsi_bawsi.listview;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Spartiate on 11/03/2016.
 */
class FileModel implements Parcelable {
    boolean showProgressbar;
    String filename;
    long size;
    int progress;
    File file;
    int icone;
    boolean isFile;
    private boolean isDownloaded;

    public FileModel(String name, long size, int icone) {
        this.filename = name;
        this.size = size;
        this.icone = icone;
    }

    public FileModel(int icone, File f) {
    }

    public FileModel(File f) {
        this.file = f;
        isFile = file.isFile();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(showProgressbar ? (byte) 1 : (byte) 0);
        dest.writeString(this.filename);
        dest.writeLong(this.size);
        dest.writeInt(this.progress);
        dest.writeSerializable(this.file);
        dest.writeInt(this.icone);
        dest.writeByte(isFile ? (byte) 1 : (byte) 0);
        dest.writeByte(isDownloaded ? (byte) 1 : (byte) 0);
    }

    protected FileModel(Parcel in) {
        this.showProgressbar = in.readByte() != 0;
        this.filename = in.readString();
        this.size = in.readLong();
        this.progress = in.readInt();
        this.file = (File) in.readSerializable();
        this.icone = in.readInt();
        this.isFile = in.readByte() != 0;
        this.isDownloaded = in.readByte() != 0;
    }

    public static final Parcelable.Creator<FileModel> CREATOR = new Parcelable.Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel source) {
            return new FileModel(source);
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };
}