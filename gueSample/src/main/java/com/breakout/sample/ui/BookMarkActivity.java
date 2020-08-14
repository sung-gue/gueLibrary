package com.breakout.sample.ui;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.views.AppBar;
import com.breakout.util.string.StringUtil;

import java.util.List;

public class BookMarkActivity extends BaseActivity {

    private LinearLayout _bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _bodyView = super.setEmptyContentView();
        _bodyView.setBackgroundColor(Color.TRANSPARENT);
        _bodyView.setGravity(Gravity.BOTTOM);

        super.initUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        final EditText etBookmarkName = EditText(_bodyView, "write bookmark name");
        final EditText etBookmarkUrl = EditText(_bodyView, "write url & uri");
        Button(_bodyView, "Add bookmark icon home").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String shortcutName = etBookmarkName.getText().toString();
                String url = etBookmarkUrl.getText().toString();
                if (StringUtil.nullCheckB(shortcutName) && StringUtil.nullCheckB(url)) {
                    Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
                    shortcutIntent.setAction(Intent.ACTION_VIEW);
                    shortcutIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                    shortcutIntent.setData(Uri.parse(url));
                    shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                    Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
                    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(_appContext, R.drawable.ic_heart));
                    intent.putExtra("duplicate", false);
                    sendBroadcast(intent);
                } else
                    Toast.makeText(_appContext, "please write shortcun name!!", Toast.LENGTH_SHORT).show();
            }
        });

        final EditText etPackage = EditText(_bodyView, "write app package name");
        Button(_bodyView, "Add app icon home").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = etPackage.getText().toString();
                if (StringUtil.nullCheckB(packageName)) {
                    Intent shortcutIntent = new Intent();
					/*shortcutIntent.setAction(Intent.ACTION_MAIN);
					shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					shortcutIntent.setClassName(_appContext, getClass().getName());*/
                    shortcutIntent.setAction(Intent.ACTION_MAIN);
                    shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shortcutIntent.setPackage(packageName);
                    List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(shortcutIntent, 0);
                    if (pkgAppsList == null || (pkgAppsList != null && pkgAppsList.size() == 0)) {
                        Toast.makeText(_appContext, "please write correct package name!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    shortcutIntent.setClassName(pkgAppsList.get(0).activityInfo.packageName, pkgAppsList.get(0).activityInfo.name);
                    shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                    Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, pkgAppsList.get(0).loadLabel(getPackageManager()));
                    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(_appContext, R.drawable.ic_heart));
                    intent.putExtra("duplicate", false);
                    sendBroadcast(intent);
                } else
                    Toast.makeText(_appContext, "please write package name!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void refreshUI() {
    }

}
