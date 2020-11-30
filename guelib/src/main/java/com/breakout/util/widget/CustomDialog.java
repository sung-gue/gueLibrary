package com.breakout.util.widget;


import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;


/**
 * Custom Alert Dialog
 *
 * @author sung-gue
 * @version 1.0 (2012. 12. 31.)
 */
public class CustomDialog extends AlertDialog {
    private Context _context;


    private CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
        _context = context;
        init();
    }

    private void init() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getWindow().getAttributes().windowAnimations = android.R.style.Animation_InputMethod;
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    public CustomDialog setContents(String title, String message) {
        super.setTitle(title);
        super.setMessage(message);
        return this;
    }

    public CustomDialog setContents(int titleId, int messageId) {
        String title = null;
        String message = null;
        try {
            title = getContext().getString(titleId);
        } catch (Exception ignored) {
        }
        try {
            message = getContext().getString(messageId);
        } catch (Exception ignored) {
        }
        return this.setContents(title, message);
    }

    public CustomDialog setOkBt(String btStr, OnClickListener listener) {
        setButton(DialogInterface.BUTTON_POSITIVE, btStr, listener);
        return this;
    }

    public CustomDialog setOkBt(int btStrId, OnClickListener listener) {
        String btStr = null;
        try {
            btStr = getContext().getString(btStrId);
        } catch (Exception ignored) {
        }
        return this.setOkBt(btStr, listener);
    }

    public CustomDialog setCancelBt(String btStr, OnClickListener listener) {
        setButton(DialogInterface.BUTTON_NEGATIVE, btStr, listener);
        return this;
    }

    public CustomDialog setCancelBt(int btStrId, OnClickListener listener) {
        String btStr = null;
        try {
            btStr = getContext().getString(btStrId);
        } catch (Exception ignored) {
        }
        return this.setCancelBt(btStr, listener);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        super.setCanceledOnTouchOutside(flag);
    }

    public CustomDialog setCancel(boolean flag) {
        this.setCancelable(flag);
        return this;
    }

    public CustomDialog clearDimBehind() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return this;
    }

}