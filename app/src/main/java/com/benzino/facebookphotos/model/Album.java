package com.benzino.facebookphotos.model;

/**
 * Created on 15/4/16.
 *
 * @author Anas
 */
public class Album {
    String id;
    String title;
    String coverPhotoUrl;

    public Album() {
    }

    public Album(String title) {
        this.title = title;
    }

    public Album(String title, String coverPhotoUrl) {
        this.title = title;
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public Album(String id, String title, String coverPhotoUrl) {
        this.id = id;
        this.title = title;
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
