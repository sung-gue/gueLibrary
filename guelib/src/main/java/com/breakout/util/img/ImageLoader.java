package com.breakout.util.img;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import com.breakout.util.Log;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.res.AnimationSuite;
import com.breakout.util.string.StringUtil;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;


/**
 * 이미지의 url을 받아 멀티 쓰레드를 적용하여 url을 md5로 바꾸어 캐시를 생성하거나 캐시가 있다면 디코딩하여 원하는 imageview에 적용한다.<br>
 * 사용방법<br>
 * <ol>
 * <li>인스턴스를 생성한다. : {@link #getInstance(Context)}</li>
 * <li>다음 함수를 사용하여 이미지의 캐싱 작업을 시작한다. : {@link #download(String, ImageView, Bitmap)}</li>
 * <li></li>
 * </ol>
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>gue/2012.08.11 : device display size를 읽어와서 thumbnail과 일반 imageView에 decode되어 뿌려주는 속도 향상.</li>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2012. 6. 14.
 */
public final class ImageLoader implements ImageLoadCompleteListener {
    private final static String TAG = "ImageLoader";
    /**
     * singleton instance
     */
    private static ImageLoader _this;
    private Context _context;
    /**
     * appliocation cache directory
     */
    private String _cacheDir;
    private String _sdErrStr;
    private boolean _sdErrFlag;
    /**
     * {@link #resetPurgeTimer}
     */
    private final int RESET_PURGE_TIME = 1000 * 60 * 3;
    /**
     * {@link #sHardBitmapCache} size
     */
    private static final int HARD_CACHE_CAPACITY = 150;
    /**
     * Hard cache, with a fixed maximum capacity and a life duration
     */
    private final LinkedHashMap<String, Bitmap> sHardBitmapCache =
            new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY * 4 / 3 + 1, 0.75f, true) {
                private static final long serialVersionUID = 7348866211489221159L;

                @Override
                protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
                    if (size() >= HARD_CACHE_CAPACITY) {
                        // Entries push-out of hard reference cache are transferred to soft reference cache
                        sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                        return true;
                    } else
                        return false;
                }
            };
    /**
     * Soft cache
     */
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
    /**
     * 1~3의 방식은 같은 이미지의 url을 MD5로 변환하여 cache에 저장후 사용하기 때문에 성능상 thumbnail을 필요로 하는 View에서  
     * 캐쉬상에 큰 이미지를 그대로 보여줄 필요가 없기 때문에 2,3의 추가 method를 사용하게 된다. 
     * 이때 {@link ImageLoaderTask}에서 이 값을 사용하여 분기처리를 하여 준다.
     * <ol>    
     *         <li>{@link #download}</li>
     *         <li>{@link #downloadOptimize}</li>
     *         <li>{@link #downloadForThumb}</li>
     *         <li>{@link #downloadLocalPath}</li>
     *         <li></li>
     * </ol>
     * @see {@link ImageLoaderTask#form}
     */
//    private int loaderForm;
    /**
     * 0 : width, 1 : height
     */
    private int[] displaySize;
    /**
     * {@link #asyncTable}
     * RejectedExecutionException : pool=128/128, queue=10/10
     */
    private static final int ASYNCTASK_CAPACITY = 30;
    /**
     * thread가 지정 갯수를 초과하지 않게 방지. 가장 먼저 삽입된 값의 task를 cancel하고 값을 삭제.
     */
    private final static LinkedHashMap<String, ImageLoaderTask> asyncTaskTable =
            new LinkedHashMap<String, ImageLoaderTask>(ASYNCTASK_CAPACITY * 4 / 3 + 1, 0.75f, false) {
                private static final long serialVersionUID = -1801595502367578616L;

                @Override
                protected boolean removeEldestEntry(LinkedHashMap.Entry<String, ImageLoaderTask> eldest) {
                    if (size() >= ASYNCTASK_CAPACITY) {
                        ImageLoaderTask task = eldest.getValue();
                        if (task != null) {
                            task.cancel(true);
                        }
                        Log.i(TAG, String.format("removeEldestEntry | task size=%d, url= %s", size(), eldest.getKey()));
                        return true;
                    }
                    return false;
                }
            };
    /*private final LinkedHashMap<String, WeakReference<ImageLoaderTask>> asyncTaskTable =
                new LinkedHashMap<String, WeakReference<ImageLoaderTask>>(ASYNCTASK_CAPACITY * 4/3 +1, 0.75f, false) {
            private static final long serialVersionUID = -1801595502367578616L;
            @Override
            protected boolean removeEldestEntry(LinkedHashMap.Entry<String, WeakReference<ImageLoaderTask>> eldest) {
                if (size() >= ASYNCTASK_CAPACITY) {
                    ImageLoaderTask task = eldest.getValue().get();
                    if (task != null){
                        task.cancel(true);
                    }
                    Log.i(TAG, String.format("asyncTask cancel [%d] : %s", size(), eldest.getKey()));
                    return true;
                }
                return false;
            }
        };*/


    public ImageLoader(Context context) {
        this._context = context;
        this.displaySize = DeviceUtil.getDisplaySize(_context);
        // get cache dir
        if (_context.getExternalCacheDir() != null) {
            _cacheDir = _context.getExternalCacheDir().getAbsolutePath() + "/";
        } else {
            _cacheDir = _context.getCacheDir().getAbsolutePath() + "/";
        }
        Log.i("ImageLoader Instance create");
    }

    /**
     * context를 받아 device의 해상도와 이미지의 캐쉬폴더로 app의 외장캐쉬폴더의 경로를 설정한다.
     * {@link #setCacheDir(String)}을 사용하여 캐쉬폴더의 위치를 변경 가능하다.
     */
    public static synchronized ImageLoader getInstance(Context context) {
        if (_this == null) _this = new ImageLoader(context);
        return _this;
    }

    /**
     * 사용하기 전에 {@link #getInstance(Context)}로 instance의 초기화가 이루어져 있어야 한다. 아닐경우 null return
     */
    public static ImageLoader getInstance() {
        return _this;
    }

    public static void destroyInstance() {
        if (_this != null) _this.destroy();
    }

    public void destroy() {
        Log.i(TAG, "destroy ImageLoader instance");
        LoaderTaskQueue.destroyInstance();
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
        asyncTaskTable.clear();
        _context = null;
        _this = null;
    }
    

/* ************************************************************************************************
 * INFO public method
 */

    /**
     * 1. image loading : url
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set download image, null일경우 returndnpq
     * @param baseImage bitmap of base image
     */
    public final void download(String url, ImageView imageView, Bitmap baseImage) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 1;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 1-1. image loading : url
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set down image, null일경우 return
     * @param baseImage bitmap of base image
     * @param scroll    true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     */
    public final void download(String url, ImageView imageView, Bitmap baseImage, boolean scroll) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 1;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 2. image loading : url<br>
     * imageView에 쓰이는 Image를 device의 display size를 사용하여 최적화된 size로 decode하여 system의 부하를 줄인다.
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set down image, null일경우 return
     * @param baseImage bitmap of base image
     * @author gue
     */
    public final void downloadOptimize(String url, ImageView imageView, Bitmap baseImage) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 2;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(new int[]{displaySize[0], displaySize[1], 0});
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 2-1. image loading : url<br>
     * imageView에 쓰이는 Image를 device의 display size를 사용하여 최적화된 size로 decode하여 system의 부하를 줄인다.
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set down image, null일경우 return
     * @param baseImage bitmap of base image
     * @param scroll    리스트에서 스크롤 상태값, true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     * @author gue
     */
    public final void downloadOptimize(String url, ImageView imageView, Bitmap baseImage, boolean scroll) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 2;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null && !scroll) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(new int[]{displaySize[0], displaySize[1], 0});
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else if (bitmap == null && scroll) {
            imageView.setImageBitmap(baseImage);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * INFO gue/2014. 9. 16. : [임시코드] 이미지 라운드 처리 관련
     * 2-2. image loading : url<br>
     * imageView에 쓰이는 Image를 device의 display size를 사용하여 최적화된 size로 decode하여 system의 부하를 줄인다. <br/>
     * 추가로 이미지 라운드 처리를 한다.
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set down image, null일경우 return
     * @param baseImage bitmap of base image
     * @param scroll    리스트에서 스크롤 상태값, true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     * @author gue
     */
    public final void downloadOptimizeRoundImg(String url, ImageView imageView, Bitmap baseImage, boolean scroll, int radiusPxToRound) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 2;
        Bitmap bitmap = getBitmapFromCache(url, -1);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null && !scroll) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm, radiusPxToRound);
                task.setWantImageInfo(new int[]{displaySize[0], displaySize[1], 0});
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else if (bitmap == null && scroll) {
            imageView.setImageBitmap(baseImage);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * INFO gue/2014. 9. 16. : [임시코드] 이미지 라운드 처리 관련
     * 2-2. image loading : url<br>
     * imageView에 쓰이는 Image를 device의 display size를 사용하여 최적화된 size로 decode하여 system의 부하를 줄인다. <br/>
     * 추가로 이미지 라운드 처리를 한다.
     *
     * @param url       image url, null일경우 baseImage를 셋팅해준다.
     * @param imageView set down image, null일경우 return
     * @param baseImage bitmap of base image
     * @param scroll    리스트에서 스크롤 상태값, true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     * @author gue
     */
    public final void downloadOptimizeCircleImg(String url, ImageView imageView, Bitmap baseImage, boolean scroll) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 6;
        Bitmap bitmap = getBitmapFromCache(url, -1);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null && !scroll) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(new int[]{displaySize[0], displaySize[1], 0});
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else if (bitmap == null && scroll) {
            imageView.setImageBitmap(baseImage);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 3. image loading : url<br>
     * gridView등을 사용할 때 imageView가 thumbnail 형태의 작은 이미지일 경우
     * device의 display size를 사용하여 입력받은 thumbNumOfWidth를 사용하여
     * display size/thumbNumOfWidth 로 계산된 size로 decode하여 system의 부하를 줄인다.
     *
     * @param url             image url, null일경우 baseImage를 셋팅해준다.
     * @param thumbNumOfWidth 화면의 가로를 기준으로 들어갈 cell의 숫자
     * @param imageView       set down image, null일경우 return
     * @param baseImage       bitmap of base image
     * @author gue
     */
    public final void downloadForThumb(String url, int thumbNumOfWidth, ImageView imageView, Bitmap baseImage) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 3;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setThumbWidth(displaySize[0] / thumbNumOfWidth);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 3-1. image loading : url<br>
     * gridView등을 사용할 때 imageView가 thumbnail 형태의 작은 이미지일 경우
     * device의 display size를 사용하여 입력받은 thumbNumOfWidth를 사용하여
     * display size/thumbNumOfWidth 로 계산된 size로 decode하여 system의 부하를 줄인다.
     *
     * @param url             image url, null일경우 baseImage를 셋팅해준다.
     * @param thumbNumOfWidth 화면의 가로를 기준으로 들어갈 cell의 숫자
     * @param imageView       set down image, null일경우 return
     * @param baseImage       bitmap of base image
     * @param scroll          리스트에서 스크롤 상태값, true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     * @author gue
     */
    public final void downloadForThumb(String url, int thumbNumOfWidth, ImageView imageView, Bitmap baseImage, boolean scroll) {
        if (checkBeforeStartLoader(url, imageView, baseImage)) return;
        int loaderForm = 3;
        Bitmap bitmap = getBitmapFromCache(url, loaderForm);
        boolean cancelTask = cancelPotentialDownload(url, imageView, loaderForm);

        if (bitmap == null && !scroll) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setThumbWidth(displaySize[0] / thumbNumOfWidth);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, url));
//                imageView.setMinimumHeight(156);
//                task.execute(url);
                startLoader(task, url);
            }
        } else if (bitmap == null && scroll) {
            imageView.setImageBitmap(baseImage);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 4. image loading : real path
     *
     * @param imageFilePath image realPath, null일경우 baseImage를 셋팅해준다.
     * @param wantImageInfo {@link ImageLoaderTask#wantImageInfo}
     * @param imageView     set down image, null일경우 return
     * @param baseImage     bitmap of base image
     * @author gue
     */
    public final void downloadLocalPath(String imageFilePath, int[] wantImageInfo, ImageView imageView, Bitmap baseImage) {
        if (checkBeforeStartLoader(imageFilePath, imageView, baseImage)) return;
        int loaderForm = 4;
        Bitmap bitmap = getBitmapFromCache(imageFilePath, loaderForm);
        boolean cancelTask = cancelPotentialDownload(imageFilePath, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(wantImageInfo);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, imageFilePath));
//                imageView.setMinimumHeight(156);
//                task.execute(imageFilePath);
                startLoader(task, imageFilePath);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 4-1. image loading : real path
     *
     * @param imageFilePath image realPath, null일경우 baseImage를 셋팅해준다.
     * @param wantImageInfo {@link ImageLoaderTask#wantImageInfo}
     * @param imageView     set down image, null일경우 return
     * @param baseImage     bitmap of base image
     * @param scroll        true : 스크롤 중인 상태일 때 캐쉬에 있다면 해당 bitmap을 설정하고 아니라면 baseImage를 설정한다.
     * @author gue
     */
    public final void downloadLocalPath(String imageFilePath, int[] wantImageInfo, ImageView imageView, Bitmap baseImage, boolean scroll) {
        if (checkBeforeStartLoader(imageFilePath, imageView, baseImage)) return;
        int loaderForm = 4;
        Bitmap bitmap = getBitmapFromCache(imageFilePath, loaderForm);
        boolean cancelTask = cancelPotentialDownload(imageFilePath, imageView, loaderForm);

        /*if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(wantImageInfo);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, imageFilePath));
//                imageView.setMinimumHeight(156);
                startLoader(task, imageFilePath);
            }
        } 
        else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }*/
        if (bitmap == null && !scroll) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(wantImageInfo);
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, imageFilePath));
//                imageView.setMinimumHeight(156);
//                task.execute(imageFilePath);
                startLoader(task, imageFilePath);
            }
        } else if (bitmap == null && scroll) {
            imageView.setImageBitmap(baseImage);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * 이미지 회전하여 로딩 추가
     * 5. image loading : real path & iamge rotate
     *
     * @param imageFilePath image realPath, null일경우 baseImage를 셋팅해준다.
     * @param wantImageInfo {@link ImageLoaderTask#wantImageInfo}
     * @param imageView     set down image, null일경우 return
     * @param baseImage     bitmap of base image
     * @param rotate        <li>0 : 정상</li>
     *                      <li>1 : 90도</li>
     *                      <li>2 : 180도</li>
     *                      <li>3 : 270도</li>
     * @author gue
     */
    public final void downloadLocalPathMatrix(String imageFilePath, int[] wantImageInfo, ImageView imageView, Bitmap baseImage, int rotate) {
        if (checkBeforeStartLoader(imageFilePath, imageView, baseImage)) return;
        int loaderForm = 5;
        Bitmap bitmap = getBitmapFromCache(imageFilePath, loaderForm);
        boolean cancelTask = cancelPotentialDownload(imageFilePath, imageView, loaderForm);

        if (bitmap == null) {
            if (cancelTask) {
                ImageLoaderTask task = new ImageLoaderTask(imageView, _cacheDir, this, loaderForm);
                task.setWantImageInfo(wantImageInfo);
                task.rotate = rotate;
                imageView.setImageDrawable(setBaseDrawable(task, baseImage, imageFilePath));
//                imageView.setMinimumHeight(156);
//                task.execute(imageFilePath);
                startLoader(task, imageFilePath);
            }
        } else {
            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundResource(0);
        }
    }


    /* ************************************************************************************************
     * INFO AsyncTask manage method
     */
    private final void startLoader(ImageLoaderTask task, String url) {
        synchronized (asyncTaskTable) {
//            asyncTaskTable.put(url, new WeakReference<ImageLoaderTask>(task));
            asyncTaskTable.put(url, task);
            Log.w(TAG, String.format("startLoader | task size=%d, url= %s", asyncTaskTable.size(), url));
            try {
                task.execute(url);
            } catch (RejectedExecutionException e) {
                android.util.Log.w(TAG, String.format("[RejectedExecutionException:%s] startLoader - %s ", e.getMessage(), url), e);
                clearAsyncTask();
                System.gc();
            } catch (Exception e) {
                android.util.Log.w(TAG, String.format("[Exception:%s] startLoader - %s ", e.getMessage(), url), e);
                clearAsyncTask();
                System.gc();
            }
        }
    }

    void removeAsyncTask(String url) {
        synchronized (asyncTaskTable) {
            try {
                asyncTaskTable.remove(url);
                Log.i(TAG, String.format("removeAsyncTask | task size=%d, url= %s", asyncTaskTable.size(), url));
            } catch (Exception e) {
                android.util.Log.w(TAG, String.format("[Exception:%s] removeTask - %s ", e.getMessage(), url), e);
            }
        }
    }

    private void clearAsyncTask() {
        synchronized (asyncTaskTable) {
            /*for (WeakReference<ImageLoaderTask> temp : asyncTaskTable.values()){
                ImageLoaderTask task = temp.get();
                if (task != null) {
                    boolean result = task.cancel(true);
                    Log.i(TAG, "clearAsyncTaskTable size - " + asyncTaskTable.size() + " / " + result);
                }
            }*/
            for (ImageLoaderTask temp : asyncTaskTable.values()) {
                if (temp != null) {
                    boolean result = temp.cancel(true);
                    Log.i(TAG, String.format("clearAsyncTask | task size=%d, cancel=%s, url= %s", asyncTaskTable.size(), result, temp._url));
                }
            }
            asyncTaskTable.clear();
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
/* ************************************************************************************************
 * INFO private method
 */

    /**
     * url과 imageView를 check하여 null일 경우 true를 return하여 ImageLoader의 작업을 시작하지 않는다.
     *
     * @author gue
     * @since 2012. 8. 11.
     */
    private final boolean checkBeforeStartLoader(String url, ImageView imageView, Bitmap baseImage) {
        boolean result = false;
        // softCache 생존 시간 제어
        resetPurgeTimer();

        // 아무 작업도 하지 않는다.
        if (imageView == null) result = true;

        // url이 없을경우 baseImage로 설정한다.
        if (!StringUtil.nullCheckB(url)) {
            imageView.setImageBitmap(baseImage);
            result = true;
        }
        return result;
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private final Bitmap getBitmapFromCache(String url, int loaderForm) {
        if (url != null) {
            if (loaderForm == 3) url = url + "_thumb";
            if (loaderForm == -1) url = url + "_round";

            // 1. hard reference check
            synchronized (sHardBitmapCache) {
                final Bitmap bitmap = sHardBitmapCache.get(url);
                if (bitmap != null) {
                    // Bitmap found in hard cache Move element to first position, so that it is removed last
                    sHardBitmapCache.remove(url);
                    sHardBitmapCache.put(url, bitmap);
                    return bitmap;
                }
            }

            // 2. soft reference check
            SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
            if (bitmapReference != null) {
                final Bitmap bitmap = bitmapReference.get();
                if (bitmap != null) {
                    // Bitmap found in soft cache
                    return bitmap;
                } else {
                    // Soft reference has been Garbage Collected
                    sSoftBitmapCache.remove(url);
                }
            }
        }
        return null;
    }

    private Drawable setBaseDrawable(ImageLoaderTask task, Bitmap baseImage, String url) {
        DrawBase baseDrawble = null;
        // 기본 이미지가 있으면 이미지 넣고 없다면 흰 배경
        if (baseImage != null) {
            baseDrawble = new DrawBitmap(task, baseImage, url);
        } else {
            baseDrawble = new DrawColor(task, url);
        }
        return (Drawable) baseDrawble;
    }

    /**
     * 동일 url에 대한 AsyncTask 처리
     *
     * @return <li>true : 다운로드 진행, 동일 ImageView에 AsyncTask 있을시에는 선행 다운로드 취소
     * <li>false : 다운로드 취소, ImageView의 AsyncTask에서 해당 url의 다운로드가 진행중임
     */
    private final boolean cancelPotentialDownload(String url, ImageView imageView, int loaderForm) {
        ImageLoaderTask imageLoaderTask = getImageLoaderTask(imageView);
        if (imageLoaderTask != null) {
            String bitmapUrl = imageLoaderTask._url;
            if (loaderForm == 3) url = url + "_thumb";
            if (imageLoaderTask.thumbWidth > 0) bitmapUrl = bitmapUrl + "_thumb";
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                boolean result = imageLoaderTask.cancel(true);
                Log.i(TAG, String.format("cancelPotentialDownload | task cancel=%s, url= %s", result, url));
                removeAsyncTask(url);
                return true;
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private final static ImageLoaderTask getImageLoaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DrawBase) {
                DrawBase drawBase = (DrawBase) drawable;
                return drawBase.getImageLoaderTask();
            }
        }
        return null;
    }

    /**
     * imageView에 삽입된 {@link DrawBase}의 tag를 가져온다.
     */
    private final static String getDrawableTag(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DrawBase) {
                DrawBase drawBase = (DrawBase) drawable;
                return drawBase.getDrawableTag();
            }
        }
        return null;
    }


    /* ************************************************************************************************
     * INFO clear cache & add cache
     */
    private final Handler purgeHandler = new Handler();
    private final Runnable purger = new Runnable() {
        @Override
        public void run() {
            clearAsyncTask();
            clearCache();
        }
    };

    private final void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
        LoaderTaskQueue.getInstance().init();
        System.gc();
        Log.i(TAG, "clear cache");
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private final void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, RESET_PURGE_TIME);
    }


    /* ************************************************************************************************
     * INFO user method
     */
    public final void setCacheDir(String cacheDir) {
        _cacheDir = cacheDir;
    }

    public final String getCacheDir() {
        return _cacheDir;
    }

    public final void setsdErrStr(String msg) {
        _sdErrStr = msg;
    }

    private Handler _sdcardErrHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(_context, _sdErrStr, Toast.LENGTH_LONG).show();
            return true;
        }
    });

    final void sdcardErrToast() {
        synchronized (_sdcardErrHandler) {
            if (!_sdErrFlag && _sdErrStr != null) {
                _sdErrFlag = true;
                _sdcardErrHandler.sendEmptyMessage(0);
            }
        }
    }

    /**
     * url을 키값으로 가지는 bitmap을 sHardBitmapCache,sSoftBitmapCache에서 검사하여 map에서 remove한다.<br>
     * 해당 bitmap은 recycle하지 않는다. recycle은 사용자가 알아서..
     * 해당 bitmap이 아직 특정View에서 사용중인 경우 RuntimeError을 유발할 수 있기 때문이다. <br>
     * url이 null일경우 map에 담긴 모든 bitmap을 삭제한다.
     *
     * @author gue
     */
    public final void clearCache(String url) {
        if (StringUtil.nullCheckB(url)) {
            if (sHardBitmapCache.containsKey(url)) {
                sHardBitmapCache.remove(url);
            } else if (sSoftBitmapCache.containsKey(url)) {
                sSoftBitmapCache.remove(url);
            }
        } else clearCache();
    }

    /**
     * 작업중... 완료되면 지시자 public으로 변경예정<p>
     * Activity의 orientation에 따라 image의 size를 달리 decode하여 보여주고 싶을 경우
     * Activity의 onConfigurationChanged()을 override한 곳에서 호출하여 현재 device의 size가 변경되었음을 알려주어 image의 재 decode를 실행하게끔 한다.
     *
     * @param newConfig onConfigurationChanged()의 Configuration 전달
     * @author gue
     * @since 2013. 1. 21.
     */
    protected final void onConfigurationChanged(Configuration newConfig) {
        clearCache();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            displaySize = DeviceUtil.getDisplaySize(_context);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            displaySize = DeviceUtil.getDisplaySize(_context);
        }
    }

    /* ************************************************************************************************
     * INFO imageLoadCompleteListener callBack
     */
    @Override
    public final void onCompleted(String url, ImageView imageView, Bitmap bitmap, boolean downErr, int[] forms) {
        Log.i(TAG, String.format("onCompleted | task size=%d, url= %s", asyncTaskTable.size(), url));
        removeAsyncTask(url);

        if (downErr || bitmap == null) {
            synchronized (this) {
                clearCache();
                Log.i(TAG, Thread.currentThread().getName() + " - clearcache : " + url);
                return;
            }
        }

        int form = forms[0];
        int radiusPxToRound = forms[1];
        switch (form) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                synchronized (this) {
                    String drawableTag = getDrawableTag(imageView);
                    // INFO gue/2014. 9. 16. : [임시코드] 이미지 라운드 처리 관련
                    if (bitmap != null) {
                        if (radiusPxToRound > 0) {
                            try {
                                Bitmap roundBitmap = ImageAlter.roundCorner(bitmap, radiusPxToRound);
                                bitmap.recycle();
                                bitmap = null;
                                bitmap = roundBitmap;
                            } catch (OutOfMemoryError e) {
                                Log.e(TAG, e.getMessage(), e);
                                ;
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                                ;
                            }
                        }
                        if (imageView != null && bitmap != null && drawableTag != null && drawableTag.equals(url)) {
                            imageView.setImageBitmap(bitmap);
                            imageView.startAnimation(AnimationSuite.fadeIn());
                            imageView.setBackgroundResource(0);
                        }
                        if (form == 3) url = url + "_thumb";
                        if (radiusPxToRound > 0) url = url + "_round";
                        sHardBitmapCache.put(url, bitmap);
                    }
                }
                break;
            case 6:
                synchronized (this) {
                    String drawableTag = getDrawableTag(imageView);
                    if (bitmap != null) {
                        try {
                            Bitmap roundBitmap = ImageAlter.circleImage(bitmap, 0,0);
                            bitmap.recycle();
                            bitmap = null;
                            bitmap = roundBitmap;
                        } catch (OutOfMemoryError e) {
                            Log.e(TAG, e.getMessage(), e);
                            ;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                            ;
                        }
                        if (imageView != null && bitmap != null && drawableTag != null && drawableTag.equals(url)) {
                            imageView.setImageBitmap(bitmap);
                            imageView.startAnimation(AnimationSuite.fadeIn());
                            imageView.setBackgroundResource(0);
                        }
                        url = url + "_circle";
                        sHardBitmapCache.put(url, bitmap);
                    }
                }
                break;
        }
    }

}