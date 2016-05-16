package com.dawsi_bawsi.listview.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Spartiate on 29/02/2016.
 */
public class Upload {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("path_lower")
    @Expose
    private String pathLower;
    @SerializedName("path_display")
    @Expose
    private String pathDisplay;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("client_modified")
    @Expose
    private String clientModified;
    @SerializedName("server_modified")
    @Expose
    private String serverModified;
    @SerializedName("rev")
    @Expose
    private String rev;
    @SerializedName("size")
    @Expose
    private long size;


    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The pathLower
     */
    public String getPathLower() {
        return pathLower;
    }

    /**
     * @param pathLower The path_lower
     */
    public void setPathLower(String pathLower) {
        this.pathLower = pathLower;
    }

    /**
     * @return The pathDisplay
     */
    public String getPathDisplay() {
        return pathDisplay;
    }

    /**
     * @param pathDisplay The path_display
     */
    public void setPathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The clientModified
     */
    public String getClientModified() {
        return clientModified;
    }

    /**
     * @param clientModified The client_modified
     */
    public void setClientModified(String clientModified) {
        this.clientModified = clientModified;
    }

    /**
     * @return The serverModified
     */
    public String getServerModified() {
        return serverModified;
    }

    /**
     * @param serverModified The server_modified
     */
    public void setServerModified(String serverModified) {
        this.serverModified = serverModified;
    }

    /**
     * @return The rev
     */
    public String getRev() {
        return rev;
    }

    /**
     * @param rev The rev
     */
    public void setRev(String rev) {
        this.rev = rev;
    }

    /**
     * @return The size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(long size) {
        this.size = size;
    }
}
