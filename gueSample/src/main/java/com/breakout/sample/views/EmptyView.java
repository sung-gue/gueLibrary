package com.breakout.sample.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.breakout.sample.Log;
import com.breakout.sample.R;


public class EmptyView extends LinearLayout {
    protected final String TAG = getClass().getSimpleName();
    private Context _context;
    private ImageView _ivImage;
    private TextView _tvDesc;

    private String _msg;
    private Drawable _drawable;
    private int _ivHeight = -2;


    public EmptyView(Context context) {
        super(context);
        init(null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmptyView(Context context, String msg) {
        super(context);
        _msg = msg;
        init(null);
    }

    public EmptyView(Context context, int msgResId) {
        this(context, context.getString(msgResId));
    }

    public EmptyView(Context context, Drawable imgDrawable, String msg) {
        super(context);
        _drawable = imgDrawable;
        _msg = msg;
        init(null);
    }

    public EmptyView(Context context, int imgResId, int msgResId) {
        this(context, ContextCompat.getDrawable(context, imgResId), context.getString(msgResId));
    }

    public EmptyView(Context context, int imgHeight, Drawable imgDrawable, String msg) {
        super(context);
        _drawable = imgDrawable;
        _msg = msg;
        init(null);
    }

    public EmptyView(Context context, int imgHeight, int imgResId, int msgResId) {
        this(context, imgHeight, ContextCompat.getDrawable(context, imgResId), context.getString(msgResId));
    }


    private void init(AttributeSet attrs) {
        _context = getContext();
        LayoutInflater.from(_context).inflate(R.layout.v_empty, this, true);
        _ivImage = findViewById(R.id.ivEmptyImage);
        _tvDesc = findViewById(R.id.tvEmtyDesc);

        if (attrs != null) {
            TypedArray type = _context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
            _drawable = type.getDrawable(R.styleable.EmptyView_image);
            _ivHeight = type.getDimensionPixelSize(R.styleable.EmptyView_imageHeight, _ivHeight);
            _msg = type.getString(R.styleable.EmptyView_description);
            //_btName = type.getString(R.styleable.EmptyView_btName);
            type.recycle();
        }
        setContents();
    }

    private void setContents() {
        setEmptyImage(_drawable);
        setEmptyDesc(_msg);
    }

    public void setEmptyImage(int resId) {
        Drawable drawable = null;
        try {
            drawable = ContextCompat.getDrawable(_context, resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        setEmptyImage(drawable);
    }

    public void setEmptyImage(Drawable drawable) {
        _drawable = drawable;
        if (drawable != null) {
            _ivImage.setImageDrawable(drawable);
            _ivImage.setColorFilter(Color.argb(255, 0, 0, 0));
            _ivImage.setVisibility(View.VISIBLE);
        } else {
            _ivImage.setVisibility(View.GONE);
        }
        _ivImage.getLayoutParams().height = _ivHeight;
    }

    public void setEmptyDesc(int resId) {
        String msg = null;
        try {
            msg = _context.getString(resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        setEmptyDesc(msg);
    }

    public void setEmptyDesc(String msg) {
        _msg = msg;
        if (!TextUtils.isEmpty(msg)) {
            _tvDesc.setText(msg);
            _tvDesc.setVisibility(View.VISIBLE);
        } else {
            _tvDesc.setVisibility(View.GONE);
        }
    }

    public TextView getTextView() {
        return _tvDesc;
    }

    public ImageView getImageView() {
        return _ivImage;
    }

}