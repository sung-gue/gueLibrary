package com.breakout.sample.test.baseact;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.util.FragmentEx;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.widget.DialogView;

import java.util.Locale;

/**
 * @author sung-gue
 * @version 1.0 (2020-11-24)
 */
public abstract class FragmentEx1<SHARED extends SharedDataEx, BASE_ACTIVITY extends ActivityEx<SHARED>, T extends FragmentEx1.OnFragmentActionListener> extends FragmentEx {
    /**
     * Fragment에서 Activity로 전달할 사항이 있을경우 구현하여 사용
     * <pre>
     *     public interface OnSampleFragmentActionListener extends OnFragmentActionListener {
     *         enum ActionType implements FragmentActionType {
     *             COMPLETE_GUIDE
     *         }
     *         void onFragmentAction(ActionType actionType, Bundle bundle);
     *     }
     * </pre>
     *
     * @see FragmentActionType
     */
    public interface OnFragmentActionListener {
    }

    /**
     * OnFragmentActionListener 에서 type에따라 전달할 내용이 달라진다면 구현하여 사용
     */
    public interface FragmentActionType {
    }

    protected SHARED _shared;
    protected ImageLoader _imageLoader;
    private T _onFragmentActionListener;
    protected BASE_ACTIVITY _baseActivity;
    protected int _containerId;

    public FragmentEx1() {
        super();
    }

    protected abstract SHARED getSharedInstance(Context appContext);


    /*
        INFO: fragment life cycle
     */

    /**
     * Fragment가 Activity에 최초로 연결될 때 호출
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            _baseActivity = (BASE_ACTIVITY) _context;
        } catch (Exception e) {
            throw new ClassCastException(_context.toString() + " activity must extends BaseActivity");
        }
        //_shared = (SHARED) IMSharedData.getInstance(_appContext);
        _shared = getSharedInstance(_appContext);
        _imageLoader = ImageLoader.getInstance(_appContext);
        _imageLoader.setsdErrStr(getString(R.string.al_sdcard_strange_condition));
    }

    /**
     * listener을 사용할 경우 상속받은 클래스는 onAttach() 에서 호출하여 사용
     */
    @SuppressWarnings("unchecked")
    protected T initActionListener() {
        try {
            if (_context instanceof OnFragmentActionListener) {
                _onFragmentActionListener = (T) _context;
            }
        } catch (Exception e) {
            throw new ClassCastException(_context.toString() + " activity must implement OnFragmentActionListener");
        }
        return _onFragmentActionListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (container != null) {
            _containerId = container.getId();
        }
        return view;
    }

    /**
     * fragment가 스크롤을 가질경우 최상위 위치로 이동가능 하도록 구현
     */
    public void scrollTop() {
    }

    public void refresh() {
        refreshUI();
    }


    /*
        INFO: intent
     */

    /**
     * startActivity check
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Log.i(TAG, String.format(
                Locale.getDefault(),
                "-------------------------------------------------\n" +
                "%s | startActivity\n" +
                "|  %s\n" +
                "|  requestCode : %d\n" +
                "|  component : %s\n" +
                "-------------------------------------------------",
                TAG, intent, requestCode, intent != null ? intent.getComponent() : ""
        ));
        super.startActivityForResult(intent, requestCode);
        if (_baseActivity != null) {
            _baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (_baseActivity != null) {
            _baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void fragmentTransactionCommit(int containerViewId, Fragment fragment, String tag, boolean useBackStack, String backStackName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        if (useBackStack) {
            fragmentTransaction.addToBackStack(backStackName);
        }
        fragmentTransaction.commit();
    }

    /*
        INFO: progress dialog
     */
    private Dialog _pDialog;

    @Override
    public Dialog showProgress() {
        if (_pDialog == null) {
            ProgressBar progressBar = new ProgressBar(_context, null, DialogView.Size.medium.defStyle);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(
                    _context,
                    R.color.progress_swipeRefreshLayout
            ), PorterDuff.Mode.MULTIPLY); //PorterDuff.Mode.SRC_IN
            DialogView dv = new DialogView(_context, progressBar, null);
//            DialogView dv = new DialogView(_context, new ProgressBar(_context, null, DialogView.Size.medium.defStyle), null);
//            DialogView dv = new DialogView(_context, new ProgressBar(_context, null, DialogView.Size.medium.defStyle), null);
//            DialogView dv = new DialogView(_context, DialogView.Size.small);
            _pDialog = dv.getDialog(false, false);
        }
        if (_baseActivity != null && !_baseActivity.isFinishing() && !_pDialog.isShowing()) {
            try {
                _pDialog.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return _pDialog;
    }

    @Override
    public void closeProgress() {
        if (_pDialog != null && _pDialog.isShowing()) {
            _pDialog.dismiss();
            _pDialog = null;
        }
    }


    /*
        INFO: option menu
     */

    /**
     * Activity의 option menu를 변경할 경우 작성
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            /*
            menu.clear();
            inflater.inflate(R.menu.menu_main, menu);
            */
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
}
