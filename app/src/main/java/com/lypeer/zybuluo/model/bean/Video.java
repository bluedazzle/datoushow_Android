package com.lypeer.zybuluo.model.bean;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * Created by lypeer on 2017/1/9.
 */

public class Video {

    private int id;
    private String title;
    private String artist;
    private String path;
    private Bitmap thumbnail;

    public Video(int id, String title, String artist, String path) {
        super();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.setThumbnail();
    }

    public void setThumbnail() {
        try {
            this.thumbnail = ThumbnailUtils.createVideoThumbnail(getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
        } catch (Exception e) {
            e.printStackTrace();
            this.thumbnail = null;
        }
    }

    public Bitmap getThumbnail() {
        if (thumbnail == null) {
            this.setThumbnail();
            return this.thumbnail;
        }
        return this.thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        if (artist == null) {
            return "";
        }
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        if (path == null) {
            return "";
        }
        return path;
    }
}
