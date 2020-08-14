package com.breakout.sample.ui.viewpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.BaseFragment;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.listener.EndlessRecyclerViewScrollListener;
import com.breakout.sample.views.EmptyView;
import com.breakout.sample.views.GridSpacingItemDecoration;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.widget.ViewUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GridFragment extends BaseFragment<GridFragment.OnActionListener> {

    public interface OnActionListener extends BaseFragment.OnFragmentActionListener {
        enum ActionType implements FragmentActionType {
        }

        void onGridFragmentAction(ActionType actionType, Bundle bundle, GridFragment gridFragment);
    }

    private OnActionListener _onActionListener;
    private OnRequestListener _onRequestListener;
    public static String BR_VIEW_PAGER_GRID_REFRESH = "BR_VIEW_PAGER_GRID_REFRESH";

    private MultipleViewPagerViewModel _viewModel;
    private ArrayList<NaverImageDto.Item> _itemList;// = new ArrayList<>();

    private PagerTab _pagerTab;

    private RecyclerView _rvList;
    private EmptyView _emptyView;

    private GridAdapter _adapter;
    private EndlessRecyclerViewScrollListener _scrollListener;

    public GridFragment() {
        super();
        //setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        _context.registerReceiver(_refreshReceiver, new IntentFilter(BR_VIEW_PAGER_GRID_REFRESH));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.v_list, container, false);

        _emptyView = rootView.findViewById(R.id.emptyView);
        _emptyView.setEmptyDesc(R.string.no_list);

        _rvList = rootView.findViewById(R.id.rvList);
        int margin = (int) ViewUtil.dp2px(0, _context);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) _rvList.getLayoutParams();
        params.setMargins(margin, 0, margin, 0);

        float spacing = 1.5f * _context.getResources().getDisplayMetrics().density + 0.5f;
        _rvList.addItemDecoration(new GridSpacingItemDecoration(GridAdapter.SPAN_COUNT, (int) spacing, false));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _viewModel = new ViewModelProvider(requireActivity()).get(MultipleViewPagerViewModel.class);
        _itemList = _viewModel.gridImageList;
        _viewModel.getInitImageList().observe(getViewLifecycleOwner(), new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> goods) {
                initAdapter();
            }
        });
        _viewModel.getUpdateImageList().observe(getViewLifecycleOwner(), new Observer<ArrayList<NaverImageDto.Item>>() {
            @Override
            public void onChanged(ArrayList<NaverImageDto.Item> goods) {
                if (_adapter != null) {
                    addData(goods);
                }
            }
        });
        _viewModel.getPagerTab().observe(getViewLifecycleOwner(), new Observer<PagerTab>() {
            @Override
            public void onChanged(PagerTab pagerTab) {
                _pagerTab = pagerTab;
            }
        });
        _viewModel.getGridLinkPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
            }
        });
        _viewModel.getPagerLinkPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                if (_adapter != null) {
                    _adapter.setPagerLinkPosition(position);
                }
            }
        });
        _pagerTab = _viewModel.getPagerTab().getValue();

        _adapter = new GridAdapter(_context, null, _itemList);
        _rvList.setLayoutManager(_adapter.getLayoutManager());
        _rvList.setAdapter(_adapter);
        initAdapter();
        refreshUI();
    }

    @Override
    protected void initUI() {
    }

    private void initAdapter() {
        _rvList.removeOnScrollListener(_scrollListener);
        _scrollListener = new EndlessRecyclerViewScrollListener(_adapter.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, String.format("%s onLoadMore | %s / %s", TAG, page, totalItemsCount));
                if (_adapter.page < page && _itemList.size() > 0) {
                    switch (_pagerTab) {
                        case MENU1:
                        case MENU2:
                            _onRequestListener.onRequest(
                                    _pagerTab.type, Const.DEFAULT_GRID_LIST_CNT, _itemList.size() + 1
                            );
                            break;
                    }
                }
            }
        };
        _rvList.addOnScrollListener(_scrollListener);
//        _list.clear();
        _adapter.page = 0;
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshUI() {
    }

    private void addData(ArrayList<NaverImageDto.Item> list) {
        if (list != null && list.size() > 0) {
            int curSize = _adapter.getItemCount();
            _itemList.addAll(list);
            _adapter.notifyItemRangeInserted(curSize, list.size());
        }
        initEmptyView();
    }

    private void initEmptyView() {
        if (_itemList.size() == 0) {
            _emptyView.setVisibility(View.VISIBLE);
        } else {
            _emptyView.setVisibility(View.GONE);
        }
    }


    private class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
        private final String TAG = getClass().getSimpleName();

        public static final int ITEM_VIEW_TYPE_HEADER = 0;
        public static final int ITEM_VIEW_TYPE_LIST = 1;
        public static final int SPAN_COUNT = 3;

        private Context _context;
        private BaseActivity _baseActivity;

        public int page = 0;
        public LinearLayoutManager _layoutManager;

        private View _header;
        private ArrayList<NaverImageDto.Item> _itemList;

        private ImageLoader _imageLoader;
        private SharedData _shared;
        private Bitmap _baseBitmap;


        public GridAdapter(Context context, View header, ArrayList<NaverImageDto.Item> list) {
            _context = context;
            _header = header;
            _itemList = list;

            _imageLoader = ImageLoader.getInstance(context);
            _shared = SharedData.getInstance(context);

            /*try {
                _baseBitmap = ImageUtil.drawableToBitmap(ContextCompat.getDrawable(_context, R.drawable.base_image));
            } catch (OutOfMemoryError | Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }*/

            _baseActivity = (BaseActivity) context;
            initLayoutManager();
        }

        private void initLayoutManager() {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(_context, SPAN_COUNT);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int spanSize;
                    switch (getItemViewType(position)) {
                        case ITEM_VIEW_TYPE_HEADER:
                            spanSize = SPAN_COUNT;
                            break;
                        case ITEM_VIEW_TYPE_LIST:
                        default:
                            spanSize = 1;
                            break;
                    }
                    return spanSize;
                }
            });
            _layoutManager = gridLayoutManager;

            /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            _layoutManager = linearLayoutManager;*/
        }

        public LinearLayoutManager getLayoutManager() {
            return _layoutManager;
        }

        private boolean isHeader(int position) {
            if (_header != null) {
                return position == 0;
            } else {
                return false;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_LIST;
        }

        private int getPositionByItemList(int adapterPosition) {
            if (_header != null) {
                return --adapterPosition;
            }
            return adapterPosition;
        }

        private int getPositionByAdapter(int itemPosition) {
            if (_header != null) {
                return ++itemPosition;
            }
            return itemPosition;
        }

        @Override
        public int getItemCount() {
            int count = _itemList.size();
            if (_header != null) {
                count++;
            }
            return count;
        }

        @NonNull
        @Override
        public GridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View contactView;
            switch (viewType) {
                case ITEM_VIEW_TYPE_HEADER:
                    contactView = _header;
                    break;
                case ITEM_VIEW_TYPE_LIST:
                default:
                    contactView = LayoutInflater.from(context).inflate(R.layout.c_naver_image, parent, false);
                    break;
            }
            return new GridAdapter.ViewHolder(contactView, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull GridAdapter.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_HEADER:
                    break;
                case ITEM_VIEW_TYPE_LIST:
                    initViewHolder(holder, position);
                    break;
            }
        }

        private void initViewHolder(GridAdapter.ViewHolder holder, final int position) {
            final int itemPosition = getPositionByItemList(position);
            final NaverImageDto.Item item = _itemList.get(itemPosition);

            holder.tvTitle.setText(item.title);
            _imageLoader.download(item.imageUrl, holder.ivImage, null, false);
        }

        public void refreshItem(int position) {
            int adapterPosition = getPositionByAdapter(position);
            notifyItemChanged(adapterPosition);
        }

        public void addItems(ArrayList<NaverImageDto.Item> list) {
            int size = _itemList.size();
            if (_header != null) size++;
            _itemList.addAll(list);
            notifyItemRangeInserted(size, _itemList.size());
        }

        public void clearItems() {
            _itemList.clear();
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            _itemList.remove(position);
            int adapterPosition = getPositionByAdapter(position);
            notifyItemRemoved(adapterPosition);
        }

        public void addItem(@NonNull NaverImageDto.Item item) {
            _itemList.add(item);
            int size = _itemList.size();
            if (_header != null) size++;
            notifyItemInserted(size - 1);
        }

        private void setPagerLinkPosition(final int position) {
            _layoutManager.scrollToPositionWithOffset(position, 0);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                private int cnt = 0;
                private int runCnt = 0;

                @Override
                public void run() {
                    final ViewHolder viewHolder;
                    if (_rvList != null) {
                        viewHolder = (GridAdapter.ViewHolder) _rvList.findViewHolderForAdapterPosition(position);
                    } else {
                        viewHolder = null;
                    }
                    if (viewHolder != null) {
                        if (cnt % 2 == 0) {
                            viewHolder.ivImage.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.ivImage.setColorFilter(Color.argb(100, 0, 0, 0));
                                }
                            });
                        } else {
                            viewHolder.ivImage.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.ivImage.clearColorFilter();
                                }
                            });
                        }
                        cnt++;
                        runCnt = 0;
                    }
                    if (cnt == 4 || runCnt == 10) {
                        timer.cancel();
                        timer.purge();
                    }
                    runCnt++;
                }
            }, 200, 150);
        }

        private void moveDetail(int position) {
            final int itemPosition = getPositionByItemList(position);
            _viewModel.setGridLinkPosition(itemPosition);
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTitle;
            ImageView ivImage;

            @Override
            public void onClick(View v) {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.tvText:
                    case R.id.ivImage:
                        moveDetail(position);
                        break;
                }
            }

            ViewHolder(View itemView, int viewType) {
                super(itemView);
                switch (viewType) {
                    case ITEM_VIEW_TYPE_HEADER:
                        break;
                    case ITEM_VIEW_TYPE_LIST:
                    default:
                        ivImage = itemView.findViewById(R.id.ivImage);
                        tvTitle = itemView.findViewById(R.id.tvTitle);
                        ivImage.setOnClickListener(this);
                        tvTitle.setOnClickListener(this);
                        FrameLayout flWrap = itemView.findViewById(R.id.flWrap);
                        flWrap.getLayoutParams().height = (int) ViewUtil.dp2px(250, _context);
                        break;
                }
            }
        }
    }
}
