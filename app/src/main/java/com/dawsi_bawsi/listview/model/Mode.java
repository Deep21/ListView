package com.dawsi_bawsi.listview.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Spartiate on 17/04/2016.
 */

public class Mode {

    @SerializedName(".tag")
    @Expose
    private String Tag;

    /**
     *
     * @return
     * The Tag
     */
    public String getTag() {
        return Tag;
    }

    /**
     *
     * @param Tag
     * The .tag
     */
    public void setTag(String Tag) {
        this.Tag = Tag;
    }

}