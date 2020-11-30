package com.breakout.sample;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.Extra;
import com.breakout.sample.ui.IntroActivity;
import com.breakout.sample.views.AppBar;

import java.util.Arrays;
import java.util.List;


/**
 * <b>custom scheme - ://intro</b><p>
 * <b>uri -> ://intro?scheme=[스키마명]&ex_uri_msg=[약속된메세지]</b><p>
 * 외부에서 앱 진입시에 해당 uri의 query parameter를 분석 및 이동을 한다.<br>
 * manifest에 launchMoe가 singleTask로 설정 되어 있고 taskAffinity가 package name으로 되어 있어 기존 task가 존재한다면 해당 task로 합쳐진다.
 *
 * @author sung-gue
 * @version 1.0 (2016.04.05)
 */
public class UriActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTaskRoot();

        // 1. app의 task 존재 여부체크
        boolean aliveAppTask = false;
        try {
            String className = getClass().getName();
            String packageName = getPackageName();
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> procesesList = am.getRunningAppProcesses();
            List<RunningTaskInfo> taskList = am.getRunningTasks(procesesList.size());
            for (RunningTaskInfo info : taskList) {
                if (!TextUtils.isEmpty(packageName) && packageName.equals(info.baseActivity.getPackageName()) && !TextUtils.isEmpty(className) && !className.equals(info.baseActivity.getClassName())) {
                    aliveAppTask = true;
                    break;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        String[] hosts = new String[]{
                "target-host-name"
        };

        Intent intent = getIntent();
        // query에 scheme_host 존재하고 app의 task가 존재한다면 바로 scheme host로 이동
        if (!TextUtils.isEmpty(_uriSchemeHost) && Arrays.asList(hosts).contains(_uriSchemeHost)) {
//        if (aliveAppTask && !TextUtils.isEmpty(_uriSchemeHost)) {
            intent = new Intent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setData(Uri.parse(Const.APP_SCHEME + _uriSchemeHost));

            // TODO 2016-04-05 [modify] scheme에 따른 행동 정의 필요
            /*if (ActivityID.SH_JOIN.equals(_uriSchemeHost) || ActivityID.SH_LOGIN.equals(_uriSchemeHost)) {
                if (!TextUtils.isEmpty(_shared.getAuthKey()) ) intent.setClass(_context, MainActivity.class);
                else intent.setData(Uri.parse(Const.SCHEME + _uriSchemeHost));
            }
            else if (!TextUtils.isEmpty(_shared.getAuthKey())) {
                intent.setData(Uri.parse(Const.SCHEME + _uriSchemeHost));
            }
            else {
                intent.setClass(this, LoginActivity.class);
            }*/
        }
        // app의 task가 존재하지 않고 scheme host도 없는경우에는 IntroActivity로 이동
        else {
            intent.setClass(this, IntroActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // manifest에 launchMode와 taskAffinity로 task분기 없이 기존 task와 결합하기 때문에 사용하지 않음
        }

        // uri query param을 intent에 담는다.
        intent.putExtra(Extra.EX_URI_SCHEME_HOST, _uriSchemeHost);
        intent.putExtra(Extra.EX_URI_MSG, _uriMsg);

        startActivity(intent);
        finish();
    }


    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
    }

    @Override
    protected void refreshUI() {
    }
}