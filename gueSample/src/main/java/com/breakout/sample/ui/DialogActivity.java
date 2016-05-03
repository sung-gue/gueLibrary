package com.breakout.sample.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.util.Util;
import com.breakout.util.img.ImageAlter;
import com.breakout.util.widget.CV_Tv2;
import com.breakout.util.widget.DialogView;
import com.breakout.util.widget.DialogView.Size;

public class DialogActivity extends BaseActivity {
    private Dialog _dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("Dialog", true);
        _vParent.setBackgroundColor(0x55FF0000);

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
        TextView(_vParent, "ProgressBar circle");
        setBtProgressBar();

        TextView(_vParent, "Drawable");
        setBtDrawable();

        TextView(_vParent, "Add View {Custom view or Layout inflate)");
        setBtAddView();

        TextView(_vParent, "Dialog Animation of android.R.style");
        setBtDialogAnimation();

        TextView(_vParent, "Dialog Animation of custom style");
        setBtDialogCustomAnimation();
    }

    @Override
    protected void refreshUI() {
    }


    private void setBtProgressBar() {
        Button(_vParent, "small : cancel true").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, Size.small);
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "medium, dimBehind true").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, Size.medium);
                _dialog = dv.getDialog(true, true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "circle large, cancel false").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, Size.large);
                _dialog = dv.getDialog();
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
    }

    private void setBtDrawable() {
        Button(_vParent, "use xml : animation-list").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getResources().getDrawable(android.R.drawable.ic_popup_sync));
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "use xml : shape drawable").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getResources().getDrawable(R.drawable.spinner_ring));
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "use xml : animated-rotate drawable").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getResources().getDrawable(R.drawable.spinner_png));
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "resource drawable").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getResources().getDrawable(R.drawable.ic_heart));
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "java code : AnimationDrawable 1").setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                AnimationDrawable ad = new AnimationDrawable();
                ad.setOneShot(false);
                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_heart)).getBitmap();
                for (int i = 0; i < 360; i += 60) {
                    ad.addFrame(new BitmapDrawable(ImageAlter.getRotateBitmap(bitmap, i, false)), 200);
                }
                DialogView dv = new DialogView(_context, ad);
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "java code : AnimationDrawable 2").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationDrawable ad = new AnimationDrawable();
                ad.setOneShot(false);
                ad.addFrame(getResources().getDrawable(R.drawable.dialog_guide01), 1000);
                ad.addFrame(getResources().getDrawable(R.drawable.dialog_guide02), 1000);
                DialogView dv = new DialogView(_context, ad);
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
    }

    private void setBtAddView() {
        Button(_vParent, "ImageView").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = new ImageView(_context);
                iv.setImageDrawable(getResources().getDrawable(R.drawable.dialog_guide01));
                iv.setScaleType(ScaleType.FIT_CENTER);

                DialogView dv = new DialogView(_context, iv);
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Custom view").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CV_Tv2 view = new CV_Tv2(_context);
                view.setTitle("CV_Tv2");
                view.setMsg("내용입니다. 내용입니다. 내용입니다. 내용입니다...... 내용입니다. 내용입니다. 내용입니다. 내용입니다......");
                DialogView dv = new DialogView(_context, view);
                _dialog = dv.getDialog(true);
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Layout inflate").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
                Util.logViewHierachy(_dialog.getWindow().getDecorView(), 0);
            }
        });
    }

    private void setBtDialogAnimation() {
        Button(_vParent, "Animation (default)").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Animation_Activity").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Activity;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Animation_InputMethod").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_InputMethod;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Animation_Toast").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Animation_Translucent").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Translucent;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
    }

    private void setBtDialogCustomAnimation() {
        Button(_vParent, "Custom Animation : zoom").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_Zoom;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
        Button(_vParent, "Custom Animation : slide right in").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogView dv = new DialogView(_context, getInflateView());
                _dialog = dv.getDialog();
                _dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_SlideRight;
                if (_dialog != null && !_dialog.isShowing()) _dialog.show();
            }
        });
    }

    private View getInflateView() {
        final View view = LayoutInflater.from(_context).inflate(R.layout.dialog_alert, null);
        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        TextView msg = (TextView) view.findViewById(R.id.tvMsg);
        title.setText("R.layout.alert_dialog");
        title.setVisibility(View.VISIBLE);
        msg.setText("잠시만 기다려주세요...... 잠시만 기다려주세요...... 잠시만 기다려주세요...... 잠시만 기다려주세요......");
        msg.setSelected(true);
        ((Button) view.findViewById(R.id.btOk)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(_context, "확인!!", Toast.LENGTH_SHORT).show();
            }
        });
        ((Button) view.findViewById(R.id.btCancel)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(_context, "취소!!", Toast.LENGTH_SHORT).show();
                if (_dialog != null && _dialog.isShowing()) _dialog.dismiss();
            }
        });
        view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        return view;
    }

	

	
	
/* ************************************************************************************************
 * TODO listener setting
 */
	
	
/* ************************************************************************************************
 * TODO callBack method
 */ 
	
	
/* ************************************************************************************************
 * TODO option & context menu
 */ 
	
	
/* ************************************************************************************************
 * TODO life cycle
 */
    /**
     * 해당 frame을 start하기 위한 member value
     */
    private AnimationDrawable _ad;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (_ad != null) _ad.start();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
            _ad = null;
        }
        super.onPause();
    }
}
