package com.breakout.sample.test;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(null, false);

        super.initUI();
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        _vParent.setBackgroundColor(0xffebebeb);
        _vParent.setGravity(Gravity.TOP | Gravity.LEFT);
        TextView tv = TextView(_vParent, "html str test");
//		TextView tv = new TextView(this);
        tv.setTextColor(Color.parseColor("#ff000000"));
        tv.setLayoutParams(getMarginLayoutParams(new LinearLayout.LayoutParams(-2, -2), 10, 10, 10, 10));
        String str = "<p>hi~ !!</p>" +
                "<p>hi~ !!</p>" +
                "<p>last update : 2013-11-20 5:35</p>" +
                "<p style='text-align: center; float: none; clear: none; font-weight: bold; color: red;'> 	download APK file </p>" +
                "<p > <font color='red'>	download APK file </fonr></p>" +
                "<p ><em> <font color='green'>	download APK file </fonr> </em></p>" +
                "" +
                "" +
                "<hr width='100%'/><br/>";
        tv.setText(Html.fromHtml(str));
//		_vParent.addView(tv);

        TextView tvHtml = TextView(_vParent, "icon text test");
        ImageGetter imageGetter = new ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int resourceId = 0;
                Drawable drawable = null;
                if ("(놀람)".equals(source)) resourceId = R.drawable.e_006;
                else if ("(대표아이콘)".equals(source)) resourceId = R.mipmap.ic_launcher;
                if (resourceId != 0) {
                    try {
                        drawable = getResources().getDrawable(resourceId);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                return drawable;
            }
        };
        Spanned span = Html.fromHtml("<img src=\"(가나)\" style=\"width:50px;height:50px;\" />나 놀랐니? (놀람) <img src=\"(놀람)\" style=\"width:50px;height:50px;\" /> 응? <br/> 정말 그런가??? ㅋ<br/> <img src=\"(대표아이콘)\"  style=\"width:50px;height:50px;\" />", imageGetter, null);
        tvHtml.setText(span);
        tvHtml.setBackgroundColor(Color.BLUE);
    }

    @Override
    protected void refreshUI() {
    }

}
