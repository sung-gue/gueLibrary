package com.breakout.sample.views;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.util.widget.TabLayoutEx;
import com.breakout.util.widget.ViewUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;


/**
 * Custom action bar<br/>
 * {@link AppCompatActivity}에서 사용되지 않는 경우 ClassCastException 을 발생시킨다.
 *
 * @author sung-gue
 * @version 1.0 (2016.02.24)
 */
public class AppBar extends AppBarLayout {
    private final String TAG = getClass().getSimpleName();

    /*
        TODO: 2019-12-16 변수명 정리 필요
     */
    private AppCompatActivity _activity;
    private Context _context;

    private CollapsingToolbarLayout _toolBarLayout;
    public Toolbar _toolbar;
    public ActionBar _actionBar;
    public TabLayoutEx _tabLayout;

    private TextView _tvTitle;

    private boolean _isFragment;
    private int _defaultScrollFlags;

    public AppBar(Context context) {
        super(context);
        init(null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        _context = getContext();
        LayoutInflater.from(_context).inflate(R.layout.ui_app_bar, this, true);

        _toolBarLayout = findViewById(R.id.toolbarLayout);
        _toolbar = findViewById(R.id.toolbar);
        _tabLayout = findViewById(R.id.tabLayout);

        if (!isInEditMode()) {
            if (_context instanceof AppCompatActivity) {
                LayoutParams params = (LayoutParams) _toolBarLayout.getLayoutParams();
                _defaultScrollFlags = params.getScrollFlags();
                _activity = (AppCompatActivity) _context;
                _activity.setSupportActionBar(_toolbar);
                _actionBar = _activity.getSupportActionBar();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    float elevation = getResources().getDimension(R.dimen.appbar_elevation);
                    StateListAnimator stateListAnimator = new StateListAnimator();
                    stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this, "elevation", elevation));
                    this.setStateListAnimator(stateListAnimator);
                    setElevation(elevation);
                }
            } else {
                throw new ClassCastException(_context.toString() + " must extends AppCompatActivity");
            }
        }
        if (attrs != null) {
            TypedArray type = _context.obtainStyledAttributes(attrs, R.styleable.AppBar);
            String titleStr = type.getString(R.styleable.AppBar_titleStr);
            if (!TextUtils.isEmpty(titleStr)) {
                setTitle(titleStr);
            }
            Drawable titleIcon = type.getDrawable(R.styleable.AppBar_titleIcon);
            if (titleIcon != null) {
                _actionBar.setIcon(titleIcon);
            }
            boolean useTabs = type.getBoolean(R.styleable.AppBar_useTabs, true);
            setTabLayout(useTabs);

            int primaryColor = ViewUtil.getThemeColor(_context, "colorPrimary", R.attr.colorPrimary);
            setBackgroundColor(type.getColor(R.styleable.AppBar_bgColor, primaryColor));
            type.recycle();
        }
    }

    public AppBar setScrollFlags(int flags) {
        LayoutParams params = (LayoutParams) _toolBarLayout.getLayoutParams();
        //params.setScrollFlags(LayoutParams.SCROLL_FLAG_SCROLL | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
        params.setScrollFlags(flags);
        return this;
    }

    public AppBar fixAppBarLocation() {
        return setScrollFlags(0);
    }

    public AppBar fixAppBarLocation(boolean fix) {
        if (fix) {
            return setScrollFlags(0);
        } else {
            return setScrollFlags(_defaultScrollFlags);
        }
    }


    public AppBar setIsFragmentUI(boolean isFragment) {
        _isFragment = isFragment;
        setHomeIcon();
        _activity.getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (_activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    setHomeIcon(true);
                } else {
                    setHomeIcon(false);
                }
            }
        });
        return this;
    }

    public String getTitle() {
        return String.valueOf(_actionBar.getTitle());
    }

    public AppBar setTitle(int titleId) {
        String title = null;
        try {
            title = _context.getString(titleId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return setTitle(title);
    }

    public AppBar setTitle(String title) {
        if (_tvTitle != null) {
            _tvTitle.setText(title);
        } else {
            _actionBar.setDisplayShowTitleEnabled(true);
            _actionBar.setTitle(title);
        }
        return this;
    }

    public AppBar setIcon(int iconResId) {
        _actionBar.setIcon(iconResId);
        return this;
    }

    /**
     * {@link Toolbar}의 navigationIcon을 사용
     */
    public AppBar setHomeIcon() {
        return setHomeIcon(true);
    }

    /**
     * {@link Toolbar}의 navigationIcon을 사용
     *
     * @param useBackIcon true일 경우 actionBar의 navigationIcon을 사용. click event는 onBackPressd()로 작동
     */
    public AppBar setHomeIcon(boolean useBackIcon) {
        return setHomeIcon(useBackIcon, R.drawable.ic_back_black, new OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.onBackPressed();

                // 이전 stack 으로
                /*NavUtils.navigateUpFromSameTask(this);*/

                // 다른 stack 구조에 있는지 검사 후 이전 스택으로
                /*Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }*/
            }
        });
    }

    /**
     * {@link Toolbar}의 navigationIcon을 사용
     *
     * @param useHomeIcon      the use home icon
     * @param homeIconResId    the home icon res id
     * @param homeIconListener the home icon listener
     */
    public AppBar setHomeIcon(boolean useHomeIcon, int homeIconResId, OnClickListener homeIconListener) {
        _actionBar.setDisplayHomeAsUpEnabled(useHomeIcon);
        if (useHomeIcon) {
            _actionBar.setHomeAsUpIndicator(homeIconResId);
            if (homeIconListener != null)
                _toolbar.setNavigationOnClickListener(homeIconListener);
        }
        return this;
    }

    public TabLayout getTabLayout() {
        return _tabLayout;
    }

    public AppBar setTabLayout(boolean use) {
        if (!use) {
//            removeView(_tabLayout);
            _tabLayout.setVisibility(View.GONE);
        } else {
//            addView(_tabLayout);
            _tabLayout.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public AppBar setTabSetupWithViewPager(ViewPager viewPager) {
        _tabLayout.setupWithViewPagerEx(viewPager);
        return this;
    }

    public AppBar setEditText(EditText editText) {
//        _actionBar.setDisplayShowTitleEnabled(true);
        _toolbar.addView(editText, new ViewGroup.LayoutParams(-1, -2));
        return this;
    }

    /**
     * use {@link Toolbar.LayoutParams}
     */
    public AppBar setCustomTitle(TextView textView) {
        _actionBar.setDisplayShowTitleEnabled(false);
        _tvTitle = textView;
        _toolbar.addView(textView);
        return this;
    }

    public AppBar setCustomTitle() {
        TextView textView = new TextView(_context);
        textView.setLayoutParams(new Toolbar.LayoutParams(-2, -2, Gravity.CENTER));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.AppBar_Title_TextStyle);
        } else {
            textView.setTextAppearance(_context, R.style.AppBar_Title_TextStyle);
        }
        textView.setGravity(Gravity.CENTER);
        /*textView.setTextColor(ContextCompat.getColor(_context, R.color.headerText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.appbarTextSize));
        textView.setSingleLine(true);
        textView.setClickable(true);
        textView.setSelected(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);*/

        return setCustomTitle(textView);
    }

    @Override
    public void setElevation(float elevation) {
        super.setElevation(elevation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*if (_toolbarTopLayout != null) {
                _toolbarTopLayout.setElevation(elevation);
            }
            if (_toolbar != null) {
                _toolbar.setElevation(elevation);
            }
            if (_actionBar != null) {
                _actionBar.setElevation(elevation);
            }
            if (_tabLayout != null) {
                _tabLayout.setElevation(elevation);
            }*/
        }
    }
}
