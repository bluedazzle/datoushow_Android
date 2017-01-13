package com.lypeer.zybuluo.mixture.core;

/**
 * Created by 游小光 on 2016/12/30.
 */

public class HeadInfo {
    public int frame;
    public double x;
    public double y;
    public double rotation;
    public double size;
    public double time;

    public HeadInfo(int frame, double x, double y, double rotation, double size, double time) {
        this.frame = frame;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.size = size;
        this.time = time;
    }
}
