package com.breakout.util.img;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.breakout.util.Log;


/**
 * Bitmap Util
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 15.)
 */
@SuppressWarnings("ALL")
public class BitmapUtil {
    private final static String TAG = "BitmapUtil";

    /**
     * recycle bitmap
     */
    public static void recycleBitmap(@NonNull Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            Log.d(TAG, "recycleBitmap [" + bitmap + "]- " + bitmap.isRecycled());
        }
    }

    /**
     * imageView 안의 bitmap을 해제한다.
     */
    public static void recycleBitmapInImageView(@NonNull ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            recycleBitmap(bitmap);
            bitmap = null;
        }
        imageView.setImageBitmap(null);
    }
}