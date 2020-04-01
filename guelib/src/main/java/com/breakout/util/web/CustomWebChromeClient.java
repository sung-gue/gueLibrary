package com.breakout.util.web;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.breakout.util.Log;


/**
 * 기본적인 웹뷰 설정과 ovrride 함수의 로그를 제공한다.<br>
 *
 * @author sung-gue
 * @copyright Copyright 2020. sung-gue All rights reserved.
 * @since 2020-03-30
 */
public class CustomWebChromeClient extends WebChromeClient {
    private final String TAG = "CWCC";
    private CustomWebViewClient.CustomWebViewClientListener _listener;

    public CustomWebChromeClient() {
        this._listener = null;
    }

    public CustomWebChromeClient(CustomWebViewClient.CustomWebViewClientListener listener) {
        this._listener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
//        Log.d(TAG, "onProgressChanged : " + newProgress);
        super.onProgressChanged(view, newProgress);
        if (_listener != null) {
            _listener.onWebViewProgressUpdate(view, newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onCloseWindow(WebView window) {
        Log.d(TAG, "onCloseWindow : " + window);
        super.onCloseWindow(window);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d(TAG, String.format("console log" +
                        "\n    [%s](%s) %s" +
                        "\n    %s",
                consoleMessage.messageLevel(), consoleMessage.lineNumber(), consoleMessage.sourceId(), consoleMessage.message()
        ));
        return true;
        //return super.onConsoleMessage(consoleMessage);
    }

    /*@Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new CustomDialog(_activity)
                .setContents(null, message)
                .setCancel(false)
                .setOkBt("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                .show();
        return true;
        //return super.onJsAlert(view, url, message, result);
    }*/

    /*@Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new CustomDialog(_activity)
                .setContents(null, message)
                .setCancel(false)
                .setOkBt("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                .setCancelBt("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                        result.cancel();
                    }
                })
                .show();
        return true;
        //return super.onJsConfirm(view, url, message, result);
    }*/


    /*
        For Android Version < 3.0
        http://gogorchg.tistory.com/entry/Android-WebView-File-Upload
     */
    /*public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        Log.d(TAG, "WebChromeClient : openFileChooser 1");
        //System.out.println("WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
        mUploadMessage = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(TYPE_IMAGE);
        _baseAct.startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
    }*/

    /*
        For 3.0 <= Android Version < 4.1
     */
    /*public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.d(TAG, "WebChromeClient : openFileChooser 2");
        //System.out.println("WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
        openFileChooser(uploadMsg, acceptType, "");
    }*/

    /*
        For 4.1 <= Android Version < 5.0
     */
    /*public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        Log.d(TAG, "WebChromeClient : openFileChooser 3");
        Log.d(getClass().getName(), "openFileChooser : " + acceptType + "/" + capture);
        mUploadMessage = uploadFile;
        imageChooser();
    }*/

    /*
        For Android Version 5.0+
        Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
     */
    /*@Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        Log.d(TAG, "WebChromeClient : onShowFileChooser");
        System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;
        imageChooser();
        return true;
    }*/

    /*private void imageChooser() {
        Log.d(TAG, "WebChromeClient : imageChooser");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(_context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(true, getClass().getName(), "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType(TYPE_IMAGE);

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        _baseAct.startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }*/

    /*
        More info this method can be found at
        http://developer.android.com/training/camera/photobasics.html
     */
    /*private File createImageFile() throws IOException {
        Log.d(TAG, "WebChromeClient : createImageFile");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  //prefix
                ".jpg",     //suffix
                storageDir      //directory
        );
        return imageFile;
    }*/
}