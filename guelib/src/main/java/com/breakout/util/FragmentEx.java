package com.breakout.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakout.util.widget.DialogView;


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
 * @author sung-gue
 * @version 1.0 (2016-02-17)
 */
public abstract class FragmentEx extends Fragment {
    /**
     * Fragment Tag : class simpleName
     */
    public final String TAG = getClass().getSimpleName();
    /**
     * Application Context, init {@link #onAttach(Context)}
     */
    protected Context _appContext;
    /**
     * Activity Context, init {@link #onAttach(Context)}
     */
    protected Context _context;
    /**
     * Activity, init {@link #onAttach(Context)}
     */
    private Activity _activity;

    public FragmentEx() {
        super();
    }


    /*
        INFO: fragment life cycle
     */

    /**
     * Fragment가 Activity에 최초로 연결될 때 호출
     */
    @Override
    public void onAttach(@NonNull Context context) {
        Log.v(TAG, String.format("onAttach %s | %s", getTag(), context));
        super.onAttach(context);
        _appContext = context.getApplicationContext();
        _activity = getActivity();
        _context = getActivity();
    }

    /**
     * Fragment 최초 생성 시점에 호출<br/>
     * option menu를 fragment에서 사용할 경우 선언
     * <pre>
     *     setHasOptionsMenu(true);
     * </pre>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, String.format("onCreate %s", getTag()));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, String.format("onCreateView %s", getTag()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, String.format("onViewCreated %s", getTag()));
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Activity의 onCreate()가 반환된 후에 호출
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, String.format("onActivityCreated %s", getTag()));
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Fragment를 복구할 필요가 있을경우 상태를 bundle로 저장
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StringBuilder logBuilder = new StringBuilder();
        try {
            for (String key : outState.keySet()) {
                logBuilder.append(String.format("%s=%s ", key, outState.get(key)));
            }
        } catch (Exception ignored) {
        }
        Log.v(TAG, String.format("onSaveInstanceState %s | bundle: %s", getTag(), logBuilder.toString()));
        super.onSaveInstanceState(outState);
    }

    /**
     * Fragment가 화면에 보이게 되면 호출
     */
    @Override
    public void onStart() {
        Log.v(TAG, String.format("onStart %s", getTag()));
        super.onStart();
    }

    /**
     * Fragment가 준비 완료되면 호출
     */
    @Override
    public void onResume() {
        Log.v(TAG, String.format("onResume %s", getTag()));
        super.onResume();
    }

    /**
     * Fragment가 화면에는 보이지만 포커스를 일게 되면 호출
     */
    @Override
    public void onPause() {
        Log.v(TAG, String.format("onPause %s", getTag()));
        super.onPause();
    }

    /**
     * Fragment가 더이상 화면에 보이자 않게 되면 호출
     */
    @Override
    public void onStop() {
        Log.v(TAG, String.format("onStop %s", getTag()));
        super.onStop();
    }

    /**
     * onCreateView()에서 호출된 View가 Activity에서 제거되면서 호출
     * 일반적으로 View 리소스를 해제하는 용도로 사용
     */
    @Override
    public void onDestroyView() {
        Log.v(TAG, String.format("onDestroyView %s", getTag()));
        super.onDestroyView();
    }

    /**
     * onCreate()에 대응되는 함수로 Fragment가 더이상 유효하지 않을 때 호출
     * 일반적으로 Fragment 자체 리소스를 해제하는 용도로 사용
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, String.format("onDestroy %s", getTag()));
        closeProgress();
        super.onDestroy();
    }

    /**
     * Fragment가 Activity와 연결이 끊어지는 상황에서 호출
     * 부모 Activity에서 Fragment의 참조를 가지고 있다면 해제하는 작업을 수행
     */
    @Override
    public void onDetach() {
        Log.v(TAG, String.format("onDetach %s", getTag()));
        super.onDetach();
    }


    /*
        INFO: UI 구현
     */

    /**
     * UI 구현
     */
    protected abstract void initUI();

    /**
     * UI 새로고침 구현
     */
    protected abstract void refreshUI();


    /*
        INFO: progress dialog
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
    // TODO consider to apply synchronized keyword
    public Dialog showProgress(View view, Drawable backGround) {
        if (_pDialog == null) {
            DialogView dv;
            if (view != null) {
                dv = new DialogView(_context, view, backGround);
            } else {
                dv = new DialogView(_context, DialogView.Size.small);
            }
            _pDialog = dv.getDialog();
        }
        if (_activity != null && !_activity.isFinishing() && !_pDialog.isShowing()) {
            try {
                _pDialog.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
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


    /*
        INFO: Fragment management
        TODO: 2016-02-17 Fragment stack 등 관리 목적 코드 필요
     */


}