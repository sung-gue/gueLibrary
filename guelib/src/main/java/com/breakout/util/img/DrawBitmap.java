package com.breakout.util.img;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


/**
 * 
 * @author gue
 * @since 2012. 6. 15.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
class DrawBitmap extends BitmapDrawable implements DrawBase{
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
