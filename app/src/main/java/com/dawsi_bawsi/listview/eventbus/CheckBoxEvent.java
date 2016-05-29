package com.dawsi_bawsi.listview.eventbus;

/**
 * Created by Spartiate on 23/05/2016.
 */
public class CheckBoxEvent {

    public int position;
    public boolean setIsSelected;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSetIsSelected() {
        return setIsSelected;
    }

    public void setSetIsSelected(boolean setIsSelected) {
        this.setIsSelected = setIsSelected;
    }
}
