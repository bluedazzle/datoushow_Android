package com.lypeer.zybuluo.model.bean;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
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
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(getPath());
            bitmap = retriever.getFrameAtTime(-1);

            if (bitmap == null)
                return;

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 720) {
                float scale = 720f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }

            thumbnail = bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            thumbnail = null;
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
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
