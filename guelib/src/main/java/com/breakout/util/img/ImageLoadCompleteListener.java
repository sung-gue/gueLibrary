package com.breakout.util.img;

import android.graphics.Bitmap;
import android.widget.ImageView;


/**
 * {@link ImageLoaderTask}에서 이미지가 로딩이 끝났을 때 {@link ImageLoaderTask#onPostExecute(Bitmap)} 부분의 처리를
 * {@link ImageLoader}로 넘기기 위한 CallBack Interface
 *
 * @author sung-gue
 * @version 1.0 (2012. 6. 15.)
 */
public interface ImageLoadCompleteListener {
    /**
     * {@link ImageLoaderTask}에서 이미지가 로딩이 끝났을 때 {@link ImageLoaderTask#onPostExecute(Bitmap)} 부분의 처리를 받아준다.
     *
     * @param url       이미지의 url
     * @param imageView 이미지가 그려질 view
     * @param bitmap    url의 이미지를 다운로드 받아 디코딩한 bitmap
     * @param decodeErr 다운로드중이나 디코드시에 에러가 발생했을시에 true 를 리턴해준다.
     * @param forms     현재 다운로드 형식을 전달한다 new ing[] { {@link ImageLoaderTask#form}, {@link ImageLoaderTask#radiusPxToRound} }
     */
    void onCompleted(String url, ImageView imageView, Bitmap bitmap, boolean decodeErr, int[] forms);
}