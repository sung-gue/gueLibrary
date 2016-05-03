package com.breakout.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.breakout.util.widget.DialogView;
import com.breakout.util.widget.DialogView.Size;


/**
 * {@link Fragment}를 부모로 두고 UI의 작성 편의를 위한 abstract method와 유용한 method 몇가지를 제공한다.<br>
 * <dl>
 * <dt>abstract method</dt>
 * <dd>
 * <li>{@link #initUI()}</li>
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
 * @author gue
 * @version 1.0
 * @copyright Copyright.2016.gue.All rights reserved.
 * @since 2016.02.17
 */
public abstract class FragmentEx extends Fragment {
    /**
     * Activity Tag : class simpleName
     */
    public final String TAG = getClass().getSimpleName();
    /**
     * The App context.
     */
    protected Context _appContext;
    /**
     * Activity Context
     */
    protected Context _context;


/* ************************************************************************************************
 * INFO UI 구현
 */

    /**
     * UI 구현
     */
    protected abstract void initUI();

    /**
     * UI 새로고침 구현
     */
    protected abstract void refreshUI();


/* ************************************************************************************************
 * INFO dialog
 */
    // TODO: 2016-02-17 Fragment 에서 사용될 수 있는 dialog 변경작업 필요
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
                dv = new DialogView(_context, view, backGround);
            } else {
                dv = new DialogView(_context, Size.small);
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
 * INFO Fragment management
 */
    // TODO: 2016-02-17 Fragment stack 등 관리 목적 코드 필요


/* ************************************************************************************************
 * INFO : intent method
 */

    /**
     * startActivity check
     */
    /*@Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (CValue.DEBUG) {
            StringBuilder logBuilder = new StringBuilder("-------------------------------------------------\n");
            logBuilder.append(String.format("| %s | startActivity\n", TAG));
            logBuilder.append(String.format("|  %s\n", intent));
            logBuilder.append(String.format("|  requestCode : %d\n", requestCode));
            logBuilder.append(String.format("|  component : %s\n", intent != null ? intent.getComponent() : ""));
            logBuilder.append(String.format("|  uri : %s\n", intent != null ? intent.getData() : ""));
            try {
                if (intent != null && intent.getExtras() != null) {
                    logBuilder.append("|  extra bundle\n");
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
    }*/

    /*public void fragmentTransactionCommit(int containerViewId, Fragment fragment, String tag, boolean useBackStack, String backStackName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        if (useBackStack) {
            fragmentTransaction.addToBackStack(backStackName);
        }
        fragmentTransaction.commit();
    }*/

    
/* ************************************************************************************************
 * INFO fragment life cycle
 */

    /**
     * Fragment가 Activity에 최초로 연결될 때 호출
     */
    @Override
    public void onAttach(Context context) {
        Log.v(TAG, "onAttach | " + context);
        super.onAttach(context);
        _appContext = context.getApplicationContext();
        _context = getActivity();
    }

    /**
     * Fragment 최초 생성 시점에 호출
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate | " + _context);
        super.onCreate(savedInstanceState);
        /*
        // option menu를 fragment에서 사용할 경우 선언
        setHasOptionsMenu(true);
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView | " + _context);
        super.onCreateView(inflater, container, savedInstanceState);
        return null;
    }

    /**
     * Activity의 onCreate()가 반환된 후에 호출
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated | " + _context);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * fragment를 복구할 필요가 있을경우 상태를 bundle로 저장
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (CValue.DEBUG) {
            StringBuilder logBuilder = new StringBuilder("-------------------------------------------------\n");
            logBuilder.append(String.format("| %s | Fragment.onSaveInstanceState\n", TAG));
            try {
                if (outState != null) {
                    logBuilder.append("|  outState \n");
                    for (String key : outState.keySet()) {
                        logBuilder.append(String.format("       %s : %s\n", key, outState.get(key)));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            logBuilder.append("-------------------------------------------------");
            Log.i(TAG, logBuilder.toString());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity가 화면에 보이게 되면 호출
     */
    @Override
    public void onStart() {
        Log.v(TAG, "onStart | " + _context);
        super.onStart();
    }

    /**
     * Activity가 준비 완료되면 호출
     */
    @Override
    public void onResume() {
        Log.v(TAG, "onResume | " + _context);
        super.onResume();
    }

    /**
     * Activity가 화면에는 보이지만 포커스를 일게 되면 호출
     */
    @Override
    public void onPause() {
        Log.v(TAG, "onPause | " + _context);
        super.onPause();
    }

    /**
     * Activity가 더이상 화면에 보이자 않게 되면 호출
     */
    @Override
    public void onStop() {
        Log.v(TAG, "onStop | " + _context);
        super.onStop();
    }

    /**
     * onCreateView()에서 호출된 View가 Activity에서 제거되면서 호출
     * 일반적으로 View 리소스를 해제하는 용도로 사용
     */
    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestroyView | " + _context);
        super.onDestroyView();
    }

    /**
     * onCreate()에 대응되는 함수로 Fragment가 더이상 유효하지 않을 때 호출
     * 일반적으로 Fragment 자체 리소스를 해제하는 용도로 사용
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy | " + _context + ", isfinishing : " + getActivity().isFinishing());
        if (_pDialog != null && _pDialog.isShowing()) {
            _pDialog.dismiss();
            _pDialog = null;
        }
        _context = null;
        super.onDestroy();
    }

    /**
     * Fragment가 Activity와 연결이 끊어지는 상황에서 호출
     * 부모 Activity에서 Fragment의 참조를 가지고 있다면 해제하는 작업을 수행
     */
    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach | " + _context);
        super.onDetach();
    }

//    @Override
//    public void finish() {
//        Log.v(getPackageName(), TAG + " | finish, isfinishing : " + isFinishing());
//
//        // get activity task
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        // process & task check
//        /*List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
//        for (RunningAppProcessInfo runningAppProcessInfo : processList) {
//            if (getPackageName().equals(runningAppProcessInfo.processName))
//                Log.e(TAG + " | runningAppProcessInfo : " + runningAppProcessInfo.pid  + " / " + runningAppProcessInfo.processName);
//            else Log.i(TAG + " | runningAppProcessInfo : " + runningAppProcessInfo.pid  + " / " + runningAppProcessInfo.processName );
//        }
//        List<RunningTaskInfo> taskList = am.getRunningTasks(processList.size());
//        for (RunningTaskInfo runningTaskInfo : taskList) {
//            if (getPackageName().equals(runningTaskInfo.topActivity.getPackageName()))
//                Log.e(TAG + " | runningTaskInfo : " + runningTaskInfo.topActivity.getPackageName()  + " / " + runningTaskInfo.topActivity  );
//            else Log.i(TAG + " | runningTaskInfo : " + runningTaskInfo.topActivity.getPackageName()  + " / " + runningTaskInfo.topActivity  );
//        }*/
//
//        try {
//            List<RunningTaskInfo> info = am.getRunningTasks(1);
//            Log.i(TAG, String.format("-------------------------------------------------\n" +
//                            "%s | activity finish\n" +
//                            "     baseActivity : %s\n" +
//                            "     topActivity : %s\n" +
//                            "     numActivities : %s\n" +
//                            "     numRunning : %s\n" +
//                            "-------------------------------------------------",
//                    TAG, info.get(0).baseActivity.getClassName(), info.get(0).topActivity.getClassName(),
//                    info.get(0).numActivities, info.get(0).numRunning));
//        } catch (Exception e) {
//            Log.e(getPackageName(), TAG + " | " + e.getMessage(), e);
//        }
//        super.finish();
//    }


/* ************************************************************************************************
 * INFO option
 */

    /**
     * Activity의 option menu를 변경할 경우 작성
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            /*
            menu.clear();
            inflater.inflate(R.menu.menu_main, menu);
            */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case android.R.id.home: {
                getFragmentManager().popBackStack();
                break;
            }*/
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
        return super.onOptionsItemSelected(item);
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