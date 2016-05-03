package com.breakout.util.widget;

import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * generate View id
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2016.gue.All rights reserved.
 * @since 2016.02.22
 */
public final class GenViewId {
    final static private AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static int _generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return _generateViewId();
        } else {
            return View.generateViewId();
        }
    }
}
