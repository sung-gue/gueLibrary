package com.breakout.util.img;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.breakout.util.Log;
import com.breakout.util.dto.media.ImageDTO;
import com.breakout.util.string.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Image Util<br>
 * api level 10 or 16 에서 테스트 완료
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @since 2012. 10. 15.
 */
public final class ImageUtil {
    private final static String TAG = "ImageUtil";
    private final static String EXCEPTION_FILE_NULL = "file path is null or file is not exist";
    private final static String EXCEPTION_URI_NULL = "uri variable is null";

    /**
     * 변환작업이나 activity의 이동등으로 다음 activity로 전달하고 싶은 Bitmap이 있을때 사용<br>
     * 1회성이므로 사용후 항상 null 처리를 하여준다.
     */
    private static Bitmap _staticBitmap;

    public final static void setStaticBitmap(Bitmap bitmap) {
        _staticBitmap = bitmap;
    }

    public final static Bitmap getStaticBitmap() {
        Bitmap output = _staticBitmap.copy(_staticBitmap.getConfig(), true);
        recycleBitmap(_staticBitmap);
        _staticBitmap = null;
        return output;
    }

    /**
     * 해당 object가 특정 class의 instance인지 check
     * @author gue
     */
    /*public final static boolean classCheck(Object obj, Class<?> instance) {
		return instance.isInstance(obj);
	}*/

    /**
     * @author gue
     */
    private final static boolean isGoodFilePath(String filePath) {
        return filePath != null && new File(filePath).exists();
    }


/* ************************************************************************************************
 * INFO image & gallery
 */
    /**
     * 화면회전등의 이유로 카메라의 EXTRA_OUTPUT값인 uri가 activity안에서 null 처리 되었을 경우를 대비하여 uri 저장.<br>
     * static이지만 값의 유효에 대해 보장을 하지 못하므로 1회성이라 생각하고 한번 사용한후 꼭 null 처리를 해준다.
     *
     * @see #callCamera(Activity, int, Uri)
     */
    public static Uri _ouputUri;

    private final static void startIntent(Activity activity, Intent intent, int requestCode, String chooser) {
        if (StringUtil.nullCheckB(chooser)) {
            activity.startActivityForResult(Intent.createChooser(intent, chooser), requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    private final static void startIntent(Fragment fragment, Intent intent, int requestCode, String chooser) {
        if (StringUtil.nullCheckB(chooser)) {
            fragment.startActivityForResult(Intent.createChooser(intent, chooser), requestCode);
        } else {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 갤러리에서 이미지를 선택한 후 onActivityResult 에서 Uri를 전달받는다.<br>
     * action : Intent.ACTION_GET_CONTENT<br>
     * type : "image/*"
     *
     * @param chooser null이 아닐경우 Intent.createChooser를 생성한다.
     * @author gue
     */
    public final static void callGallery(Activity activity, int requestCode, String chooser) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startIntent(activity, intent, requestCode, chooser);
    }

    /**
     * 갤러리에서 이미지를 선택한 후 onActivityResult 에서 Uri를 전달받는다.<br>
     * action : Intent.ACTION_PICK<br>
     * type : MediaStore.Images.Media.CONTENT_TYPE
     *
     * @author gue
     */
    public final static void callGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//		intent.setType( "image/*" );
//		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startIntent(activity, intent, requestCode, null);
    }

    /**
     * 갤러리에서 이미지를 선택한 후 onActivityResult 에서 Uri를 전달받는다.<br>
     * action : Intent.ACTION_PICK<br>
     * type : MediaStore.Images.Media.CONTENT_TYPE
     *
     * @author gue
     */
    public final static void callGallery(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//		intent.setType( "image/*" );
//		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startIntent(fragment, intent, requestCode, null);
    }

    /**
     * 갤러리에서 이미지를 선택한 후 Crop을 거쳐서 onActivityResult 에서는
     * 입력한 cropUri를 사용하여 이미지에 대한 작업을 이어나간다.<br>
     * {@link #_ouputUri} 에 임시로 입력받은 uri를 저장한다.
     *
     * @param form    int[] {outputX, outputY, aspectX, aspectY}, 사용하지 않으려면 짝으로 엮인값(output, aspect)에 0을 입력
     * @param cropUri crop image save uri
     * @throws Exception imageUri, cropUri, form이 null이거나 form length가 4가 아닐경우
     * @author gue
     */
    public final static void callGalleryCrop(Activity activity, int requestCode, Uri cropUri, int[] form, boolean scale, boolean returnData) throws Exception {
        _ouputUri = cropUri;
        if (cropUri == null || (form != null && form.length != 4)) {
            _ouputUri = null;
            throw new NullPointerException(EXCEPTION_URI_NULL);
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("scale", scale);
        intent.putExtra("return-data", returnData);
        if (form != null) {
            if (form[0] != 0 && form[1] != 0) {
                intent.putExtra("outputX", form[0]);
                intent.putExtra("outputY", form[1]);
            }
            if (form[2] != 0 && form[3] != 0) {
                intent.putExtra("aspectX", form[2]);
                intent.putExtra("aspectY", form[3]);
            }
        }
        Log.d(TAG, String.format("callGalleryCrop : requestCode=%d, cropUri=%s, size=%dx%d, ratio=%d:%d",
                requestCode, cropUri, form[0], form[1], form[2], form[3]));
        startIntent(activity, intent, requestCode, null);
    }

    /**
     * scale : true, return-data : true<br>
     * see {@link #callGalleryCrop(Activity, int, Uri, int[], boolean, boolean)}
     */
    public final static void callGalleryCrop(Activity activity, int requestCode, Uri cropUri, int[] form) throws Exception {
        callGalleryCrop(activity, requestCode, cropUri, form, true, true);
    }

    /**
     * image crop을 호출한다. {@link #_ouputUri} 에 임시로 입력받은 uri를 저장한다.
     *
     * @param originalImageUri 원본 이미지 uri
     * @param form             int[] {outputX, outputY, aspectX, aspectY}, 사용하지 않으려면 짝으로 엮인값(output, aspect)에 0을 입력
     * @param cropUri          crop image save uri
     * @throws Exception imageUri, cropUri, form이 null이거나 form length가 4가 아닐경우
     * @author gue
     */
    public final static void callCrop(Activity activity, int requestCode, Uri originalImageUri, Uri cropUri, int[] form, boolean scale, boolean returnData) throws Exception {
        _ouputUri = cropUri;
        if (originalImageUri == null || cropUri == null || (form != null && form.length != 4)) {
            _ouputUri = null;
            throw new NullPointerException(EXCEPTION_URI_NULL);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(originalImageUri, "image/*");
        intent.putExtra("scale", scale);
        intent.putExtra("return-data", returnData);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        if (form != null) {
            if (form[0] != 0 && form[1] != 0) {
                intent.putExtra("outputX", form[0]);
                intent.putExtra("outputY", form[1]);
            }
            if (form[2] != 0 && form[3] != 0) {
                intent.putExtra("aspectX", form[2]);
                intent.putExtra("aspectY", form[3]);
            }
        }
        Log.d(TAG, String.format("callCrop : requestCode=%d, originalImageUri=%s, cropUri=%s, size=%dx%d, ratio=%d:%d",
                requestCode, originalImageUri, cropUri, form[0], form[1], form[2], form[3]));
        startIntent(activity, intent, requestCode, null);
    }

    /**
     * image crop을 호출한다. {@link #_ouputUri} 에 임시로 입력받은 uri를 저장한다.
     *
     * @param originalImageUri 원본 이미지 uri
     * @param form             int[] {outputX, outputY, aspectX, aspectY}, 사용하지 않으려면 짝으로 엮인값(output, aspect)에 0을 입력
     * @param cropUri          crop image save uri
     * @throws Exception imageUri, cropUri, form이 null이거나 form length가 4가 아닐경우
     * @author gue
     */
    public final static void callCrop(Fragment fragment, int requestCode, Uri originalImageUri, Uri cropUri, int[] form, boolean scale, boolean returnData) throws Exception {
        _ouputUri = cropUri;
        if (originalImageUri == null || cropUri == null || (form != null && form.length != 4)) {
            _ouputUri = null;
            throw new NullPointerException(EXCEPTION_URI_NULL);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(originalImageUri, "image/*");
        intent.putExtra("scale", scale);
        intent.putExtra("return-data", returnData);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        if (form != null) {
            if (form[0] != 0 && form[1] != 0) {
                intent.putExtra("outputX", form[0]);
                intent.putExtra("outputY", form[1]);
            }
            if (form[2] != 0 && form[3] != 0) {
                intent.putExtra("aspectX", form[2]);
                intent.putExtra("aspectY", form[3]);
            }
        }
        Log.d(TAG, String.format("callCrop : requestCode=%d, originalImageUri=%s, cropUri=%s, size=%dx%d, ratio=%d:%d",
                requestCode, originalImageUri, cropUri, form[0], form[1], form[2], form[3]));
        startIntent(fragment, intent, requestCode, null);
    }

    /**
     * scale : true, return-data : true<br>
     *
     * @see #callCrop(Activity, int, Uri, Uri, int[], boolean, boolean)
     */
    public final static void callCrop(Activity activity, int requestCode, Uri originalImageUri, Uri cropUri, int[] form) throws Exception {
        callCrop(activity, requestCode, originalImageUri, cropUri, form, true, true);
    }

    /**
     * scale : true, return-data : true<br>
     *
     * @see #callCrop(Activity, int, Uri, Uri, int[], boolean, boolean)
     */
    public final static void callCrop(Fragment fragment, int requestCode, Uri originalImageUri, Uri cropUri, int[] form) throws Exception {
        callCrop(fragment, requestCode, originalImageUri, cropUri, form, true, true);
    }

    /**
     * 카메라에서 이미지를 촬영한 후 입력받은 uri에 저장한다.<br>
     * {@link #_ouputUri} 에 임시로 입력받은 uri를 저장한다.
     *
     * @throws Exception outputUri null일 경우
     * @author gue
     */
    public final static void callCamera(Activity activity, int requestCode, Uri outputUri) throws Exception {
        _ouputUri = outputUri;
        Log.d(TAG, "callCamera : outputUri= " + outputUri);
        if (outputUri == null) {
            throw new NullPointerException(EXCEPTION_URI_NULL);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 카메라에서 이미지를 촬영한 후 입력받은 uri에 저장한다.<br>
     * {@link #_ouputUri} 에 임시로 입력받은 uri를 저장한다.
     *
     * @throws Exception outputUri null일 경우
     * @author gue
     */
    public final static void callCamera(Fragment fragment, int requestCode, Uri outputUri) throws Exception {
        _ouputUri = outputUri;
        Log.d(TAG, "callCamera : outputUri= " + outputUri);
        if (outputUri == null) {
            throw new NullPointerException(EXCEPTION_URI_NULL);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 이미지의 file path로부터 이미지의 uri return, 내부 media db에 없는 경로라면 null return
     *
     * @author gue
     */
    public final static Uri getImageUri(Context context, String imageFilePath) {
        Uri uri = null;
        if (imageFilePath != null) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media.DATA + "=?",
                    new String[]{imageFilePath},
                    null);
            if (cursor != null) {
                int id = -1;
                if (cursor.moveToNext()) {
                    id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                }
                cursor.close();
                uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            }
        }
        Log.d(TAG, "getImageUri : imageFilePath=" + imageFilePath + ", get uri=" + uri);
        return uri;
    }

    /**
     * 이미지의 Uri로부터 file path return<br>
     * uri의 scheme가 file, content 인경우에 이미지 uri의
     *
     * @author gue
     */
    public final static String getImagePath(Context context, Uri uri) {
        String imageFilePath = null;
        if (uri != null) {
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Cursor cursor = context.getContentResolver().query(uri,
                        new String[]{ImageColumns.DATA},
                        null,
                        null,
                        null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        imageFilePath = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
                    }
                    cursor.close();
                }
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                imageFilePath = uri.getPath();
            }
        }
        Log.d(TAG, "getImagePath : imageUri=" + uri + ", get path=" + imageFilePath);
        return imageFilePath;
    }

    /**
     * {@link ContentResolver#query(Uri, String[], String, String[], String)}<br>
     * 위 함수를 그대로 실행한다.
     *
     * @author gue
     */
    public final static Cursor getCursorOfUri(Context context,
                                              Uri uri,
                                              String[] projection,
                                              String selection,
                                              String[] selectionArgs,
                                              String sortOrder) {
        Cursor cursor = context.getContentResolver().query(uri == null ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return cursor;
    }

    /**
     * cursor안에 있는 모든 컬럼과 값을 log로 출력한다.<br>
     * api level 10 이하 : image columnCount=18<br>
     * api level 14 이상 : image columnCount=20<br>
     *
     * @author gue
     */
    public final static void cursorLog(Cursor cursor) {
        if (cursor != null) {
            String str = "";
            int columnCount = cursor.getColumnCount();
            Log.d(TAG, "total images count : " + cursor.getCount() + " / columnCount=" + columnCount);
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    str = cursor.getPosition() + "-" + i + ". " + cursor.getColumnName(i);

                    // android api level 11 이상
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                str += "(blob) = " + cursor.getBlob(i);
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                str += "(float) = " + cursor.getFloat(i);
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                str += "(int) = " + cursor.getInt(i);
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                str += " = " + null;
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                str += "(string) = " + cursor.getString(i);
                                break;
                        }
                    }
                    // android api level 11 미만
                    else {
                        try {
                            str += "(string) = " + cursor.getString(i);
                        } catch (Exception e1) {
                            try {
                                str += "(int) = " + cursor.getInt(i);
                            } catch (Exception e2) {
                                try {
                                    str += "(float) = " + cursor.getFloat(i);
                                } catch (Exception e3) {
                                    try {
                                        str += "(blob) = " + cursor.getBlob(i);
                                    } catch (Exception e4) {
                                        str += " = " + null;
                                    }
                                }
                            }
                        }
                    }
                    Log.d(TAG, str);
                }
            }
            cursor.moveToPosition(-1);
        }
    }

    /**
     * 폴더이름으로 정렬하여 ImageDTO의 list를 반환<br>
     * bucket_id, bucket_display_name, bucket_delegate_image_id, bucket_delegate_image_data, bucket_count
     *
     * @author gue
     * @since 2012. 11. 20.
     */
    public final static ArrayList<ImageDTO> getImageFolderList(Context context) {
        ArrayList<ImageDTO> list = new ArrayList<ImageDTO>();
        Cursor cursor = context.getContentResolver().
                query(Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build(),
                        new String[]{ImageColumns.BUCKET_ID, ImageColumns.BUCKET_DISPLAY_NAME},
                        null,
                        null,
                        "UPPER(" + ImageColumns.BUCKET_DISPLAY_NAME + ") ASC");

        Cursor temp = null;
        if (cursor != null) {
            ImageDTO dto;
            for (; cursor.moveToNext(); list.add(dto)) {
                dto = new ImageDTO();
                dto.bucket_id = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_ID));
                dto.bucket_display_name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));

                temp = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{ImageColumns._ID, ImageColumns.DATA},
                        ImageColumns.BUCKET_ID + "=?",
                        new String[]{dto.bucket_id},
                        ImageColumns.DATE_TAKEN + " DESC");
                if (temp != null) {
                    dto.bucket_count = temp.getCount();
                    if (temp.moveToNext()) {
                        dto.bucket_delegate_image_id = temp.getString(temp.getColumnIndex(ImageColumns._ID));
                        dto.bucket_delegate_image_data = temp.getString(temp.getColumnIndex(ImageColumns.DATA));
                    }
                    temp.close();
                }
				
				/*temp = context.getContentResolver().query(	Images.Media.EXTERNAL_CONTENT_URI, 
															new String[]{ImageColumns._ID},
															ImageColumns.BUCKET_ID + "=?", 
															new String[]{dto.bucket_id}, 
															null);
				if (temp != null) {
					dto.bucket_count = temp.getCount();
					temp.close();
				}*/
            }
            cursor.close();
        }
        list.trimToSize();
        return list;
    }

    /**
     * {@link ImageUtil#getImageList(Context, String, String[], ReturnImageDTO, boolean)} 에서
     * return시에 ImageDTO를 상속받은 클래스를 받기 위한 interface<br>
     * 부모로 생성된 인스턴스는 자식에게 형변환하여 대입될수 없기때문에 해당 interface 필요함
     * <pre>
     * ex)
     * class MediaItemDTO extends ImageDTO {
     * }
     *
     * class Ex implements ReturnImageDTO {
     * 	public ImageDTO getExtendsInstance() {
     * 		return new MediaItemDTO();
     *    }
     * }
     * </pre>
     *
     * @author gue
     * @since 2012. 11. 20.
     */
    public interface ReturnImageDTO {
        /**
         * {@link ImageDTO}를 상속받은 class의 new instance를 반환<br>
         *
         * @author gue
         */
        public ImageDTO getExtendsInstance();
    }

    /**
     * desc 값에 따라 정렬하여 {@link ReturnImageDTO}로부터 입력받은 instance type의 array list를 반환<br>
     *
     * @param context    application context
     * @param bucket_id  null일 경우 전체 이미지에 대한 arraylist return
     * @param projection ImageDTO에 넣을 칼럼 array, null일경우 _id,_data,bucket_id 값이 들어간다.
     * @param returnDTO  ImageDTO를 상속받은 new instance를 받기위한 interface, null일경우 ImageDTO list를 반환
     * @param desc       true일 경우 날짜순 역정렬, false일 경우 날짜순 정렬
     * @author gue
     * @since 2012. 11. 20.
     */
    public final static ArrayList<? extends ImageDTO> getImageList(Context context, String bucket_id, String[] projection, ReturnImageDTO returnDTO, boolean desc) {
        ArrayList<ImageDTO> list = null;

        Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
                projection != null ? projection : null,
                bucket_id != null ? ImageColumns.BUCKET_ID + "=?" : null,
                bucket_id != null ? new String[]{bucket_id} : null,
                ImageColumns.DATE_TAKEN + (desc ? " DESC" : " ASC"));
        if (cursor != null) {
            list = new ArrayList<ImageDTO>();
            ImageDTO dto = null;
            for (; cursor.moveToNext(); list.add(dto)) {
                if (returnDTO != null) dto = returnDTO.getExtendsInstance();
                else dto = new ImageDTO();
                for (String column : projection != null ? projection : ImageDTO.IMAGE_COLUMN_LIST) {
                    getValue(dto, cursor, column);
                }
            }
            cursor.close();
        }
        list.trimToSize();
        return list;
    }

    private final static ImageDTO getValue(ImageDTO dto, Cursor c, String columnName) {
        int index = c.getColumnIndex(columnName);
        if (index > -1) {
            if (ImageColumns._ID.equals(columnName)) dto._id = c.getString(index);
            else if (ImageColumns.DATA.equals(columnName)) dto._data = c.getString(index);
            else if (ImageColumns.SIZE.equals(columnName)) dto._size = c.getString(index);
            else if (ImageColumns.DISPLAY_NAME.equals(columnName))
                dto._display_name = c.getString(index);
            else if (ImageColumns.MIME_TYPE.equals(columnName)) dto.mime_type = c.getString(index);
            else if (ImageColumns.TITLE.equals(columnName)) dto.title = c.getString(index);
            else if (ImageColumns.DATE_ADDED.equals(columnName))
                dto.date_added = c.getString(index);
            else if (ImageColumns.DATE_MODIFIED.equals(columnName))
                dto.date_modified = c.getString(index);
            else if (ImageColumns.WIDTH.equals(columnName)) dto.width = c.getString(index);
            else if (ImageColumns.HEIGHT.equals(columnName)) dto.height = c.getString(index);
            else if (ImageColumns.DESCRIPTION.equals(columnName))
                dto.description = c.getString(index);
            else if (ImageColumns.PICASA_ID.equals(columnName)) dto.picasa_id = c.getString(index);
            else if (ImageColumns.IS_PRIVATE.equals(columnName))
                dto.is_private = c.getString(index);
            else if (ImageColumns.LATITUDE.equals(columnName)) dto.latitude = c.getString(index);
            else if (ImageColumns.LONGITUDE.equals(columnName)) dto.longitude = c.getString(index);
            else if (ImageColumns.DATE_TAKEN.equals(columnName)) dto.datetaken = c.getString(index);
            else if (ImageColumns.ORIENTATION.equals(columnName))
                dto.orientation = c.getString(index);
            else if (ImageColumns.MINI_THUMB_MAGIC.equals(columnName))
                dto.mini_thumb_magic = c.getString(index);
            else if (ImageColumns.BUCKET_ID.equals(columnName)) dto.bucket_id = c.getString(index);
            else if (ImageColumns.BUCKET_DISPLAY_NAME.equals(columnName))
                dto.bucket_display_name = c.getString(index);
        }
        return dto;
    }
	
	
/* ************************************************************************************************
 * INFO get bitmap size
 */

    /**
     * get bitmap's size
     *
     * @param imageFilePath
     * @return int[] {width, height}
     * @author gue
     */
    public final static int[] getBitmapOfSize(String imageFilePath) throws Exception {
        BitmapFactory.Options options = makeBitmapOptionsUndecode();
        BitmapFactory.decodeFile(imageFilePath, options);
        int[] rect = null;
        int degree = getExifOrientation(imageFilePath);
        if (degree == 90 || degree == 270) {
            rect = new int[]{options.outHeight, options.outWidth};
        } else {
            rect = new int[]{options.outWidth, options.outHeight};
        }
        Log.d(TAG, String.format("getBitmapOfSize : path=%s, width=%d, height=%d", imageFilePath, rect[0], rect[1]));
        return rect;
    }

    /**
     * get bitmap's height
     *
     * @author gue
     */
    public final static int getBitmapOfHeight(String imageFilePath) throws Exception {
        return getBitmapOfSize(imageFilePath)[0];
    }

    /**
     * get bitmap's width
     *
     * @author gue
     */
    public final static int getBitmapOfWidth(String imageFilePath) throws Exception {
        return getBitmapOfSize(imageFilePath)[1];
    }
	

/* ************************************************************************************************
 * INFO bitmap recycle
 */

    /**
     * bitmap이 존재한다면 해당 bitmap을 recycle 하고 값으로 null을 대입한다.
     *
     * @author gue
     */
    public final static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            Log.d(TAG, "recycleBitmap [" + bitmap + "]- " + bitmap.isRecycled());
        }
        bitmap = null;
    }

    /**
     * imageView 안의 bitmap을 해제한다.
     *
     * @author gue
     */
    public final static void recycleBitmapInImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            recycleBitmap(bitmap);
            bitmap = null;
        }
        imageView.setImageBitmap(null);
    }

	
/* ************************************************************************************************
 * INFO make BitmapFactory.Options
 */

    /**
     * make BitmapFactory.Options
     *
     * @param config Bitmap.Config
     * @author gue
     */
    public final static BitmapFactory.Options makeBitmapOptions(Config config, boolean inJustDecodeBounds, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = inJustDecodeBounds;
        if (!inJustDecodeBounds) {
            // 참고 : 이미지의 경우 투명 이미지는 ARGB_8888, 불투명 이미지는 RGB_565로 충분하다.
//			options.inPreferredConfig = Config.ARGB_4444;	// 알파값이 있고 표현색 가장 적음
//			options.inPreferredConfig = Config.ALPHA_8;
//			options.inPreferredConfig = Config.ARGB_8888; 	// 32bit
//			options.inPreferredConfig = Config.RGB_565; 	// 16bit, 알파값 없음
            options.inPreferredConfig = config;
            options.inSampleSize = inSampleSize;
            options.inPurgeable = true;        // system이 원할때 memory 반환, 하지만 checking이 되지 않는다면 추후 제거
            options.inDither = false;            // image에 적용되는 dithering을 사용하지 않음으로 image 변환의 성능에 도움을 준다.
//			options.inScaled = true; 			// image를 device의 dpi에 구애받지 않고 원본 size로 load한다.
        }
        return options;
    }

    /**
     * make BitmapFactory.Options use inJustDecodeBounds true
     *
     * @author gue
     */
    public final static BitmapFactory.Options makeBitmapOptionsUndecode() {
        return makeBitmapOptions(null, true, 0);
    }
	
	
/* ************************************************************************************************
 * INFO get bitmap of image file
 */

    /**
     * uri로 cursor를 통하여 이미지의 id값을 알아낸 후 bitmap의 thumbnail을 얻어낸다.<br>
     * 단, sdk v14 미만은 해당 uri로 thumbnamil bitmap이 자동으로 생성되지 않기 때문에 해당 함수로  bitmao을 반환 받을 수 없다..<br>
     * 하지만, sdk v14 미만의 device에서 기본 카메라로 생성한 이미지라면 해당 썸네일은 생성되어 있기 때문에 bitmap을 반환 받을수 있다.
     *
     * @author gue
     */
    public final static Bitmap getBitmapThumb(Context context, Uri uri) throws OutOfMemoryError, Exception {
        Bitmap bitmap = null;

        if (uri != null) {
            int orientation = -1;
            long imageId = -1;
            String imageFilePath = null;
            Cursor c = null;
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                c = context.getContentResolver().query(uri,
                        new String[]{MediaStore.MediaColumns._ID,
                                MediaStore.Images.Media.ORIENTATION,
                                MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);

            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.MediaColumns._ID,
                                MediaStore.Images.Media.ORIENTATION,
                                MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.DATA + "=?",
                        new String[]{uri.getPath()},
                        null);
            }
            if (c != null) {
                while (c.moveToNext()) {
                    imageId = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID));
                    orientation = c.getInt(c.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                    imageFilePath = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.d(TAG, String.format("getBitmapThumb db info : id=%d, orientation=%d, path=%s", imageId, orientation, imageFilePath));
                }
                c.close();
            }
            if (orientation < 0) {
                orientation = getExifOrientation(imageFilePath);
                Log.d(TAG, String.format("getBitmapThumb exif info : id=%d, orientation=%d, path=%s", imageId, orientation, imageFilePath));
            }

            if (imageId > -1) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                        imageId,
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        null);
                bitmap = ImageAlter.getRotateBitmap(bitmap, orientation);
            }
        }

        return bitmap;
    }

    /**
     * bitmap decode from image file
     *
     * @param imageFilePath
     * @param inSampleSize  BitmapFactory.Options.inSampleSize
     * @param config        Bitmap.Config
     * @author gue
     */
    public final static Bitmap getBitmap(String imageFilePath, int inSampleSize, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            BitmapFactory.Options options = makeBitmapOptions(config, false, inSampleSize);
            Bitmap bitmap = ImageAlter.getRotateBitmap(BitmapFactory.decodeFile(imageFilePath, options), getExifOrientation(imageFilePath));
            Log.d(TAG, String.format("getBitmap : path=%s, inSampleSize=%d, size=%dx%d", imageFilePath, inSampleSize, bitmap.getWidth(), bitmap.getHeight()));
            return bitmap;
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmap(String, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmap(String imageFilePath, int inSampleSize) throws OutOfMemoryError, Exception {
        return getBitmap(imageFilePath, inSampleSize, Config.RGB_565);
    }

    /**
     * bitmap decode from image file : use inSampleSize = 1<br>
     * see {@link #getBitmap(String, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmap(String imageFilePath, Config config) throws OutOfMemoryError, Exception {
        return getBitmap(imageFilePath, 1, config);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565, inSampleSize = 1<br>
     * see {@link #getBitmap(String, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmap(String imageFilePath) throws OutOfMemoryError, Exception {
        return getBitmap(imageFilePath, 1, Config.RGB_565);
    }

    /**
     * 실제 이미지의 크기와 주어진 width, height를 비교하여
     * bigRatio가 true이면 scale값이 큰쪽으로 inSampleSize를 계산하고,
     * bigRatio가 false면 scale값이 작은쪽으로 inSampleSize를 계산하여 반환한다.
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantWidth     원하는 이미지의 가로 크기
     * @param wantHeight    원하는 이미지의 세로 크기
     * @param bigRatio      true이면 scale값이 큰값
     * @author gue
     */
    public final static int getInSampleSizeOfRatio(String imageFilePath, int wantWidth, int wantHeight, boolean bigRatio) throws Exception {
        int[] rect = getBitmapOfSize(imageFilePath);
        return getInSampleSizeOfRatio(rect[0], rect[1], wantWidth, wantHeight, bigRatio);
    }

    /**
     * 실제 이미지의 크기와 주어진 width, height를 비교하여
     * bigRatio가 true이면 scale값이 큰쪽으로 inSampleSize를 계산하고,
     * bigRatio가 false면 scale값이 작은쪽으로 inSampleSize를 계산하여 반환한다.
     *
     * @param orgWidth   실제 이미지의 가로 크기
     * @param orgHeight  실제 이미지의 세로 크기
     * @param wantWidth  원하는 이미지의 가로 크기
     * @param wantHeight 원하는 이미지의 세로 크기
     * @param bigRatio   true이면 scale값이 큰값
     * @author gue
     */
    public final static int getInSampleSizeOfRatio(int orgWidth, int orgHeight, int wantWidth, int wantHeight, boolean bigRatio) throws Exception {
        double widthScale = (orgWidth * 1.0) / (wantWidth * 1.0);
        double heightScale = (orgHeight * 1.0) / (wantHeight * 1.0);
        double scale;
        if (bigRatio) scale = widthScale > heightScale ? widthScale : heightScale;
        else scale = widthScale < heightScale ? widthScale : heightScale;
        return scale > (int) scale ? (int) scale + 1 : (int) scale;
    }

    /**
     * bitmap decode from image file <br>
     * inSampleSize를 주어진 width와 height를 사용하여 적정값을 구한다.<br>
     * 실제 이미지의 크기와 주어진 width, height를 비교하여 scale값이 작은쪽으로 inSampleSize를 설정하여 decode한다.
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantWidth     원하는 이미지의 가로 크기
     * @param wantHeight    원하는 이미지의 세로 크기
     * @param config        Bitmap.Config
     * @return resize bitmap
     * @author gue
     */
    public final static Bitmap getBitmapTinyRatio(String imageFilePath, int wantWidth, int wantHeight, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            int inSampleSize = getInSampleSizeOfRatio(imageFilePath, wantWidth, wantHeight, false);

            Log.d(TAG, String.format("getBitmapTinyRatio : path=%s, inSampleSize=%d, wantWidth=%d, wantHeight=%d", imageFilePath, inSampleSize, wantWidth, wantHeight));
            return getBitmap(imageFilePath, inSampleSize, config);
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmapTinyRatio(String, int, int, Config)}
     */
    public final static Bitmap getBitmapTinyRatio(String imageFilePath, int wantWidth, int wantHeight) throws OutOfMemoryError, Exception {
        return getBitmapTinyRatio(imageFilePath, wantWidth, wantHeight, Config.RGB_565);
    }


    /**
     * bitmap decode from image file <br>
     * inSampleSize를 주어진 width와 height를 사용하여 적정값을 구한다.<br>
     * 실제 이미지의 크기와 주어진 width, height를 비교하여 scale값이 큰쪽으로 inSampleSize를 설정하여 decode한다.
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantWidth     원하는 이미지의 가로 크기
     * @param wantHeight    원하는 이미지의 세로 크기
     * @param config        Bitmap.Config
     * @return resize bitmap
     * @author gue
     */
    public final static Bitmap getBitmapBigRatio(String imageFilePath, int wantWidth, int wantHeight, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            int inSampleSize = getInSampleSizeOfRatio(imageFilePath, wantWidth, wantHeight, true);

            Log.d(TAG, String.format("getBitmapBigRatio : path=%s, inSampleSize=%d, wantWidth=%d, wantHeight=%d", imageFilePath, inSampleSize, wantWidth, wantHeight));
            return getBitmap(imageFilePath, inSampleSize, config);
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmapBigRatio(String, int, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmapBigRatio(String imageFilePath, int wantWidth, int wantHeight) throws OutOfMemoryError, Exception {
        return getBitmapBigRatio(imageFilePath, wantWidth, wantHeight, Config.RGB_565);
    }

    /**
     * bitmap decode from image file <br>
     * 주어진 width에 맞추어 scale을 적용한 bitmap을 만든다. 단 확대는 하지 않는다.<br>
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantWidth     변환을 원하는 가로크기
     * @param config        Bitmap.Config
     * @return resize bitmap
     * @author gue
     */
    public final static Bitmap getBitmapFixWidth(String imageFilePath, int wantWidth, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            int[] rect = getBitmapOfSize(imageFilePath);
            int wantHeight = rect[1];
            int inSampleSize = 1;
            Bitmap output = null;

            if (wantWidth < rect[0]) {
                inSampleSize = rect[0] / wantWidth;
                Bitmap bitmap = getBitmap(imageFilePath, inSampleSize, config);
                wantHeight = wantWidth * rect[1] / rect[0];
                output = Bitmap.createScaledBitmap(bitmap, wantWidth, wantHeight, true);
                if (bitmap != output) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } else {
                output = getBitmap(imageFilePath, inSampleSize, config);
                wantWidth = rect[0];
            }
            Log.d(TAG, String.format("getBitmapFixWidth : path=%s, inSampleSize=%d, wantWidth=%d, calcHeight=%d", imageFilePath, inSampleSize, wantWidth, wantHeight));
            return output;
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmapFixWidth(String, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmapFixWidth(String imageFilePath, int wantWidth) throws OutOfMemoryError, Exception {
        return getBitmapFixWidth(imageFilePath, wantWidth, Config.RGB_565);
    }

    /**
     * bitmap decode from image file <br>
     * 주어진 height에 맞추어 scale을 적용한 bitmap을 만든다. 단 확대는 하지 않는다.<br>
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantHeight    변환을 원하는 세로크기
     * @param config        Bitmap.Config
     * @return resize bitmap
     * @author gue
     */
    public final static Bitmap getBitmapFixHeight(String imageFilePath, int wantHeight, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            int[] rect = getBitmapOfSize(imageFilePath);
            int wantWidth = rect[0];
            int inSampleSize = 1;
            Bitmap output = null;

            if (wantHeight < rect[1]) {
                inSampleSize = rect[1] / wantHeight;
                Bitmap bitmap = getBitmap(imageFilePath, inSampleSize, config);
                wantWidth = wantHeight * rect[0] / rect[1];
                output = Bitmap.createScaledBitmap(bitmap, wantWidth, wantHeight, true);
                if (bitmap != output) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } else {
                output = getBitmap(imageFilePath, inSampleSize, config);
                wantHeight = rect[1];
            }

            Log.d(TAG, String.format("getBitmapFixHeight : path=%s, inSampleSize=%d, wantWidth=%d, calcHeight=%d",
                    imageFilePath, inSampleSize, wantWidth, wantHeight));
            return output;
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmapFixHeight(String, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmapFixHeight(String imageFilePath, int wantHeight) throws OutOfMemoryError, Exception {
        return getBitmapFixHeight(imageFilePath, wantHeight, Config.RGB_565);
    }

    /**
     * bitmap decode from image file<br>
     * 입력받은 width, height에 맞게 이미지를 resize 한다. 단 확대는 하지 않는다. <br>
     *
     * @param imageFilePath 이미지 파일의 절대경로
     * @param wantWidth     변환을 원하는 가로크기
     * @param wantHeight    변환을 원하는 세로크기
     * @param config        Bitmap.Config
     * @return resize bitmap
     * @author gue
     */
    public final static Bitmap getBitmapResize(String imageFilePath, int wantWidth, int wantHeight, Config config) throws OutOfMemoryError, Exception {
        if (isGoodFilePath(imageFilePath)) {
            Bitmap output = null;
            Bitmap bitmap = getBitmapFixWidth(imageFilePath, wantWidth, config);
            int calcWidth = bitmap.getWidth();
            int calcHeight = bitmap.getHeight();

            if (wantHeight < calcHeight) {
                calcWidth = wantHeight * calcWidth / calcHeight;
                calcHeight = wantHeight;
                output = Bitmap.createScaledBitmap(bitmap, calcWidth, calcHeight, true);
                if (bitmap != output) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } else {
                output = bitmap;
            }

            Log.d(TAG, String.format("getBitmapResize : path=%s, wantWidth=%d, wantHeight=%d, calcWidth=%d, calcHeight=%d",
                    imageFilePath, wantWidth, wantHeight, calcWidth, calcHeight));
            return output;
        } else throw new NullPointerException(EXCEPTION_FILE_NULL);
    }

    /**
     * bitmap decode from image file : use Config.RGB_565<br>
     * see {@link #getBitmapResize(String, int, int, Config)}
     *
     * @author gue
     */
    public final static Bitmap getBitmapResize(String imageFilePath, int wantWidth, int wantHeight) throws OutOfMemoryError, Exception {
        return getBitmapResize(imageFilePath, wantWidth, wantHeight, Config.RGB_565);
    }
	
	
/* ************************************************************************************************
 * INFO drawable convert Bitmap
 */

    /**
     * drawable -> bitmap
     *
     * @param drawable
     * @author gue
     */
    public final static Bitmap drawableToBitmap(Drawable drawable) throws OutOfMemoryError, Exception {
        Bitmap output = null;

        if (drawable instanceof BitmapDrawable) {
            output = ((BitmapDrawable) drawable).getBitmap().copy(Config.ARGB_8888, true);
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }

        return output;
    }
	
	
/* ************************************************************************************************
 * INFO size convert
 */

    /**
     * image size converter<br>
     * see {@link #sizeConverter(Bitmap, int, int, boolean)}
     *
     * @author gue
     */
    public final static Bitmap sizeConvert(Drawable drawable, int wantWidth, int wantHeight, boolean recycle) throws OutOfMemoryError, Exception {
        Log.d(TAG, "drawable size : " + drawable.getIntrinsicWidth() + " / " + drawable.getMinimumHeight());
        return sizeConvert(drawableToBitmap(drawable), wantWidth, wantHeight, recycle);
    }

    /**
     * image size converter
     *
     * @param wantWidth  변환을 원하는 가로 크기
     * @param wantHeight 변환을 원하는 세로 크기
     * @param recycle    true : 주어진 bitmap을 recycle 처리한다.
     * @author gue
     */
    public final static Bitmap sizeConvert(Bitmap bitmap, int wantWidth, int wantHeight, boolean recycle) throws OutOfMemoryError, Exception {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, String.format("1. original bitmap size - width=%d, height=%d ", width, height));
        Bitmap output = null;

        if (width != wantWidth || height != wantHeight) {
            output = Bitmap.createScaledBitmap(bitmap, wantWidth, wantHeight, true);
            width = output.getWidth();
            height = output.getHeight();
        } else output = bitmap.copy(Config.ARGB_8888, true);

        Log.d(TAG, String.format("2. convert bitmap size - width=%d, height=%d ", width, height));

        if (recycle && output != bitmap) bitmap.recycle();

        return output;
    }

    /**
     * image size converter<br>
     * see {@link #sizeConvertFixWidth(Bitmap, int, boolean)}
     *
     * @author gue
     */
    public final static Bitmap sizeConvertFixWidth(Drawable drawable, int wantWidth, boolean recycle) throws OutOfMemoryError, Exception {
        Log.d(TAG, "drawable size : " + drawable.getIntrinsicWidth() + " / " + drawable.getMinimumHeight());
        return sizeConvertFixWidth(drawableToBitmap(drawable), wantWidth, recycle);
    }

    /**
     * image size converter : 가로 크기를 기준으로 bitmap의 크기를 조정한다.<br>
     *
     * @param wantWidth 변환을 원하는 가로 크기
     * @param recycle   true : 주어진 bitmap을 recycle 처리한다.
     * @author gue
     */
    public final static Bitmap sizeConvertFixWidth(Bitmap bitmap, int wantWidth, boolean recycle) throws OutOfMemoryError, Exception {
        double width = bitmap.getWidth();
        double height = bitmap.getHeight();
        Log.d(TAG, String.format("1. original bitmap size - width=%d, height=%d ", width, height));
        Bitmap output = null;

        if (width > wantWidth) {
            output = Bitmap.createScaledBitmap(bitmap, wantWidth, (int) (height * wantWidth / width), true);
            width = output.getWidth();
            height = output.getHeight();
        } else output = bitmap.copy(Config.ARGB_8888, true);

        Log.d(TAG, String.format("2. convert bitmap size - width=%d, height=%d ", width, height));
        if (recycle && output != bitmap) bitmap.recycle();
        return output;
    }

    /**
     * image size converter<br>
     * see {@link #sizeConvertFixHeight(Bitmap, int, boolean)}
     *
     * @author gue
     */
    public final static Bitmap sizeConvertFixHeight(Drawable drawable, int wantHeight, boolean recycle) throws OutOfMemoryError, Exception {
        Log.d(TAG, "drawable size : " + drawable.getIntrinsicWidth() + " / " + drawable.getMinimumHeight());
        return sizeConvertFixHeight(drawableToBitmap(drawable), wantHeight, recycle);
    }

    /**
     * image size converter : 세로 크기를 기준으로 bitmap의 크기를 조정한다.<br>
     *
     * @param wantHeight 변환을 원하는 세로 크기
     * @param recycle    true : 주어진 bitmap을 recycle 처리한다.
     * @author gue
     */
    public final static Bitmap sizeConvertFixHeight(Bitmap bitmap, int wantHeight, boolean recycle) throws OutOfMemoryError, Exception {
        double width = bitmap.getWidth();
        double height = bitmap.getHeight();
        Log.d(TAG, String.format("1. original bitmap size - width=%d, height=%d ", width, height));
        Bitmap output = null;

        if (height > wantHeight) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * wantHeight / height), wantHeight, true);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        } else output = bitmap.copy(Config.ARGB_8888, true);

        Log.d(TAG, String.format("2. convert bitmap size - width=%d, height=%d ", width, height));
        if (recycle && output != bitmap) bitmap.recycle();
        return output;
    }
	

/* ************************************************************************************************
 * INFO image info
 */

    /**
     * 이미지의 orientation값을 구하여 각도로 변환
     * return 회전해야할 degree값
     */
    public static int getExifOrientation(String imageFilePath) {
        int orientation = 0;
        int degree = 0;
        if (isGoodFilePath(imageFilePath)) {
            try {
                ExifInterface exif = new ExifInterface(imageFilePath);
                if (exif != null) {
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                }
                switch (orientation) {
					/*case ExifInterface.ORIENTATION_NORMAL:
					degree = 0;
					break;*/
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (degree != 0)
                Log.d(TAG, String.format("getExifOrientation : %d / degree: %d / path: %s ", orientation, degree, imageFilePath));
        }
        return degree;
    }

    /* ************************************************************************************************
     * INFO view bitmap
     */
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(canvas);
        return bitmap;
    }

    public void screenshot(View view) throws Exception {
        view.setDrawingCacheEnabled(true);
        Bitmap scrreenshot = view.getDrawingCache();
        String filename = "screenshot.png";
        try {
            File f = new File(Environment.getExternalStorageDirectory(), filename);
            f.createNewFile();
            OutputStream outStream = new FileOutputStream(f);
            scrreenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(false);
    }
	
/* ************************************************************************************************
 * INFO not arrangement
 */
    /**
     * 이미지 타켓 사이즈에 맞는 샘플 이미지 출력
     */
	/*public static Bitmap readImageFitWidth(String imagePath, int targetWidth, Bitmap.Config bmConfig) throws OutOfMemoryError, Exception {
		// Get the dimensions of the bitmap
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		int photoWidth = options.outWidth;
		int photoHeight = options.outHeight;

		int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / 1);

		float hScaleTemp = ((targetWidth * 1.0f) * (photoHeight * 1.0f)) / (photoWidth * 1.0f);

		int hScale = (int) hScaleTemp;
		int wScale = (int) targetWidth;

		options.inSampleSize = scaleFactor;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		Bitmap src = BitmapFactory.decodeFile(imagePath, options);
		Bitmap resized = Bitmap.createScaledBitmap(src, wScale, hScale, true);

		return resized;
	}*/


    /** 이미지 타켓 사이즈에 맞는 샘플 이미지 출력 */
	/*public static Bitmap readImageWithSampling(String imagePath, int targetWidth, int targetHeight, Bitmap.Config bmConfig) {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);

		int photoWidth = bmOptions.outWidth;
		int photoHeight = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inPreferredConfig = bmConfig;
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap orgImage = BitmapFactory.decodeFile(imagePath, bmOptions);
		return orgImage;
	}*/
}
