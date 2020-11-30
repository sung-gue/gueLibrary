package com.breakout.sample.device.flash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.breakout.sample.constant.Const;


/**
 * Flash Widget Provider 2x1
 *
 * @author sung-gue
 * @version 1.0 (2013. 10. 2.)
 */
public class FlashWidget2x1Provider extends FlashWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent != null && Const.WIDGET_2X1_CLICK_ACTION.equals(intent.getAction())) {
            String mode = intent.getStringExtra(Const.EX_FLASH_WIDGET_CLICK);

            if (Const.FLASH_WIDGET_FLASH_CLICK.equals(mode)) {
                FlashWidgetConfigure.flashOnOff(context);
            } else if (Const.FLASH_WIDGET_LINK_CLICK.equals(mode)) {
                Intent goUrlIntent = new Intent(Intent.ACTION_VIEW);
                goUrlIntent.setData(Uri.parse(Const.URL_HOME));
                goUrlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(goUrlIntent);
            }
        }
    }

}
