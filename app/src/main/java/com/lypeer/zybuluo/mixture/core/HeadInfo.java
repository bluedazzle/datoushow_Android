package com.lypeer.zybuluo.mixture.core;

/**
 * Created by 游小光 on 2016/12/30.
 */

public class HeadInfo {
    public final int frame;
    public final double x;
    public final double y;
    public final double rotation;
    public final double size;
    public final double time;

    public HeadInfo(int frame, double x, double y, double rotation, double size, double time) {
        this.frame = frame;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.size = size;
        this.time = time;
    }
}
