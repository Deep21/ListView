package com.dawsi_bawsi.listview.eventbus;

/**
 * Created by Spartiate on 29/05/2016.
 */
public class FragmentSelectEvent {
    private String fragementName;
    private String absolutePath;

    public void setFragementName(String fragementName) {
        this.fragementName = fragementName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFragmentName() {
        return null;
    }
}
