package com.breakout.sample.ui.recyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.controller.NaverImageController;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.listener.EndlessRecyclerViewScrollListener;
import com.breakout.sample.views.AppBar;
import com.breakout.sample.views.EmptyView;
import com.breakout.sample.views.GridSpacingItemDecoration;
import com.breakout.util.widget.ViewUtil;

import java.util.ArrayList;

public class ListActivity extends BaseActivity implements BaseActivity.SwipeRefreshLayoutListener {

    private RecyclerView _rvList;
    private EmptyView _emptyView;

    private ListAdapter _adapter;
    private EndlessRecyclerViewScrollListener _scrollListener;

    private ArrayList<NaverImageDto.Item> _list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.registerfinishReceiver(ReceiverName.FINISH_EXCLUDE_MAIN);
        super.setContentView(R.layout.ui_base_layout);
        super.setBodyView(R.layout.v_list);

        super.initUI();
        refreshUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
        appBar.setVisibility(View.VISIBLE);
        appBar.setTabLayout(false)
                .fixAppBarLocation(false)
                .setHomeIcon(true)
//                .setCustomTitle()
                .setTitle(TAG)
//                .setIcon(R.drawable.ic_)
        ;
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        TextView header = new TextView(_context);
        header.setText(TAG);
        header.setBackgroundColor(ContextCompat.getColor(_context, R.color.gold));
        header.setLayoutParams(new ViewGroup.LayoutParams(-1, (int) ViewUtil.dp2px(50, _context)));
        header = null;

        _emptyView = findViewById(R.id.emptyView);
        _emptyView.setEmptyImage(android.R.drawable.ic_menu_agenda);
        _emptyView.setEmptyDesc(R.string.no_list);

        /*
            RecyclerView init
         */
        _rvList = findViewById(R.id.rvList);
        int margin = (int) ViewUtil.dp2px(0, _context);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) _rvList.getLayoutParams();
        params.setMargins(margin, 0, margin, 0);

        /*
            RecyclerView cell spacing
         */
        float spacing = 1.5f * _context.getResources().getDisplayMetrics().density + 0.5f;
        _rvList.addItemDecoration(new GridSpacingItemDecoration(ListAdapter.SPAN_COUNT, (int) spacing, false));

        /*
            RecyclerView divider
         */
//        DividerItemDecoration divider = new DividerItemDecoration(_context, DividerItemDecoration.VERTICAL);
//        //divider.setDrawable(ContextCompat.getDrawable(_context, R.color.color_first));
//        divider.setDrawable(ContextCompat.getDrawable(_context, R.drawable.divider_1));
//        _rvList.addItemDecoration(divider);
//        //_rvList.setHasFixedSize(true);

        _adapter = new ListAdapter(_context, header, _list);
        _rvList.setLayoutManager(_adapter.getLayoutManager());
        _rvList.setAdapter(_adapter);
    }

    @Override
    public void initRefreshUI(SwipeRefreshLayout srlWrap) {
    }

    @Override
    protected void refreshUI() {
        _rvList.removeOnScrollListener(_scrollListener);
        _scrollListener = new EndlessRecyclerViewScrollListener(_adapter.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, String.format("%s on load more list | %s / %s", TAG, page, totalItemsCount));
                if (_adapter.page < page && _list.size() > 0) {
                    _adapter.page = page;
                    requestList(_list.size() + 1);
                }
            }
        };
        _rvList.addOnScrollListener(_scrollListener);
        _list.clear();
        _adapter.page = 0;
        _adapter.notifyDataSetChanged();
        requestList(1);
    }

    public void requestList(int start) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                NaverImageDto dto = (NaverImageDto) msg.obj;
                if (!isFinishing() && dto.list != null) {
                    setData(dto.list);
                }
                closeProgress();
                return true;
            }
        });
        if (_adapter.page == 0) {
            showProgress();
        }
        String query = "여배우";
        new NaverImageController(_context, handler).getImageListSortBySim(query, Const.DEFAULT_LIST_CNT, start);
    }

    private void setData(ArrayList<NaverImageDto.Item> list) {
        if (list != null && list.size() > 0) {
            _list.addAll(list);
            _rvList.post(() -> _adapter.notifyItemInserted(list.size()));

        }
        initEmptyView();
    }

    private void initEmptyView() {
        if (_list.size() == 0) {
            _emptyView.setVisibility(View.VISIBLE);
        } else {
            _emptyView.setVisibility(View.GONE);
        }
    }

}
