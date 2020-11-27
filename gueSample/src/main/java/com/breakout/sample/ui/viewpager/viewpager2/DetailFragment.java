package com.breakout.sample.ui.viewpager.viewpager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.breakout.sample.BaseFragment;
import com.breakout.sample.R;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.ui.viewpager.PageViewStatus;

import org.jetbrains.annotations.NotNull;

public class DetailFragment extends BaseFragment<DetailFragment.OnActionListener> {

    public interface OnActionListener extends BaseFragment.OnFragmentActionListener {
        enum ActionType implements FragmentActionType {
        }

        void onGoodsDetailFragmentAction(ActionType actionType, Bundle bundle, DetailFragment detailFragment);
    }


    public static String BR_VIEW_PAGER_PAGER_REFRESH = "BR_VIEW_PAGER_PAGER_REFRESH";
    public static String BR_VIEW_PAGER_PAGER_VIEW_TOGGLE = "BR_VIEW_PAGER_PAGER_VIEW_TOGGLE";
    private PageViewStatus _pageViewStatus;

    private NaverImageDto.Item _item;

    public DetailFragment() {
        super();
    }


    public DetailFragment(NaverImageDto.Item item, PageViewStatus goodsPageViewStatus) {
        _item = item;
        _pageViewStatus = goodsPageViewStatus;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
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
        View rootView = inflater.inflate(R.layout.c_naver_image, container, false);

        ImageView ivImage = rootView.findViewById(R.id.ivImage);
        TextView tvTitle = rootView.findViewById(R.id.tvTitle);

        tvTitle.setText(_item.title);
        _imageLoader.download(_item.imageUrl, ivImage, null, false);

        refreshUI();
        return rootView;
    }

    @Override
    protected void initUI() {
    }

    @Override
    public void refreshUI() {
    }

}
