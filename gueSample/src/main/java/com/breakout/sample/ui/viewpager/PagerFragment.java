package com.breakout.sample.ui.viewpager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.breakout.sample.BaseFragment;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.util.widget.ViewUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class PagerFragment extends BaseFragment<PagerFragment.OnActionListener> implements View.OnTouchListener, View.OnClickListener {

    public interface OnActionListener extends BaseFragment.OnFragmentActionListener {
        enum ActionType implements FragmentActionType {
        }

        void onMainPagerFragmentAction(ActionType actionType, Bundle bundle, PagerFragment pagerFragment);
    }

    private OnActionListener _onActionListener;
    private OnRequestListener _onRequestListener;
    public static String BR_VIEW_PAGER_PAGER_REFRESH = "BR_VIEW_PAGER_PAGER_REFRESH";

    private ViewPager2 _viewPager;
    protected View _headerLayer;
    protected LottieAnimationView _lottieView;
    protected PagerAdapter _adapter;

    private MultipleViewPagerViewModel _viewModel;
    private ArrayList<NaverImageDto.Item> _itemList;// = new ArrayList<>();

    private HashMap<PagerTab, TextView> _tabList = new HashMap<>();
    private PagerTab _pagerTab;

    protected PageViewStatus _pageViewStatus = new PageViewStatus();
    private GestureDetectorCompat _gestureDetector;


    public PagerFragment() {
        super();
        //setRetainInstance(true);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        _gestureDetector = new GestureDetectorCompat(context, _simpleOnGestureListener);
        _onActionListener = initActionListener();
        if (context instanceof OnRequestListener) {
            _onRequestListener = (OnRequestListener) context;
        } else {
            throw new ClassCastException(_context.toString() + " must implement OnRequestListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        _context.registerReceiver(_refreshReceiver, new IntentFilter(BR_VIEW_PAGER_PAGER_REFRESH));
    }

    private BroadcastReceiver _refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshUI();
        }
    };

    @Override
    public void onDestroy() {
        _context.unregisterReceiver(_refreshReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.v_multiple_pager, container, false);

        initViewPager(rootView);

        _headerLayer = rootView.findViewById(R.id.headerLayer);
        TextView tvMenu1 = rootView.findViewById(PagerTab.MENU1.id);
        tvMenu1.setOnClickListener(_onTabClickListener);
        _tabList.put(PagerTab.MENU1, tvMenu1);
        TextView tvMenu2 = rootView.findViewById(PagerTab.MENU2.id);
        tvMenu2.setOnClickListener(_onTabClickListener);
        _tabList.put(PagerTab.MENU2, tvMenu2);

        rootView.findViewById(R.id.ivBt1).setOnClickListener(this);
        _lottieView = rootView.findViewById(R.id.lottieView);
        return rootView;
    }

    protected void initViewPager(View rootView) {
        _viewPager = rootView.findViewById(R.id.viewPager);
        _viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        View view = _viewPager.getChildAt(0);
        if (view instanceof RecyclerView) {
            view.setOnTouchListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _viewModel = new ViewModelProvider(requireActivity()).get(MultipleViewPagerViewModel.class);
        _itemList = _viewModel.pagerImageList;
        _viewModel.getInitImageList().observe(getViewLifecycleOwner(), new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> goods) {
                initAdapter(_itemList);
            }
        });
        _viewModel.getUpdateImageList().observe(getViewLifecycleOwner(), new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> goods) {
                if (_adapter != null) {
                    _adapter.addItems(goods);
                }
            }
        });
        _viewModel.getGridLinkPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                if (_viewPager.getCurrentItem() != position) {
                    _viewPager.setCurrentItem(position, false);
                }
            }
        });
        _pagerTab = _viewModel.getPagerTab().getValue();
        initMainTab(_tabList.get(_pagerTab));
        refreshUI();
    }

    protected ViewPager2.OnPageChangeCallback _onPageChangeCallback;
    private final int CEHCK_MIN_GOODS_SIZE = 3;

    protected void initAdapter(final ArrayList<NaverImageDto.Item> list) {
        if (_baseActivity == null || _baseActivity.isFinishing()) {
            return;
        }
        _viewPager.setAdapter(null);
        _viewPager.unregisterOnPageChangeCallback(_onPageChangeCallback);

        _onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            int lastSize = 0;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (_baseActivity != null && !_baseActivity.isFinishing()) {
                    _viewModel.pagerLastPosition = position;
                    if (list != null && list.size() > lastSize && position == (list.size() - CEHCK_MIN_GOODS_SIZE)) {
                        lastSize = list.size();
                        switch (_pagerTab) {
                            case MENU1:
                            case MENU2:
                                _onRequestListener.onRequest(
                                        _pagerTab.type, Const.DEFAULT_PAGER_LIST_CNT, _itemList.size() + 1
                                );
                                break;
                        }
                    }
                }
            }
        };
        _adapter = new PagerAdapter(_context, list, _pageViewStatus);
        _viewPager.setAdapter(_adapter);
        _viewPager.registerOnPageChangeCallback(_onPageChangeCallback);
    }

    private PagerTab initMainTab(View v) {
        PagerTab currentTab = null;
        for (PagerTab tab : _tabList.keySet()) {
            TextView tv = _tabList.get(tab);
            if (v == tv) {
                currentTab = tab;
                tv.setSelected(true);
                tv.setTypeface(null, Typeface.BOLD);
            } else {
                tv.setSelected(false);
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }
        return currentTab;
    }

    @Override
    protected void initUI() {
    }

    @Override
    public void refreshUI() {
        refreshList();
    }

    private View.OnClickListener _onTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                _pagerTab = initMainTab(v);
                _viewModel.setPagerTab(_pagerTab);
            }
            refreshList();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBt1: {
                break;
            }
        }
    }

    /**
     * refresh & change list
     */
    private void refreshList() {
        switch (_pagerTab) {
            case MENU1:
            case MENU2:
                _onRequestListener.onRequest(
                        _pagerTab.type, Const.DEFAULT_PAGER_LIST_CNT, 1
                );
                break;
        }
    }

    GestureDetector.SimpleOnGestureListener _simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed");
            try {
                if (_baseActivity != null && !_baseActivity.isFinishing()) {
                    if (_pageViewStatus.isShowUI) {
                        _headerLayer.setVisibility(View.INVISIBLE);
                    } else {
                        _headerLayer.setVisibility(View.VISIBLE);
                    }
                    _pageViewStatus.isShowUI = !_pageViewStatus.isShowUI;
                    _adapter.notifyDataSetChanged();
                }
            } catch (Exception ignored) {
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap");
            if (_baseActivity != null && !_baseActivity.isFinishing()) {
                showLottieView(e);
            }
            return true;
        }
    };

    public void showLottieView(MotionEvent event) {
        DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
        final int width = (int) ViewUtil.dp2px(57, _context);
        final int height = (int) ViewUtil.dp2px(57, _context);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) _lottieView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) _lottieView.getLayoutParams();
        marginLayoutParams.width = width;
        marginLayoutParams.height = height;
        _lottieView.setScaleType(ImageView.ScaleType.CENTER);
        //_lottieView.setBackgroundColor(ContextCompat.getColor(_context, R.color.color_first));
        if (event != null) {
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            int leftMargin = (int) (event.getX() - width / 2);
            if (leftMargin < 0) {
                leftMargin = 0;
            } else if ((leftMargin + width) >= metrics.widthPixels) {
                leftMargin = metrics.widthPixels - width;
            }
            int topMargin = (int) (event.getY() - height / 2);
            if (topMargin < 0) {
                topMargin = 0;
            } else if ((topMargin + height) >= metrics.heightPixels) {
                topMargin = metrics.heightPixels - height;
            }
            marginLayoutParams.leftMargin = leftMargin;
            marginLayoutParams.topMargin = topMargin;
        } else {
            layoutParams.gravity = Gravity.CENTER;
            marginLayoutParams.topMargin = 0;
            marginLayoutParams.leftMargin = 0;
        }

        _lottieView.setVisibility(View.VISIBLE);
        _lottieView.playAnimation();
        _lottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                _lottieView.setVisibility(View.GONE);
            }
        });

        /*LottieAnimationView view = new LottieAnimationView(_context);
        view.setAnimation("29470-heart-animated.json");
        view.setScaleType(ImageView.ScaleType.CENTER);
        //view.setBackgroundColor(ContextCompat.getColor(_context, R.color.color_first));
        view.setRepeatMode(LottieDrawable.RESTART);
        view.setRepeatCount(0);

        final int size = (int) ViewUtil.dp2px(130, _context);
        final PopupWindow popupWindow = new PopupWindow(view, size, size);
        //final PopupWindow popupWindow = new PopupWindow(view, -2, -2);
        if (event != null) {
            popupWindow.showAtLocation(holder.ivGoodsImage, Gravity.TOP | Gravity.START, (int) (event.getX() - size / 2), (int) (event.getY() - size / 2));
        } else {
            popupWindow.showAtLocation(holder.ivGoodsImage, Gravity.CENTER, 0, 0);
        }

        view.playAnimation();
        view.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                popupWindow.dismiss();
            }
        });*/
    }


    /* ------------------------------------------------------------
        GestureDetector
     */

    private String _TAG = "GestureDetector";

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(_TAG, "Activity dispatchTouchEvent : " + event);
        //_gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(_TAG, "Activity onTouchEvent : " + event);
        //_gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.i(_TAG, "OnTouchListener onTouch : " + v + " / " + event);
        _gestureDetector.onTouchEvent(event);
        return false;
    }

}
