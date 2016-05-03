package com.breakout.sample;

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

import com.breakout.sample.device.flash.FlashActivity;
import com.breakout.sample.hybrid.phonegap.PhonegapActivity;
import com.breakout.sample.image_filter_gue.ImageSelectActivity;
import com.breakout.sample.sqlite.SQLiteActivity;
import com.breakout.sample.test.TestActivity;
import com.breakout.sample.theme.ThemeActivity;
import com.breakout.sample.ui.BookMarkActivity;
import com.breakout.sample.ui.DialogActivity;
import com.breakout.sample.ui.NotificationActivity;
import com.breakout.sample.web.WebView2Activity;
import com.breakout.sample.web.WebViewActivity;
import com.breakout.sample.xmpp.ChatTestActivity;
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
        /*dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);*/
        super.onCreate(savedInstanceState);
        setContentView("Android Sample Project of gue", true);

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
    protected void initTitle() {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        Button(_vParent, "Device Info").setOnClickListener(new OnClickListener() {
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

        TextView(_vParent, "use gueUtil labrary");
        Button(_vParent, "SQLite").setOnClickListener(getMoveActivityListener(SQLiteActivity.class));
        Button(_vParent, "Image Filter gue").setOnClickListener(getMoveActivityListener(ImageSelectActivity.class));
        Button(_vParent, "Dialog").setOnClickListener(getMoveActivityListener(DialogActivity.class));
        Button(_vParent, "WebView").setOnClickListener(getMoveActivityListener(WebViewActivity.class));
        Button(_vParent, "WebView2").setOnClickListener(getMoveActivityListener(WebView2Activity.class));
        Button(_vParent, "Theme").setOnClickListener(getMoveActivityListener(ThemeActivity.class));
        Button(_vParent, "Bookmark").setOnClickListener(getMoveActivityListener(BookMarkActivity.class));
        Button(_vParent, "Notification").setOnClickListener(getMoveActivityListener(NotificationActivity.class));
        Button(_vParent, "Hybrid - phonegap").setOnClickListener(getMoveActivityListener(PhonegapActivity.class));
        Button(_vParent, "Flash").setOnClickListener(getMoveActivityListener(FlashActivity.class));

        /*TextView(_vParent, " - ");*/
        /*Button(_vParent, "").setOnClickListener(getMoveActivityListener(.class));*/
    }

    private void setOpenSourceBtn() {
        TextView(_vParent, "Open Source Test");
        Button(_vParent, "XMPP Chat, aSmack 4.0.2").setOnClickListener(getMoveActivityListener(ChatTestActivity.class));
        /*Button(_vParent, ""		).setOnClickListener(getMoveActivityListener(.class));*/
    }

    private void setTestSourceBtn() {
        TextView(_vParent, "Test Source");
        Button(_vParent, "TextView Html").setOnClickListener(getMoveActivityListener(TestActivity.class));
    }

    @Override
    protected void refreshUI() {
    }
    
	
/* ************************************************************************************************
 * INFO listener setting
 */

	
/* ************************************************************************************************
 * INFO callBack method
 */ 

	
/* ************************************************************************************************
 * INFO option & context menu
 */


/* ************************************************************************************************
 * INFO life cycle
 */

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


/* ************************************************************************************************
 * INFO test source
 */

    /**
     * // Serializable class : write & read test
     *
     * @author gue
     * @history <ol>
     * <li>변경자/날짜 : 변경사항</li>
     * </ol>
     * @since 2013. 10. 1.
     */
    void execSerializable() {
//        writeObj();
//        readObj();
    }

    /**
     * Serializable class : write & read test
     *
     * @author gue
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
     *
     * @author gue
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