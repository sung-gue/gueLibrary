/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.breakout.util.img;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


/**
 * A fake Drawable that will be attached to the imageView while the download is in progress.
 * <p>Contains a reference to the actual download task, so that a download task can be stopped
 * if a new binding is required, and makes sure that only the last started download process can
 * bind its result, independently of the download finish order.</p>
 */
class DrawColor extends ColorDrawable implements DrawBase{
	/**
	 * @see ImageLoader#download
	 */
    private final WeakReference<ImageLoaderTask> task;
    private String tag;

    /*public DrawColor(ImageLoaderTask imageLoaderTask) {
        super(Color.parseColor("#00000000"));
        task = new WeakReference<ImageLoaderTask>(imageLoaderTask);
    }*/
    
    public DrawColor(ImageLoaderTask imageLoaderTask, String tag) {
    	super(Color.parseColor("#00000000"));
    	this.tag = tag;
    	task = new WeakReference<ImageLoaderTask>(imageLoaderTask);
    }
    
    @Override
	public ImageLoaderTask getImageLoaderTask() {
        return task.get();
    }
    
    @Override
    public String getDrawableTag() {
    	return tag;
    }
}
