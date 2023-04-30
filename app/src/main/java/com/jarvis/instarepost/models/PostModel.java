package com.jarvis.instarepost.models;

import java.util.ArrayList;

public class PostModel {
    private String caption;
    private String id;
    private ArrayList<MediaModel> media = new ArrayList<>();
    private String profile_pic_url;
    private String username;

    public String getCaption() {
        return this.caption;
    }

    public String getId() {
        return this.id;
    }

    public ArrayList<MediaModel> getMedia() {
        return this.media;
    }

    public String getProfile_pic_url() {
        return this.profile_pic_url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setCaption(String str) {
        this.caption = str;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setMedia(ArrayList<MediaModel> arrayList) {
        this.media = arrayList;
    }

    public void setProfile_pic_url(String str) {
        this.profile_pic_url = str;
    }

    public void setUsername(String str) {
        this.username = str;
    }
}
