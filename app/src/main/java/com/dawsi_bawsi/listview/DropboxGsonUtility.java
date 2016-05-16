package com.dawsi_bawsi.listview;

import com.dawsi_bawsi.listview.model.Mode;
import com.dawsi_bawsi.listview.model.UploadParam;
import com.google.gson.Gson;

import java.io.File;

/**
 * Created by Spartiate on 15/05/2016.
 */
public class DropboxGsonUtility {

    public static String uploadParams(File file){
        String path = "/CV/" + file.getName();
        UploadParam uploadParam = new UploadParam();
        uploadParam.setPath(path);
        uploadParam.setAutorename(true);
        uploadParam.setMute(false);
        Mode mode = new Mode();
        mode.setTag("add");
        uploadParam.setMode(mode);
        Gson gson = new Gson();
        String params = gson.toJson(uploadParam);
        return params;
    }
}
