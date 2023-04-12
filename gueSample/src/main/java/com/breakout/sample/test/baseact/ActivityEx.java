package com.breakout.sample.test.baseact;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.util.AppCompatActivityEx;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.widget.DialogView;

/**
 * @author sung-gue
 * @version 1.0 (2020-11-24)
 */
public abstract class ActivityEx<SHARED extends SharedDataEx> extends AppCompatActivityEx {
    protected SHARED _shared;
    protected ImageLoader _imageLoader;

    protected abstract SHARED getSharedInstance(Context appContext);

    /*
        INFO: life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init field
        _shared = getSharedInstance(_appContext);
        _imageLoader = ImageLoader.getInstance(_appContext);
        _imageLoader.setsdErrStr(getString(R.string.al_sdcard_strange_condition));
    }

    protected void finishToast() {
        finishToast(getString(R.string.al_not_match_data));
    }

    protected void finishToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }


    /*
        INFO: progress dialog
     */
    private Dialog _pDialog;

    @Override
    public Dialog showProgress() {
        if (_pDialog == null) {
            ProgressBar progressBar = new ProgressBar(_context, null, DialogView.Size.large.defStyle);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(
                    _context,
                    R.color.progress_swipeRefreshLayout
            ), PorterDuff.Mode.MULTIPLY); //PorterDuff.Mode.SRC_IN
            DialogView dv = new DialogView(_context, progressBar, null);
            _pDialog = dv.getDialog(false, false);
        }
        if (!isFinishing() && !_pDialog.isShowing()) {
            try {
                _pDialog.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return _pDialog;
    }

    @Override
    public void closeProgress() {
        if (_pDialog != null && _pDialog.isShowing()) {
            _pDialog.dismiss();
            _pDialog = null;
        }
    }


    /*
        INFO: activity callback
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (intent.getComponent() != null) {
            sendBroadcast(new Intent(intent.getComponent().getClassName()));
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    /*
        INFO: finish animation
     */
    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        closeProgress();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
