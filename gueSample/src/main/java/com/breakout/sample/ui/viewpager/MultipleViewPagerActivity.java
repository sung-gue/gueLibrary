package com.breakout.sample.ui.viewpager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.controller.NaverImageController;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.views.AppBar;

import java.util.ArrayList;

public class MultipleViewPagerActivity extends BaseActivity implements OnRequestListener {
    private ViewPager2 _viewPager;

    private MultipleViewPagerViewModel _viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(MultipleViewPagerViewModel.class);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.ui_base_layout);

        _viewPager = new ViewPager2(this);
        super.setBodyView(_viewPager);

        _viewModel.getInitImageList().observe(this, new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> items) {
            }
        });
        _viewModel.getUpdateImageList().observe(this, new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> items) {
            }
        });
        _viewModel.getPagerTab().observe(this, new Observer<PagerTab>() {
            @Override
            public void onChanged(PagerTab pagerTab) {
            }
        });
        _viewModel.getGridLinkPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                _viewPager.setCurrentItem(PageAdapter.PAGER, true);
            }
        });
        _viewModel.getPagerLinkPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
            }
        });

        super.initUI();
        refreshUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        _viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        _viewPager.setOffscreenPageLimit(2);
        /*View view = _viewPager.getChildAt(0);
        if (view instanceof RecyclerView) {
            view.setOnTouchListener(this);
        }*/

        PageAdapter mainPageFragmentAdapter = new PageAdapter(this);
        _viewPager.setAdapter(mainPageFragmentAdapter);
        _viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!isFinishing()) {
                    if (position == PageAdapter.GRID) {
                        Integer pos = _viewModel.pagerLastPosition;
                        if (pos != null) {
                            _viewModel.setPagerLinkPosition(pos);
                        }
                    }

                }
            }
        });
        _viewPager.setCurrentItem(PageAdapter.PAGER, false);
    }

    @Override
    protected void refreshUI() {

    }

    @Override
    public void onRequest(Type sortType, int display, final int start) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                closeProgress();
                NaverImageDto dto = (NaverImageDto) msg.obj;
                if (!isFinishing() && dto.list != null && dto.list.size() > 0) {
                    if (start > 1) {
                        _viewModel.setUpdateImageList(dto.list);
                    } else {
                        _viewModel.setInitImageList(dto.list);
                    }
                }
                return true;
            }
        });
        if (start == 1) {
            showProgress();
        }
        String query = "여배우";
        switch (sortType) {
            case SORT_SIM:
                new NaverImageController(_context, handler).getImageListSortBySim(query, display, start);
                break;
            case SORT_DATE:
                new NaverImageController(_context, handler).getImageListSortByDate(query, display, start);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (_viewPager != null && _viewPager.getCurrentItem() == PageAdapter.GRID) {
            _viewPager.setCurrentItem(PageAdapter.PAGER, true);
        } else {
            super.onBackPressed();
        }
    }

    private static class PageAdapter extends FragmentStateAdapter {
        private final String TAG = getClass().getSimpleName();
        private static final int PAGER = 0;
        private static final int GRID = 1;
        private static final int PAGE_COUNT = 2;

        private SparseArray<Fragment> _fragments = new SparseArray<>();

        public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case PAGER:
                default:
                    fragment = new PagerFragment();
                    break;
                case GRID:
                    fragment = new GridFragment();
                    break;
            }
            _fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }

}
