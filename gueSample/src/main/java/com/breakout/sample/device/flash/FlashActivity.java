package com.breakout.sample.device.flash;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.constant.Const;
import com.breakout.sample.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.breakout.util.device.CameraUtil;

public class FlashActivity extends BaseActivity implements OnClickListener{
	
	private final int FLASH_ID = 1234;
	private ImageView _ivFlash;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(null, false);
		
		registerReceiver(_receiver, new IntentFilter(Const.BR_FLASH_ACTIVITY));
		super.initUI();
	}
	
	@Override
	protected void initTitle() {}

	@Override
	protected void initFooter() {}

	@Override
	protected void initBody() {
		_vParent.setBackgroundColor(0xff31302d);
		_vParent.setGravity(Gravity.CENTER);
		_ivFlash = new ImageView(this);
		_ivFlash.setLayoutParams(getMarginLayoutParams(new LinearLayout.LayoutParams(-2,-2), 0, 0, 0, 0) );
		_ivFlash.setImageResource(R.drawable.bt_flash_off);
		_ivFlash.setId(FLASH_ID);
		_ivFlash.setOnClickListener(this);
		_vParent.addView(_ivFlash);
	}

	@Override
	protected void refreshUI() {}

	private void setFlashBtn(boolean isFlashOn){
		if (isFlashOn) {
			_ivFlash.setImageResource(R.drawable.bt_flash_on);
		}
		else {
			_ivFlash.setImageResource(R.drawable.bt_flash_off);
			
		}
	}
	
	
/* ************************************************************************************************
 * INFO listener
*/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case FLASH_ID:
				if (CameraUtil.checkCameraHardware(_appContext)) {
					boolean isFlashOn = FlashWidgetConfigure.flashOnOff(this);
					FlashWidgetConfigure.updateWidgetLayoutForFlash(_appContext);
					setFlashBtn(isFlashOn);
				}
				break;
		}
	}
	
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			setFlashBtn(false);
		}
	};


/* ************************************************************************************************
 * INFO life cycle
 */
	@Override
	protected void onResume() {
//		FlashWidgetConfigure.flashOff(this);
		sendBroadcast(new Intent(Const.BR_FLASH_WIDGET_NOTIFICATION));
		super.onResume();
	}
	
	@Override
    protected void onPause() {
//		FlashWidgetConfigure.flashOff(this);
		sendBroadcast(new Intent(Const.BR_FLASH_WIDGET_NOTIFICATION));
    	super.onPause();
    }

	@Override
	protected void onDestroy() {
		unregisterReceiver(_receiver);
		super.onDestroy();
	}
}
