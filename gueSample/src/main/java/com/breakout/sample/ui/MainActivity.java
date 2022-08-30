package com.breakout.sample.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.device.flash.FlashActivity;
import com.breakout.sample.image_filter_gue.ImageSelectActivity;
import com.breakout.sample.storage.SQLiteActivity;
import com.breakout.sample.test.HtmlTestActivity;
import com.breakout.sample.theme.ThemeActivity;
import com.breakout.sample.ui.recyclerview.ListActivity;
import com.breakout.sample.ui.viewpager.MultipleViewPagerActivity;
import com.breakout.sample.views.AppBar;
import com.breakout.sample.web.WebView2Activity;
import com.breakout.sample.web.WebViewActivity;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.dto.media.ImageDTO;
import com.breakout.util.widget.DialogView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*  api version < 11
            Android Heap Memory Management, Out of memory
            dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
        */
        super.onCreate(savedInstanceState);
        super.registerfinishReceiver(ReceiverName.FINISH);
        super.setContentView(R.layout.ui_base_layout);
        super.setBaseBodyView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.super.initUI();
                // 즉시 이동
//                startActivity(new Intent(_appContext, PhonegapActivity.class));
            }
        }, 100);

        execSerializable();
    }

    @Override
    protected void initTitle(AppBar appBar) {
        appBar.setVisibility(View.VISIBLE);
        appBar.fixAppBarLocation(false)
                .setHomeIcon(false)
                .setCustomTitle()
                .setTitle("Sample Project of gue")
//                .setIcon(android.R.drawable.ic_menu_share)
//                .setTabLayout(false)
        ;
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        makeIntentView(Button(_bodyView, "ViewPager2 1"), MultipleViewPagerActivity.class);
        makeIntentView(Button(_bodyView, "ViewPager2 2"), com.breakout.sample.ui.viewpager.viewpager2.MultipleViewPagerActivity.class);
        makeIntentView(Button(_bodyView, "ViewPager vertical"), com.breakout.sample.ui.viewpager.viewpager3.MultipleViewPagerActivity.class);
        makeIntentView(Button(_bodyView, "RecyclerView"), ListActivity.class);

        Button(_bodyView, "Device Info").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.BLACK);
                gd.setCornerRadius(20);
                gd.setAlpha(200);

                TextView tv = TextView(null, DeviceUtil.getDeviceInfo(_appContext));
                tv.setTextColor(ColorStateList.valueOf(Color.WHITE));
                tv.setHorizontallyScrolling(true);
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.setLayoutParams(new LayoutParams(-1, -1));
                DialogView dv = new DialogView(_context, tv, gd);
                dv.getDialog(true).show();
            }
        });

        setTestSourceBtn();
        setOpenSourceBtn();

        TextView(_bodyView, "use gueUtil labrary");
        makeIntentView(Button(_bodyView, "SQLite"), SQLiteActivity.class);
        makeIntentView(Button(_bodyView, "Image Filter gue"), ImageSelectActivity.class);
        makeIntentView(Button(_bodyView, "Dialog"), DialogActivity.class);
        makeIntentView(Button(_bodyView, "WebView"), WebViewActivity.class);
        makeIntentView(Button(_bodyView, "WebView2"), WebView2Activity.class);
        makeIntentView(Button(_bodyView, "Theme"), ThemeActivity.class);
        makeIntentView(Button(_bodyView, "Bookmark"), BookMarkActivity.class);
        makeIntentView(Button(_bodyView, "Notification"), NotificationActivity.class);
        makeIntentView(Button(_bodyView, "Flash"), FlashActivity.class);

        /*TextView(_vParent, " - ");*/
        /*makeIntentView(Button(_vParent, ""), Activity.class);*/
    }

    private void setOpenSourceBtn() {
        TextView(_bodyView, "Open Source Test");
        /*makeIntentView(Button(_vParent, ""), Activity.class);*/
    }

    private void setTestSourceBtn() {
        TextView(_bodyView, "Test Source");
        makeIntentView(Button(_bodyView, "TextView Html"), HtmlTestActivity.class);
    }

    @Override
    protected void refreshUI() {
    }


    private boolean _finish;

    @Override
    public void onBackPressed() {
        if (!isFinishing() && _finish) {
            super.onBackPressed();
        } else if (!isFinishing() && !_finish) {
            _finish = true;
            Toast.makeText(_appContext, "한번더 누르면 종료!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _finish = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 화면이 완전하게 그려진 후에 call
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    /* ------------------------------------------------------------
        test source
     */

    /**
     * Serializable class : write & read test
     */
    void execSerializable() {
//        writeObj();
//        readObj();
    }

    /**
     * Serializable class : write & read test
     */
    void writeObj() {
        try {
            FileOutputStream f = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "obj"));
            ObjectOutput out = new ObjectOutputStream(f);
            out.writeObject("Today");
            out.writeObject(new Date());
            ArrayList<ImageDTO> list = new ArrayList<ImageDTO>();
            for (int i = 0; i < 10; i++) {
                ImageDTO dto = new ImageDTO();
                dto._count = "1234 : " + i;
                dto._data = "data : " + i;
                dto.bucket_count = 5678 + i;
                list.add(dto);
            }
            out.writeObject(list);
//            ImageDTO dto = new ImageDTO();
//            dto._count = "1234";
//            dto._data = "data";
//            dto.bucket_count = 5678;
//            out.writeObject(dto);
            out.flush();
            out.close();

        } catch (IOException e) {
        }
    }

    /**
     * Serializable class : write & read test
     */
    @SuppressWarnings("unchecked")
    void readObj() {
        try {
            FileInputStream in = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "obj"));
            ObjectInput out = new ObjectInputStream(in);
            String today = (String) out.readObject();
            Date date = (Date) out.readObject();
            ArrayList<ImageDTO> list = (ArrayList<ImageDTO>) out.readObject();
            System.out.println("_____________");
            System.out.println("String : " + today);
            System.out.println("date : " + date);
            for (int i = 0; i < list.size(); i++) {
                ImageDTO dto = list.get(i);
                System.out.println(i + ". dto _count : " + dto._count);
                System.out.println(i + ". dto _data : " + dto._data);
                System.out.println(i + ". dto bucket_count : " + (dto.bucket_count + 1));
            }
            System.out.println("_____________");
//            ImageDTO dto = (ImageDTO) out.readObject();
//            System.out.println("_____________");
//            System.out.println("String : " + today);
//            System.out.println("date : " + date);
//            System.out.println("dto _count : " + dto._count);
//            System.out.println("dto _data : " + dto._data);
//            System.out.println("dto bucket_count : " + (dto.bucket_count + 1));
//            System.out.println("_____________");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}