package com.lypeer.zybuluo.mixture.core;

/**
 * Created by 游小光 on 2017/1/3.
 */

public class MixtureResult {
    public enum MixtureState {DOWNLOADERROR, SUCCESS, EXCEPTION, CANCEL, NOCAMERA, NOCAMERAPERMISSION}
    public MixtureState state = MixtureState.SUCCESS;
    public String message = "";
    public String videoUrl = "";
}
