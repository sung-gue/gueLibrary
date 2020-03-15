package com.breakout.util.media;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.breakout.util.Log;


/**
 * Android Media Util
 *
 * @author sung-gue
 * @version 1.0
 * @copyright Copyright 2012. sung-gue All rights reserved.
 * @since 2012. 10. 15.
 */
@SuppressWarnings("unused")
public final class MediaUtil {
    private final static String TAG = "MediaUtil";
    private final static String EXCEPTION_FILE_NULL = "file path is null or file is not exist";
    private final static String EXCEPTION_URI_NULL = "uri is null";

    /**
     * test
     */
    private void testCallImage() {
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * action : Intent.ACTION_GET_CONTENT
     * type : "image/*"
     */
    public static void callGetImageContent(@NonNull Activity activity, int requestCode, String chooser) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (TextUtils.isEmpty(chooser)) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(Intent.createChooser(intent, chooser), requestCode);
        }
    }

    /**
     * action : Intent.ACTION_PICK
     * type : MediaStore.Images.Media.CONTENT_TYPE
     */
    public static void callGallery(@NonNull Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setType( "image/*" );
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Uri로부터 file 경로 구하기
     *
     * @param uri scheme가 file, content 인경우의 파일 uri
     * @todo Intent.ACTION_GET_CONTENT 를 사용하여 가져온 이미지는 안됌
     */
    public static String getMediaPath(Context context, @NonNull Uri uri) throws Exception {
        String mediaPath = null;
        try {
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Cursor cursor = context.getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA},
                        null,
                        null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    }
                    cursor.close();
                }
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                mediaPath = uri.getPath();
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            Log.d(TAG, "getMediaPath : uri=" + uri + ", path=" + mediaPath);
        }
        return mediaPath;
    }


}
