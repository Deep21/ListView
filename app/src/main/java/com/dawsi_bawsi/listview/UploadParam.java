package com.dawsi_bawsi.listview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Spartiate on 17/04/2016.
 */
public class UploadParam {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("autorename")
    @Expose
    private Boolean autorename;
    @SerializedName("mute")
    @Expose
    private Boolean mute;
    @SerializedName("mode")
    @Expose
    private Mode mode;

    /**
     * @return The path
     */

    public String getPath() {
        return path;
    }

    /**
     * @param path The path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return The autorename
     */
    public Boolean getAutorename() {
        return autorename;
    }

    /**
     * @param autorename The autorename
     */
    public void setAutorename(Boolean autorename) {
        this.autorename = autorename;
    }

    /**
     * @return The mute
     */
    public Boolean getMute() {
        return mute;
    }

    /**
     * @param mute The mute
     */
    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    /**
     * @return The mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode The mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

}
