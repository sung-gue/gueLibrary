package com.breakout.util;

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

import androidx.appcompat.app.AppCompatActivity;

import com.breakout.util.widget.DialogView;
import com.breakout.util.widget.DialogView.Size;

import java.util.List;


/**
 * {@link AppCompatActivity}를 부모로 두고 UI의 작성 편의를 위한 abstract method와 유용한 method 몇가지를 제공한다.<br>
 * <dl>
 * <dt>abstract method</dt>
 * <dd>
 * <li>{@link #initTitle()}</li>
 * <li>{@link #initFooter()}</li>
 * <li>{@link #initBody()}</li>
 * <li>{@link #refreshUI()}</li>
 * <li></li>
 * </dd>
 * <dt>useful method</dt>
 * <dd>
 * <li>{@link #initUI()} :
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
 * <p>
 * 기본 생명주기에 대한 log와 progress dialog를 사용할 수 있게 하여주고 stack의 일괄 종료에 대한 receiver을 제공한다.<br>
 * {@link #_pDialog}, {@link #_context}는 {@link #onDestroy()}에서 null 처리 된다.
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2016. 2. 3.
 */
public abstract class AppCompatActivityEx extends AppCompatActivity {
    /**
     * Activity Tag : class simpleName
     */
    protected final String TAG = getClass().getSimpleName();
    /**
     * The App context.
     */
    protected Context _appContext;
    /**
     * Activity Context
     */
    protected Context _context = this;


/* ************************************************************************************************
 * INFO UI 구현
 */

    /**
     * UI는 일반적으로 title, body, footer로 구성되었다는 것에 착안하여 UI구현을 나누어 작업 할수 있도록 한다.<br/>
     * child class에서 override된 abstracr method를 작성하였다면 child class의 {@link #onCreate(Bundle)}에서 호출하여 아래의 순서대로 일괄 호출한다.<p>
     * 실행 순서 {@link #initTitle()} > {@link #initFooter()} > {@link #initBody()}
     */
    protected void initUI() {
        initTitle();
        initFooter();
        initBody();
    }

    /**
     * {@link #initUI()}에서 호출되며 UI의 title 영역 구현<br/>
     */
    protected abstract void initTitle();

    /**
     * {@link #initUI()}에서 호출되며 UI의 footer 영역 구현<br/>
     */
    protected abstract void initFooter();

    /**
     * {@link #initUI()}에서 호출되며 UI의 body 영역 구현<br/>
     */
    protected abstract void initBody();

    /**
     * UI 새로고침 구현
     */
    protected abstract void refreshUI();


/* ************************************************************************************************
 * INFO dialog
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
     * see {@link #showProgress(View, Drawable)}
     */
    public Dialog showProgress(View view) {
        return showProgress(view, null);
    }

    /**
     * see {@link #showProgress(View, Drawable)}
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


/* ************************************************************************************************
 * INFO finish receiver
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
        } catch (Exception e) {
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


/* ************************************************************************************************
 * INFO : intent method
 */

    /**
     * startActivity check
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (CValue.DEBUG) {
            StringBuilder logBuilder = new StringBuilder("-------------------------------------------------\n");
            logBuilder.append(String.format("| %s | startActivity\n", TAG));
            logBuilder.append(String.format("|  intent      | %s\n", intent));
            logBuilder.append(String.format("|  requestCode | %d\n", requestCode));
            logBuilder.append(String.format("|  component   | %s\n", intent != null ? intent.getComponent() : ""));
            logBuilder.append(String.format("|  uri         | %s\n", intent != null ? intent.getData() : ""));
            try {
                if (intent != null && intent.getExtras() != null) {
                    logBuilder.append("|  extra       |\n");
                    Bundle bundle = intent.getExtras();
                    for (String key : bundle.keySet()) {
                        logBuilder.append(String.format("       %s : %s\n", key, bundle.get(key)));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            logBuilder.append("-------------------------------------------------");
            Log.i(TAG, logBuilder.toString());
        }
        super.startActivityForResult(intent, requestCode);
    }

    
/* ************************************************************************************************
 * INFO activity life cycle
 */

    /**
     * 데이터의 초기화 작업 이후에 UI를 설정하기 위하여 {@link #initUI()}를 하여 주어야 한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(getPackageName(), TAG + " | onCreate");
        _appContext = getApplicationContext();
        super.onCreate(savedInstanceState);
        logTask("oncreate()");

        if (CValue.DEBUG) {
            try {
                Intent intent = getIntent();
                StringBuilder logBuilder = new StringBuilder("-------------------------------------------------\n");
                logBuilder.append(String.format("| %s - oncreate() | check intent \n", TAG));
                logBuilder.append(String.format("|  intent      | %s\n", intent));
                logBuilder.append(String.format("|  component   | %s\n", intent.getComponent()));
                logBuilder.append(String.format("|  uri         | %s\n", intent.getData()));
                try {
                    if (intent.getExtras() != null) {
                        logBuilder.append("|  extra       |\n");
                        Bundle bundle = intent.getExtras();
                        for (String key : bundle.keySet()) {
                            logBuilder.append(String.format("       %s : %s\n", key, bundle.get(key)));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                logBuilder.append("-------------------------------------------------");
                Log.i(TAG, logBuilder.toString());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }


    }

    @Override
    protected void onStart() {
        Log.v(getPackageName(), TAG + " | onStart");
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(getPackageName(), TAG + " | onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        Log.v(getPackageName(), TAG + " | onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.v(getPackageName(), TAG + " | onResume");
        if (_appContext == null) _appContext = getApplicationContext();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.v(getPackageName(), TAG + " | onStop, isfinishing : " + isFinishing());
        super.onStop();
    }

    @Override
    public void finish() {
        Log.v(getPackageName(), TAG + " | finish, isfinishing : " + isFinishing());
        logTask("finish()");
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.v(getPackageName(), TAG + " | onDestroy, isfinishing : " + isFinishing());
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
        Log.v(getPackageName(), TAG + " | onBackPressed, isfinishing : " + isFinishing());
        super.onBackPressed();
    }

    protected void logTask(String title) {
        if (!CValue.DEBUG) {
            return;
        }

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

        try {
            List<RunningTaskInfo> info = am.getRunningTasks(1);
            Log.i(TAG, String.format("-------------------------------------------------\n" +
                            "| %s - %s\n" +
                            "|  baseActivity    | %s\n" +
                            "|  topActivity     | %s\n" +
                            "|  numActivities   | %s\n" +
                            "|  numRunning      | %s\n" +
                            "-------------------------------------------------",
                    TAG, title, info.get(0).baseActivity.getClassName(), info.get(0).topActivity.getClassName(), info.get(0).numActivities, info.get(0).numRunning));
        } catch (Exception e) {
            Log.e(getPackageName(), TAG + " | " + e.getMessage(), e);
        }
    }


/* ************************************************************************************************
 * INFO db control
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
