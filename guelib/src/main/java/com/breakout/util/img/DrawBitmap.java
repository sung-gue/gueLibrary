package com.breakout.util.img;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;


/**
 * @author sung-gue
 * @version 1.0 (2012. 6. 15.)
 */
class DrawBitmap extends BitmapDrawable implements DrawBase {
    /**
     * @see ImageLoader#download
     */
    private final WeakReference<ImageLoaderTask> imageLoaderTaskReference;
    private String tag;

    /*public DrawBitmap(ImageLoaderTask imageLoaderTask, Bitmap bitmap) {
        super(bitmap);
        imageLoaderTaskReference = new WeakReference<ImageLoaderTask>(imageLoaderTask);
    }*/

    public DrawBitmap(ImageLoaderTask imageLoaderTask, Bitmap bitmap, String tag) {
        super(null, bitmap);
        this.tag = tag;
        imageLoaderTaskReference = new WeakReference<ImageLoaderTask>(imageLoaderTask);
    }

    @Override
    public ImageLoaderTask getImageLoaderTask() {
        return imageLoaderTaskReference.get();
    }

    @Override
    public String getDrawableTag() {
        return tag;
    }
}