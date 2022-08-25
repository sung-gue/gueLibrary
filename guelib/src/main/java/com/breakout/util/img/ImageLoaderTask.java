package com.breakout.util.img;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.breakout.util.CodeAction;
import com.breakout.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private String TAG;
    private final ImageLoadCompleteListener imageLoadCompleteListener;
    private final ImageLoader loader;
    private final WeakReference<ImageView> imageViewReference;
    private final String cacheDir;
    private long fileLength;
    private boolean decodeErr;
    String _url;
    /**
     * <li>0 : 정상</li>
     * <li>1 : 90도</li>
     * <li>2 : 180도</li>
     * <li>3 : 270도</li>
     */
    int rotate;
//    long mediaId;
    /**
     * imageView가 thumbnail 형태의 작은 이미지일 경우 주어진 사이즈로 축소하여 decode 한다.
     */
    int thumbWidth = 0;
    /**
     * <li>1~3. url download후 url을 md5로 변환한 후에 {@link #cacheDir}에 image file 저장, file 존재하다면 local decode</li>
     * <li>4. url 대신 file path를 사용하여 1번과 동일한 작업</li>
     * <li>5. 4번 작업에 image rotation 추가</li>
     * <li>6. circle image</li>
     */
    private int form;
    /**
     * radius of the oval used to round the corners
     */
    private int radiusPxToRound;
    /**
     * 3의 크기를 갖는 int배열값 ex) int[] wantImageInfo = new int[]{width, height, inSampleSize};
     * <li>scale을 변경할 경우에는 width,height 값에 0이 아닌값</li>
     * <li>{@link BitmapFactory.Options#inSampleSize}를 사용하려면 원하는 inSampleSize 값을 넣어준다.</li>
     * <li>사용하지 않는 값에는 0을 넣어준다.</li>
     */
    int[] wantImageInfo = new int[]{0, 0, 1};


    public ImageLoaderTask(ImageView imageView, String cacheDir, ImageLoadCompleteListener imageLoadCompleteListener, int form) {
        imageViewReference = new WeakReference<>(imageView);
        this.imageLoadCompleteListener = imageLoadCompleteListener;
        this.loader = (ImageLoader) imageLoadCompleteListener;
        this.cacheDir = cacheDir;
        this.form = form;
    }

    // INFO gue/2014. 9. 16. : [임시코드] 이미지 라운드 처리 관련
    public ImageLoaderTask(ImageView imageView, String cacheDir, ImageLoadCompleteListener imageLoadCompleteListener, int form, int radiusPxToRound) {
        imageViewReference = new WeakReference<>(imageView);
        this.imageLoadCompleteListener = imageLoadCompleteListener;
        this.loader = (ImageLoader) imageLoadCompleteListener;
        this.cacheDir = cacheDir;
        this.form = form;
        this.radiusPxToRound = radiusPxToRound;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public void setWantImageInfo(int[] wantImageInfo) {
        if (wantImageInfo != null) {
            this.wantImageInfo = wantImageInfo;
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        TAG = Thread.currentThread().getName();
        _url = params[0];

        Bitmap bitmap = null;
        File file = null;
        try {
            switch (form) {
                case 1:
                case 2:
                case 3:
                case 6:
                    file = new File(cacheDir + CodeAction.EncodeMD5(_url));
                    if (file.exists()) {
                        fileLength = file.length();
                        LoaderTaskQueue.getInstance().getQueue(TAG, String.format("[%s byte]-%s ", fileLength, _url));
                        bitmap = loadImageFromLocalCache(file);
                    } else {
                        LoaderTaskQueue.getInstance().getQueue(TAG, String.format("[download]-%s ", _url));
                        bitmap = downloadBitmap(_url);
                    }
                    break;
                case 4:
                case 5:
                    file = new File(_url);
                    if (file.exists()) fileLength = file.length();
                    LoaderTaskQueue.getInstance().getQueue(TAG, String.format("[%s byte]-%s ", fileLength, _url));
                    bitmap = loadImageFromLocalFile();
                    break;
            }
        } catch (OutOfMemoryError e) {
            decodeErr = true;
            Log.w(TAG, String.format("[OutOfMemoryError:%s] decode image - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
        } catch (Exception e) {
            decodeErr = true;
            Log.w(TAG, String.format("[Exception:%s] decode image - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
        }
        LoaderTaskQueue.getInstance().removeQueue(TAG, String.format("[%s byte]-%s ", fileLength, _url));
        return bitmap;
    }

    @Override
    protected void onCancelled() {
        Log.e(TAG, String.format("(%s) onCancelled | url= %s", TAG, _url));
        LoaderTaskQueue.getInstance().removeQueue(TAG, String.format("onCancelled [%s byte]-%s", fileLength, _url));
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap.recycle();
            bitmap = null;
        }
        
        /*switch (form) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                break;
        }*/
        if (imageViewReference != null && imageLoadCompleteListener != null && !isCancelled()) {
            imageLoadCompleteListener.onCompleted(_url, imageViewReference.get(), bitmap, decodeErr, new int[]{form, radiusPxToRound});
//            imageLoadCompleteListener.onCompleted(_url, imageViewReference.get(), bitmap, (bitmap == null ? true : false), form );
        } else {
            loader.removeAsyncTask(_url);
        }
    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * download image
     */
    private Bitmap downloadBitmap(String imageUrl) throws OutOfMemoryError {
        final int IO_BUFFER_SIZE = 2 * 1024;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            if (url.getProtocol() != null && url.getProtocol().equalsIgnoreCase("https")) {
//                trustAllHosts();
//                ((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                });
//            }
            con.setDoInput(true);
            con.connect();
            int statusCode = con.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                String location = con.getHeaderField("Location");
                if (location.startsWith("/")) {
                    location = url.getProtocol() + "://" + url.getHost() + location;
                }
                con.disconnect();
                return downloadBitmap(location);
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + _url);
                return null;
            }

            InputStream is = con.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                // image save
                //if (loader.getCacheDir() != null) throw new IOException("파일 쓰기에러 강제 실현");
                final File file = new File(cacheDir + CodeAction.EncodeMD5(_url));
                final FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                fileLength = file.length();
                Log.d(TAG, String.format("downloadBitmap | length[%s byte], -%s ", fileLength, _url));
            }
            if (false && is != null) {
                FlushedInputStream flush = null;
                byte[] result = null;
                try {
                    flush = new FlushedInputStream(is);

                    // inputstream -> byte[]
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int size = 0;
                    byte[] buff = new byte[IO_BUFFER_SIZE];
                    while ((size = flush.read(buff)) != -1) os.write(buff, 0, size);
                    result = os.toByteArray();
                    os.close();

                    // image save
                    //if (loader.getCacheDir() != null) throw new IOException("파일 쓰기에러 강제 실현");
                    final File file = new File(cacheDir + CodeAction.EncodeMD5(_url));
                    final FileOutputStream fos = new FileOutputStream(file);
                    fos.close();


                    fileLength = result.length;
                    Log.d(TAG, String.format("downloadBitmap | length[%s byte], -%s ", fileLength, _url));

                /*
                    sdcard의 용량 부족이나 파일 저장오류로 인해 file size가
                    download size와 같지 않을때에는 downstream을 그대로 decode한다.
                 */
                    long saveFileLenth = file.length();
                    try {
                        if (fileLength == saveFileLenth) {
                            return loadImageFromLocalCache(file);
                        } else throw new Exception("file save error");
                    } catch (Exception e) {
                        Log.e(TAG, String.format("[Exception:%s] - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
                        //throw new IOException("file save error");
                        return BitmapFactory.decodeStream(new ByteArrayInputStream(result));
                    }
                } catch (Exception e) {
                    loader.sdcardErrToast();
                    Log.e(TAG, String.format("[Exception:%s] - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
                    //return BitmapFactory.decodeStream(new ByteArrayInputStream(result));
                } finally {
                    if (flush != null) flush.close();
                }
            }
            con.disconnect();
        } catch (MalformedURLException e) {
            Log.w(TAG, "Incorrect URL: " + _url, e);
        } catch (IOException e) {
            Log.w(TAG, "I/O error while retrieving bitmap from " + _url, e);
        } catch (Exception e) {
            Log.w(TAG, "Error while retrieving bitmap from " + _url, e);
        }
        return null;
    }

    /* ------------------------------------------------------------
        DESC: decode local image
     */
    private final Bitmap loadImageFromLocalCache(File file) throws OutOfMemoryError {
        try {
            synchronized (this) {
                Bitmap bitmap = null;
                // imageView가 thumbnail 형태의 작은 이미지일 경우 주어진 사이즈로 축소하여 decode 한다.
                if (thumbWidth != 0) {
                    Log.d(TAG, String.format("decode image cache thumbnail - [%s byte]-%s ", fileLength, _url));
                    bitmap = ImageUtil.getBitmapBigRatio(file.getAbsolutePath(), thumbWidth, thumbWidth, Config.ARGB_8888);
                } else if (wantImageInfo[0] != 0 && wantImageInfo[1] != 0) {
                    Log.d(TAG, String.format("decode image cache device width - [%s byte]-%s ", fileLength, _url));
                    bitmap = ImageUtil.getBitmapBigRatio(file.getAbsolutePath(), wantImageInfo[0], wantImageInfo[1], Config.ARGB_8888);
                } else {
                    Log.d(TAG, String.format("decode image cache original - [%s byte]-%s ", fileLength, _url));
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                }
                return bitmap;
            }
        } catch (Exception e) {
            Log.w(TAG, String.format("[Exception:%s] decode image cache - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
        }
        return null;
    }

    private Bitmap loadImageFromLocalFile() throws OutOfMemoryError {
        try {
            synchronized (this) {
                Bitmap bitmap = null;
                // BitmapFactory.Options.inSampleSize 사용
                if (wantImageInfo[2] > 1) {
                    Log.d(TAG, String.format("decode image inSampleSize(%s) - [%s byte]-%s ", wantImageInfo[2], fileLength, _url));
                    bitmap = ImageUtil.getBitmap(_url, wantImageInfo[2], Config.ARGB_8888);
                }
                // scale want size
                else if (wantImageInfo[0] != 0 && wantImageInfo[1] != 0) {

                    Log.d(TAG, String.format("decode image wantsize(%s/%s) - [%s byte]-%s ", wantImageInfo[0], wantImageInfo[1], fileLength, _url));
                    bitmap = ImageUtil.getBitmapTinyRatio(_url, wantImageInfo[0], wantImageInfo[1], Config.ARGB_8888);
                }
                // original image decode
                else {
                    Log.d(TAG, String.format("decode image original - [%s byte]-%s ", fileLength, _url));
                    bitmap = ImageUtil.getBitmap(_url, wantImageInfo[2], Config.ARGB_8888);
                }
                return bitmap;
            }
        } catch (Exception e) {
            Log.w(TAG, String.format("[Exception:%s] decode image File - [%s byte]-%s ", e.getMessage(), fileLength, _url), e);
        }
        return null;
    }

}