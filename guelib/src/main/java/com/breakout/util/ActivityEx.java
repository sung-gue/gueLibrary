package com.breakout.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.breakout.util.widget.DialogView;
import com.breakout.util.widget.DialogView.Size;

import java.util.List;

/**
 * {@link Activity}를 부모로 두고 UI의 작성 편의를 위한 abstract method와 유용한 method 몇가지를 제공한다.<br>
 * <dl>
 * <dt>abstract method</dt>
 * <dd>
 * <li>{@link #setTitleBar()}</li>
 * <li>{@link #setHeader()}</li>
 * <li>{@link #setFooter()}</li>
 * <li>{@link #setBody()}</li>
 * <li>{@link #refreshUI()}</li>
 * <li></li>
 * </dd>
 * <dt>useful method</dt>
 * <dd>
 * <li>{@link #setUI()} :
 * 위에서 정의된 UI에 대한 구현된 method를 실행을 하기 위하여 사용필수이다. 순서 변경을 원할 경우 override하여 작성한다.
 * 제공하는 틀 이외의 UI의 구성을 사용하여 abstract method를 구현하지 않는다면 사용하지 않아도 무방하다.
 * </li>
 * <li>{@link #showProgress()}</li>
 * <li>{@link #showProgress(View)}</li>
 * <li>{@link #showProgress(View, Drawable)}</li>
 * <li>{@link #closeProgress()}</li>
 * <li>{@link #registerfinishReceiver()}</li>
 * <li>{@link #registerfinishReceiver(String)}</li>
 * <li></li>
 * </dd>
 * <dt>useful member value</dt>
 * <dd>
 * <li>{@link #TAG} :  Activity Tag, class simpleName</li>
 * <li>{@link #_appContext} : Application Context</li>
 * <li>{@link #_context} : Activity Context</li>
 * <li>{@link #_pDialog} : dialog</li>
 * <li></li>
 * </dd>
 * </dl>
 * <p/>
 * 기본 생명주기에 대한 log와 progress dialog를 사용할 수 있게 하여주고 stack의 일괄 종료에 대한 receiver을 제공한다.<br>
 * {@link #_pDialog}, {@link #_context}는 {@link #onDestroy()}에서 null 처리 된다.
 *
 * @author sung-gue
 * @version 1.0 (2013. 1. 7.)
 */
public abstract class ActivityEx extends Activity {
    /**
     * Activity Tag : class simpleName
     */
    protected final String TAG = getClass().getSimpleName();
    /**
     * Application Context
     */
    protected Context _appContext;
    /**
     * Activity Context
     */
    protected Context _context = this;


    /* ------------------------------------------------------------
        DESC UI 설정
     */

    /**
     * child class에서 override한 abstracr method 실행!<p>
     * 보통 App의 UI의 모양이 title, header, body, footer로 나뉘어 진 형태가 많기 때문에
     * 하나의 UI를 작성 시에 4부분으로 나누어 처리될 수 있게끔 함수를 작성한다.
     * 필요치 않을경우 사용하지 않아도 무방하다.<br>
     * 하지만 4가지 부분에 대해 override를 하여 내용을 작성 하였다면 child class에서는 초기화 작업을
     * 마친후에 {@link #onCreate(Bundle)}에서 필수로 호출하여 사용하여야 한다.<br>
     * 실행 순서 title > header > footer > body
     */
    protected void setUI() {
        setTitleBar();
        setHeader();
        setFooter();
        setBody();

    }

    /**
     * 1. titleBar부분에 대한 작성<br/>
     *
     * @see #setUI()
     */
    protected abstract void setTitleBar();

    /**
     * 2. header부분에 대한 작성
     *
     * @see #setUI()
     */
    protected abstract void setHeader();

    /**
     * 3. footer부분에 대한 작성
     *
     * @see #setUI()
     */
    protected abstract void setFooter();

    /**
     * 4. boty부분에 대한 작성
     *
     * @see #setUI()
     */
    protected abstract void setBody();

    /**
     * 5. UI의 새로고침에 대한 작성
     */
    protected abstract void refreshUI();


    /* ------------------------------------------------------------
        DESC dialog
     */
    /**
     * {@link #showProgress(View, Drawable)}로 생성한 progress dialog
     */
    private Dialog _pDialog;

    /**
     * create & show {@link #_pDialog}, used {@link DialogView}
     *
     * @param view       dialog안에 들어갈 view 정의
     * @param backGround dialog의 배경 drawable
     * @return {@link #_pDialog}
     */
    public Dialog showProgress(View view, Drawable backGround) {
        if (_pDialog == null) {
            DialogView dv;
            if (view != null) {
                dv = new DialogView(this, view, backGround);
            } else {
                dv = new DialogView(this, Size.small);
            }
            _pDialog = dv.getDialog();
        }

        if (_pDialog != null && !_pDialog.isShowing()) {
            Log.v(TAG, "progress show");
            try {
                _pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _pDialog;
    }

    /**
     * @see #showProgress(View, Drawable)
     */
    public Dialog showProgress(View view) {
        return showProgress(view, null);
    }

    /**
     * @see #showProgress(View, Drawable)
     */
    public Dialog showProgress() {
        return showProgress(null, null);
    }

    /**
     * close {@link #_pDialog}
     */
    public void closeProgress() {
        if (_pDialog != null && _pDialog.isShowing()) {
            Log.v(TAG, "progress close");
            _pDialog.dismiss();
            _pDialog = null;
        }
    }


    /* ------------------------------------------------------------
        DESC finish receiver
     */
    /**
     * {@link #finishReceiver}가 등록이 되었다면 true
     */
    private boolean _finishReceiverIsRegistered;

    /**
     * {@link #onDestroy()} 호출되면 receiver 해제
     */
    private void unregisterReceiver() {
        try {
            if (_finishReceiverIsRegistered) unregisterReceiver(finishReceiver);
        } catch (Exception ignored) {
        }
    }

    /**
     * 동일한 activity로의 이동이 나오게 되면 이전 stack에 존재하는 activity를 finish()하여 준다.<br/>
     * activity의 simpleName으로 {@link #registerfinishReceiver(String)}을 실행한다.
     */
    protected void registerfinishReceiver() {
        registerfinishReceiver(getClass().getName());
    }

    /**
     * filterName으로 등록된 activity를 finish()하여 준다.
     * 동일한 activity에 대하여 stack 쌓임 방지, filterName로 등록된 activity에 대한 일괄종료 등을 구현할때 사용한다.<br>
     * activity의 onDestroy가 호출되면 등록된 receiver는 해제된다.
     */
    protected void registerfinishReceiver(String filterName) {
        _finishReceiverIsRegistered = true;
        registerReceiver(finishReceiver, new IntentFilter(filterName));
    }

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


    /* ------------------------------------------------------------
        DESC : intent method
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Log.i(TAG, String.format("-------------------------------------------------\n" +
                        "%s | startActivity\n" +
                        "|  %s\n" +
                        "|  requestCode : %d\n" +
                        "|  component : %s\n" +
                        "-------------------------------------------------",
                TAG, intent, requestCode, intent != null ? intent.getComponent() : ""));
        super.startActivityForResult(intent, requestCode);
    }


    /* ------------------------------------------------------------
        DESC: activity life cycle
     */

    /**
     * 데이터의 초기화 작업 이후에 UI를 설정하기 위하여 {@link #setUI()}를 하여 주어야 한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(getPackageName(), TAG + " | onCreate");
        _appContext = getApplicationContext();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.w(getPackageName(), TAG + " | onStart");
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.w(getPackageName(), TAG + " | onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        Log.w(getPackageName(), TAG + " | onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.w(getPackageName(), TAG + " | onResume");
        if (_appContext == null) _appContext = getApplicationContext();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.w(getPackageName(), TAG + " | onStop, isfinishing : " + isFinishing());
        super.onStop();
    }

    @Override
    public void finish() {
        Log.w(getPackageName(), TAG + " | finish, isfinishing : " + isFinishing());

        // get activity task
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // process & task check
        /*List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : processList) {
            if (getPackageName().equals(runningAppProcessInfo.processName)) 
                Log.e(TAG + " | runningAppProcessInfo : " + runningAppProcessInfo.pid  + " / " + runningAppProcessInfo.processName);
            else Log.i(TAG + " | runningAppProcessInfo : " + runningAppProcessInfo.pid  + " / " + runningAppProcessInfo.processName );
        }
        List<RunningTaskInfo> taskList = am.getRunningTasks(processList.size());
        for (RunningTaskInfo runningTaskInfo : taskList) {
            if (getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) 
                Log.e(TAG + " | runningTaskInfo : " + runningTaskInfo.topActivity.getPackageName()  + " / " + runningTaskInfo.topActivity  );
            else Log.i(TAG + " | runningTaskInfo : " + runningTaskInfo.topActivity.getPackageName()  + " / " + runningTaskInfo.topActivity  ); 
        }*/

        List<RunningTaskInfo> info = am.getRunningTasks(1);
        Log.i(TAG, String.format("-------------------------------------------------\n" +
                        "%s | activity finish\n" +
                        "     baseActivity : %s\n" +
                        "     topActivity : %s\n" +
                        "     numActivities : %s\n" +
                        "     numRunning : %s\n" +
                        "-------------------------------------------------",
                TAG, info.get(0).baseActivity.getClassName(), info.get(0).topActivity.getClassName(),
                info.get(0).numActivities, info.get(0).numRunning));
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.w(getPackageName(), TAG + " | onDestroy, isfinishing : " + isFinishing());
        if (_pDialog != null && _pDialog.isShowing()) {
            _pDialog.dismiss();
            _pDialog = null;
        }
        unregisterReceiver();
        _context = null;
        /*
        // Activity View resource 해제
        Util.recursiveRecycle(getWindow().getDecorView());
        */
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.w(getPackageName(), TAG + " | onBackPressed, isfinishing : " + isFinishing());
        super.onBackPressed();
    }


    /* ------------------------------------------------------------
        DESC db control
     */
//    /**
//     * SQLite3 instance 
//     */
//    protected LocalDB mdb;
//    /**
//     * write mode로 db의 connection create
//     */
//    protected final void mdb_write(){
//        mdb = new LocalDB(_context);    
//        mdb.openDB(Const.write);
//    }
//    /**
//     * read mode로 db의 connection create
//     */
//    protected final void mdb_read(){
//        mdb = new LocalDB(_context);    
//        mdb.openDB(Const.read);
//    }
//    /**
//     * db connection close
//     */
//    protected final void mdb_close(){
//        if (mdb != null){
//            mdb.close();
//        } mdb = null;
//    }


}
