package com.breakout.sample.util.photo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.util.photo.data.PhotoData;
import com.breakout.sample.views.AppBar;
import com.breakout.util.Util;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.img.ImageUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;


/**
 * The type Camera activity.
 *
 * @author sung-gue
 * @version 1.0 (2013.11.11)
 */
public class CameraActivity extends BaseActivity {
    public static final String BR_PHOTO_UPLOAD = "br_photo_upload";
    public static final String UPLOAD_PHOTO_DATA = "upload_photo_data";

    private final int REQUEST_CAMERA = 0;
    private Uri _originalImageUri;
    private String _cacheDir;
    private String _cacheDir1;

    /**
     * 사진 업로드를 위해 필요한 intent extra class
     */
    private PhotoData _photoData;

    @Override
    protected void analyticsRecordScreen(FirebaseAnalytics firebaseAnalytics) {
//        _firebaseAnalytics.setCurrentScreen(this, "메인", null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //registerfinishReceiver(BR_PHOTO_UPLOAD);

        Intent intent = getIntent();
        _photoData = intent.getParcelableExtra(UPLOAD_PHOTO_DATA);

        if (Util.isExternalStorageAvailable()) {

            // get cache dir
            if (_context.getExternalCacheDir() != null) {
                _cacheDir = _context.getExternalCacheDir().getAbsolutePath() + "/";
            } else {
                _cacheDir = _context.getCacheDir().getAbsolutePath() + "/";
            }

            // get cache dir 1
            _cacheDir1 = Environment.getExternalStorageDirectory().toString() + "/folder_name/";

            // 사진첩 dir
            File saveFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Const.APP_NAME);
            if (!saveFolder.exists() && !saveFolder.mkdir()) {
                Toast.makeText(this, R.string.al_sdcard_write_fail, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    _originalImageUri = Uri.fromFile(new File(saveFolder, System.currentTimeMillis() + ".jpg"));
                    ImageUtil.callCamera(this, REQUEST_CAMERA, _originalImageUri);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, R.string.al_sdcard_not_found, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (_originalImageUri == null) {
                _originalImageUri = ImageUtil._ouputUri;
            }
            ImageUtil._ouputUri = null;
            Log.i(TAG, "onActivityResult() pick camera uri : " + _originalImageUri);
            String path = _originalImageUri.getPath();
            DeviceUtil.updateMediaScan(this, path);

            if (new File(path).exists()) {
                Intent intent = new Intent();
                _photoData.imagePathList.clear();
                _photoData.imagePathList.add(path);
                intent.putExtra(UPLOAD_PHOTO_DATA, _photoData);
                switch (_photoData.uploadLocation) {
                    case DEFAULT:
                        setResult(RESULT_OK, intent);
                        break;
                    default:
                        /*intent.setClass(_context, PhotoSaveActivity.class);
                        startActivity(intent);*/
                        break;
                }
            }
        }
        finish();
    }

    @Override
    protected void initTitle(AppBar appBar) {

    }

    @Override
    protected void initFooter() {

    }

    @Override
    protected void initBody() {

    }

    @Override
    protected void refreshUI() {

    }
}