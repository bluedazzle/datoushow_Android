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
    private String album;
    private String artist;
    private String displayName;
    private String mimeType;
    private String path;
    private Bitmap thumbnail;
    private long size;
    private long duration;

    public Video(int id, String title, String album, String artist,
                 String displayName, String mimeType, String path, long size,
                 long duration) {
        super();
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.duration = duration;
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

    public String getAlbum() {
        if (album == null) {
            return "";
        }
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public String getDisplayName() {
        if (displayName == null) {
            return "";
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        if (mimeType == null) {
            return "";
        }
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        if (path == null) {
            return "";
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
