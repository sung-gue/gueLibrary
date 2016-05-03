package com.breakout.util.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.breakout.util.widget.DialogView.Size;

/**
 * 작업중 : 작업 완료 되면 지시자 public 적용예정<br>
 * Dialog의 안의 View를 사용자가 원하는 View로 설정하여 Activity위에 보여지게 한다.
 * @author gue
 * @since 2012. 12. 31.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
class CustomDialog extends Dialog {
//	private final String TAG = "CustomDialog | ";

	/*public ProgressImage(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}
	public ProgressImage(Context context, int theme) {
		super(context, theme);
		init();
	}*/
	
	/**
	 * {@link DialogView}를 삽입한다.
	 */
	public CustomDialog(Context context) {
		super(context);
		init(null);
	}

	/**
	 * 원하는 View를 삽입한다.
	 */
	public CustomDialog(Context context, View view) {
		super(context);
		init(view);
	}
	
	private void init(View view) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().getAttributes().windowAnimations = android.R.style.Animation_InputMethod;
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		
		setContentView(view);
		setCancelable(false);
	}
	
	@Override
	public void setContentView(View view) {
		if (view == null) {
			view = new DialogView(getContext(), Size.small);
		} 
		super.setContentView(view);
	}
	
	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
		super.setCanceledOnTouchOutside(flag);
	}
	
}
