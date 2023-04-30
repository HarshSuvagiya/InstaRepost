package com.jarvis.instarepost.models;

public class MediaModel {
    private String display_url;
    private String file_path;
    private String id;
    private boolean is_video;
    private String photo_url;
    private String postId;
    private String preview_file_path;
    private boolean selected;
    private String video_url;

    public String getDisplay_url() {
        return this.display_url;
    }

    public String getFile_path() {
        return this.file_path;
    }

    public String getId() {
        return this.id;
    }

    public String getPhoto_url() {
        return this.photo_url;
    }

    public String getPostId() {
        return this.postId;
    }

    public String getPreview_file_path() {
        return this.preview_file_path;
    }

    public String getVideo_url() {
        return this.video_url;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isVideo() {
        return this.is_video;
    }

    public void setDisplay_url(String str) {
        this.display_url = str;
    }

    public void setFile_path(String str) {
        this.file_path = str;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setPhoto_url(String str) {
        this.photo_url = str;
    }

    public void setPostId(String str) {
        this.postId = str;
    }

    public void setPreview_file_path(String str) {
        this.preview_file_path = str;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setVideo(boolean z) {
        this.is_video = z;
    }

    public void setVideo_url(String str) {
        this.video_url = str;
    }
}
