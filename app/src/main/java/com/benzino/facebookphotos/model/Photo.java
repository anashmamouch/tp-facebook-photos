package com.benzino.facebookphotos.model;

/**
 * Created on 16/4/16.
 *
 * @author Anas
 */
public class Photo {
    private String id;
    private String url;

    private boolean checked = true;

    public Photo(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public Photo(String id, String url, boolean checked) {
        this.id = id;
        this.url = url;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
