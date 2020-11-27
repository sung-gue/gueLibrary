package com.breakout.util.img;

import com.breakout.util.Log;

import java.util.Locale;


/**
 * Thread Queue manager <br/>
 * {@link ImageLoader}로 생성되는 {@link ImageLoaderTask}가 동시에 실행될 수 있는 갯수를 지정한다.
 * 전체 갯수를 의미하는 것은 아니다.
 *
 * @author sung-gue
 * @version 1.0 (2012. 7. 31.)
 */
class LoaderTaskQueue {
    private final String TAG = getClass().getSimpleName();
    /**
     * 동시에 decode 작업 할수 있는 thread size
     */
    private final int _loaderQueueMax = 10;

    private int _loaderQueue;

//    private Semaphore _semaphore;

    private static LoaderTaskQueue _this;

    private LoaderTaskQueue() {
//        _semaphore = new Semaphore(6);
        _loaderQueue = 0;
        Log.i(TAG, "create LoaderTaskQueue instance");
    }

    static LoaderTaskQueue getInstance() {
        if (_this == null) _this = new LoaderTaskQueue();
        return _this;
    }

    static void destroyInstance() {
        if (_this != null) _this.destroy();
    }

    void init() {
        _this._loaderQueue = 0;
    }

    private void destroy() {
        Log.i(TAG, "destroy LoaderTaskQueue instance");
        _this = null;
    }

    public final synchronized void getQueue(String tag, String msg) {
        Log.i(TAG, String.format(Locale.getDefault(), "(%s) 1-1. current queue count : %d/%s", tag, _loaderQueue, msg));
        while (_loaderQueue == _loaderQueueMax) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "InterruptedException : " + msg, e);
            }
        }
        _loaderQueue++;
        Log.i(TAG, String.format(Locale.getDefault(), "(%s) 1-2. add queue count : %d/%s", tag, _loaderQueue, msg));

        /*Log.i(TAG, String.format(Locale.getDefault(), "(%s)/%s 1-1. current queue count : %d/%s", Thread.currentThread().getName(), tag, 6 - _semaphore.availablePermits(), msg));
        try {
            _semaphore.acquire();
        } catch (InterruptedException e) {
            Log.w(TAG, "InterruptedException : " + msg, e);
        }
        Log.i(TAG, String.format(Locale.getDefault(), "(%s)/%s 1-2. add queue count : %d/%s", Thread.currentThread().getName(), tag, 6 - _semaphore.availablePermits(), msg));*/
//        _semaphore.release();
//        Log.i(TAG, String.format(Locale.getDefault(), "(%s)/%s 2. remove queue count : %d/%s", Thread.currentThread().getName(), tag, 6 - _semaphore.availablePermits(), msg ));
    }

    public final synchronized void removeQueue(String tag, String msg) {
        if (_loaderQueue != 0) _loaderQueue--;
        try {
            this.notifyAll();
        } catch (Exception e) {
            android.util.Log.w(TAG, "InterruptedException : " + msg, e);
        }
        Log.i(TAG, String.format(Locale.getDefault(), "(%s) 2. remove queue count : %d/%s", tag, _loaderQueue, msg));
        
        /*
        _semaphore.release();
        if (DEBUG) Log.i(TAG, String.format(Locale.getDefault(), "(%s)/%s 2. remove queue count : %d/%s", Thread.currentThread().getName(), tag, _loaderQueueMax - _semaphore.availablePermits(), msg ));
        */
    }
}