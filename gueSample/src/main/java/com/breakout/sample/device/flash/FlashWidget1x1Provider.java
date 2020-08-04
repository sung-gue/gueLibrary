package com.breakout.sample.device.flash;

import com.breakout.sample.constant.Const;
import android.content.Context;
import android.content.Intent;


/**
 * Flash Widget Provider 1x1
 * @author gue
 * @since 2013. 10. 7.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public class FlashWidget1x1Provider extends FlashWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent != null && Const.WIDGET_1X1_CLICK_ACTION.equals(intent.getAction())) {
			String mode = intent.getStringExtra(Const.EX_FLASH_WIDGET_CLICK);
			if (Const.FLASH_WIDGET_FLASH_CLICK.equals(mode)) {
				FlashWidgetConfigure.flashOnOff(context);
			}
		}
	}
	
}
