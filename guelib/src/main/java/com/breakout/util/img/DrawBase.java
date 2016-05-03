package com.breakout.util.img;

import android.graphics.drawable.Drawable;


/**
 * {@link ImageLoader}에서 baseColor, baseImage를 등록할때 해당 drawable에 ImageLoaderTask를 설정하고 
 * imageView에 유일성을 주기 위한 Interface
 * @author gue
 * @since 2012. 6. 15.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 */
interface DrawBase {
	
	/**
	 * {@link Drawable}을 extends한 class에 저장된 ImageLoaderTask를 가져온다.
	 * @author gue
	 * @since 2012. 6. 15.
	 */
	ImageLoaderTask getImageLoaderTask();
	
	/**
	 * {@link ImageLoader}에서 listView등의 row에 포함되는 imageView가 ViewHolder로 인하여 재사용 된다고 했을때 
	 * 빠른 스크롤로 인하여 이미 image의 다운로드가 시작이 되고 
	 * {@link ImageLoader#onCompleted}까지 진행하여 로드가 완료된 {@link ImageLoaderTask}를 통해 넘어온 url과 bitmap을 삽입하려는 
	 * imageView의 url을 비교하여 같을 경우에만 imageView를 재설정 함으로서 
	 * 연속된 스크롤로 인하여 {@link ImageLoaderTask#cancel}시점을 잡지 못하였을 경우의
	 * imageView에 image가 여러번 바뀌며 깜빡이는 현상을 제거한다.
	 * image의 url과 현재 
	 * @author gue
	 * @since 2012. 9. 10.
	 */
	String getDrawableTag();
}
