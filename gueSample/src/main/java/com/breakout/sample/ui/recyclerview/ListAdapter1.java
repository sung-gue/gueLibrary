package com.breakout.sample.ui.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.util.img.ImageLoader;
import com.breakout.util.widget.ViewUtil;

import java.util.ArrayList;


class ListAdapter1 extends RecyclerView.Adapter<ListAdapter1.ViewHolder> {
    private final String TAG = getClass().getSimpleName();

    public static final int SPAN_COUNT = 3;

    private Context _context;
    private BaseActivity _baseActivity;

    /**
     * <pre>
     *      private RecyclerView _rvList = findViewById(R.id.rvList);
     *      private EndlessRecyclerViewScrollListener _scrollListener;
     *      private ArrayList<Data> _list = new ArrayList<>();
     *      ...
     *      void init() {
     *          _rvList.removeOnScrollListener(scrollListener);
     *          _scrollListener = new EndlessRecyclerViewScrollListener(_adapter.getLayoutManager()) {
     *              @Override
     *              public void onLoadMore(int page, int totalItemsCount) {
     *                  Log.i(TAG, String.format("%s onLoadMore | %s / %s", TAG, page, totalItemsCount));
     *                  if (_adapter.page < page) {
     *                      _adapter.page = page;
     *                      requestList(_list.get(totalItemsCount - 1).id);
     *                  }
     *              }
     *          };
     *          _rvList.addOnScrollListener(_scrollListener);
     *      }
     * </pre>
     */
    public int page = 0;
    public LinearLayoutManager _layoutManager;

    private RecyclerView _recyclerView;

    private ArrayList<NaverImageDto.Item> _itemList;

    private ImageLoader _imageLoader;
    private SharedData _shared;
    private Bitmap _baseBitmap;


    public ListAdapter1(Context context, ArrayList<NaverImageDto.Item> itemList) {
        _context = context;
        _itemList = itemList;

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
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        _layoutManager = gridLayoutManager;

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _layoutManager = linearLayoutManager;*/
    }

    public LinearLayoutManager getLayoutManager() {
        return _layoutManager;
    }

    @Override
    public int getItemCount() {
        return _itemList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        _recyclerView = null;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        _recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.c_naver_image, parent, false);
        return new ViewHolder(contactView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initViewHolder(holder, position);
    }

    private void initViewHolder(final ViewHolder holder, final int position) {
        final NaverImageDto.Item item = _itemList.get(position);

        holder.tvTitle.setText(item.title);
        _imageLoader.download(item.imageUrl, holder.ivImage, null, false);
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }

    public void addItems(ArrayList<NaverImageDto.Item> list) {
        int size = _itemList.size();
        _itemList.addAll(list);
        notifyItemRangeInserted(size, _itemList.size());
    }

    public void clearItems() {
        _itemList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        _itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(@NonNull NaverImageDto.Item item) {
        _itemList.add(item);
        int size = _itemList.size();
        notifyItemInserted(size - 1);
    }

    private void itemClick(ViewHolder holder, int position) {
        final NaverImageDto.Item item = _itemList.get(position);
        holder.tvTitle.setTypeface(null, Typeface.BOLD);
        holder.tvTitle.setTextColor(Color.RED);
    }

    /*
        TODO: 2020-08-13/gue holder 재사용시에 초기화 되는 지점 찾기
     */
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.tvTitle != null) {
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvTitle.setTypeface(null, Typeface.NORMAL);
        }
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
                    itemClick(this, position);
                    break;
            }
        }

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
            FrameLayout flWrap = itemView.findViewById(R.id.flWrap);
            flWrap.getLayoutParams().height = (int) ViewUtil.dp2px(150, _context);
        }
    }
}
