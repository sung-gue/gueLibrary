package com.breakout.util.res;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 기존 xml animation resource를 code로 변환하여 라이브러리만을 import하여도 
 * 지정된 animation을 사용 가능 
 * @author gue
 * @since 2012. 12. 27.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class AnimationSuite {

/* ************************************************************************************************
 * INFO AlphaAnimation
 */
	/**
	 * fromAlpha = 0, toAlpha = 1
	 * @param interpolator null일경우 {@link DecelerateInterpolator}로 설정
	 * @author gue
	 */
	public final static Animation fadeIn(long duration, Interpolator interpolator) {
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(duration);
		if (interpolator == null) interpolator = new DecelerateInterpolator();
		anim.setInterpolator(interpolator);
		return anim;
	}
	
	/**
	 * see {@link #fadeIn(long, Interpolator)}
	 */
	public final static Animation fadeIn(long duration) {
		return fadeIn(duration, null);
	}
	
	/**
	 * duration 500 고정<br>
	 * see {@link #fadeIn(long, Interpolator)}
	 */
	public final static Animation fadeIn() {
		return fadeIn(500, null);
	}
	
	/**
	 * fromAlpha = 1, toAlpha = 0
	 * @param duration
	 * @param interpolator null일경우 {@link DecelerateInterpolator}로 설정
	 * @author gue
	 */
	public final static Animation fadeOut(long duration, Interpolator interpolator) {
		Animation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(duration);
		if (interpolator == null) interpolator = new DecelerateInterpolator();
		anim.setInterpolator(interpolator);
		return anim;
	}
	
	/**
	 * see {@link #fadeOut(long, Interpolator)}
	 */
	public final static Animation fadeOut(long duration) {
		return fadeOut(duration, null);
	}

	/**
	 * duration 500 고정<br>
	 * see {@link #fadeOut(long, Interpolator)}
	 */
	public final static Animation fadeOut() {
		return fadeOut(500, null);
	}
	
	
/* ************************************************************************************************
 * INFO RotateAnimation
 */
	/**
	 * 중심축 x,y : Animation.RELATIVE_TO_SELF, 0.5f<br>
	 * @param interpolator null일경우 {@link LinearInterpolator}로 설정
	 * @author gue
	 */
	public final static Animation rotate(float fromDegree, float toDegree, long duration, Interpolator interpolator) {
		Animation anim = new RotateAnimation(	fromDegree, toDegree, 
												Animation.RELATIVE_TO_SELF, 0.5f, 
												Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(duration);
		if (interpolator == null ) interpolator = new LinearInterpolator(); 
		anim.setInterpolator(interpolator);
		return anim;
	}
	
	/**
	 * see {@link #rotate(float, float, long, Interpolator)}
	 */
	public final static Animation rotate(float fromDegree, float toDegree, long duration) {
		return rotate(fromDegree, toDegree, duration, null);
	}
	
	/**
	 * duration 500 고정<br>
	 * see {@link #rotate(float, float, long, Interpolator)}
	 */
	public final static Animation rotate(float fromDegree, float toDegree) {
		return rotate(fromDegree, toDegree, 500, null);
	}


/* ************************************************************************************************
 * INFO TranslateAnimation
 */
	public final static Animation hold(long duration) {
		Animation anim = new TranslateAnimation(	Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, 
													Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		anim.setDuration(duration);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}
	
	public final static Animation hold() {
		return hold(500);
	}
	

/* ************************************************************************************************
 * INFO Twin Animation
 */
	/**
	 * 중심축 x,y : Animation.RELATIVE_TO_SELF, 0.5f<br>
	 * @author gue
	 */
	public final static Animation zoomIn(long duration) {
		AnimationSet set = new AnimationSet(true);
		
		ScaleAnimation scale = new ScaleAnimation(	0.0f, 1.0f, 0.0f, 1.0f, 
													Animation.RELATIVE_TO_SELF, 0.5f, 
													Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration(duration);
		
		AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
		alpha.setDuration(duration);
		
		set.addAnimation(scale);
		set.addAnimation(alpha);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}
	
	/**
	 * duration 500 고정<br>
	 * see {@link #zoomIn(long)}
	 */
	public final static Animation zoomIn() {
		return zoomIn(500);
	}
	
	/**
	 * 중심축 x,y : Animation.RELATIVE_TO_SELF, 0.5f<br>
	 * @author gue
	 */
	public final static Animation zoomOut(long duration) {
		AnimationSet set = new AnimationSet(true);
		
		ScaleAnimation scale = new ScaleAnimation(	1.0f, 0.0f, 1.0f, 0.0f, 
													Animation.RELATIVE_TO_SELF, 0.5f, 
													Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration(duration);
		
		AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
		alpha.setDuration(duration);
		
		set.addAnimation(scale);
		set.addAnimation(alpha);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}

	/**
	 * duration 500 고정<br>
	 * see {@link #zoomOut(long)}
	 */
	public final static Animation zoomOut() {
		return zoomOut(500);
	}
	

/* ************************************************************************************************
 * INFO Twin Animation
 */
	/**
	 * 정방향 아이콘 흔들림 
	 * @author gue
	 */
	public final static Animation wiggleIconF(long duration) {
		AnimationSet set = new AnimationSet(true);
		
		RotateAnimation rotate1 = new RotateAnimation(	0, -2, 
														Animation.RELATIVE_TO_SELF, 0.4f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate1.setDuration(duration);
		rotate1.setRepeatMode(Animation.RESTART);
		
		RotateAnimation rotate2 = new RotateAnimation(	-2, 2, 
														Animation.RELATIVE_TO_SELF, 0.4f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate2.setDuration(duration*2);
		rotate2.setStartOffset(duration);
		rotate2.setRepeatMode(Animation.RESTART);
		
		RotateAnimation rotate3 = new RotateAnimation(	2, 0, 
														Animation.RELATIVE_TO_SELF, 0.4f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate3.setDuration(duration);
		rotate3.setStartOffset(duration*3);
		rotate2.setRepeatMode(Animation.RESTART);
		
		set.addAnimation(rotate1);
		set.addAnimation(rotate2);
		set.addAnimation(rotate3);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}

	/**
	 * duration 70 고정<br>
	 * see {@link #wiggleIconF(long)}
	 */
	public final static Animation wiggleIconF() {
		return wiggleIconF(70);
	}
	
	/**
	 * 역방향 아이콘 흔들림 
	 * @author gue
	 */
	public final static Animation wiggleIconR(long duration) {
		AnimationSet set = new AnimationSet(true);
		
		RotateAnimation rotate1 = new RotateAnimation(	0, 2, 
														Animation.RELATIVE_TO_SELF, 0.6f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate1.setDuration(duration);
		rotate1.setRepeatMode(Animation.RESTART);
		
		RotateAnimation rotate2 = new RotateAnimation(	2, -2, 
														Animation.RELATIVE_TO_SELF, 0.6f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate2.setDuration(duration*2);
		rotate2.setStartOffset(duration);
		rotate2.setRepeatMode(Animation.RESTART);
		
		RotateAnimation rotate3 = new RotateAnimation(	-2, 0, 
														Animation.RELATIVE_TO_SELF, 0.6f, 
														Animation.RELATIVE_TO_SELF, 0.4f);
		rotate3.setDuration(duration);
		rotate3.setStartOffset(duration*3);
		rotate2.setRepeatMode(Animation.RESTART);
		
		set.addAnimation(rotate1);
		set.addAnimation(rotate2);
		set.addAnimation(rotate3);
		set.setInterpolator(new DecelerateInterpolator());
		return set;
	}
	
	/**
	 * duration 70 고정<br>
	 * see {@link #wiggleIconR(long)}
	 */
	public final static Animation wiggleIconR() {
		return wiggleIconR(70);
	}
	
	
	
}
