package com.breakout.sample.device.flash;

import android.content.Context;
import android.content.Intent;

import com.breakout.sample.constant.Const;


/**
 * Flash Widget Provider 1x1
 *
 * @author sung-gue
 * @version 1.0 (2013. 10. 7.)
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
