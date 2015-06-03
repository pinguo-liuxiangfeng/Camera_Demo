package com.example.liuxiangfeng.arcseekbar;

import android.graphics.drawable.Drawable;

/**
 * Created by liuxiangfeng on 15-5-11.
 */
public interface ScaleAdapter<T extends ScaleAdapter.Scale> {

    public int getScaleCount();

    public int getScaleViewCount();

    public T getScale(int position);

    public interface Scale {
        public String getText();

        public Drawable getDrawable();
    }

}
