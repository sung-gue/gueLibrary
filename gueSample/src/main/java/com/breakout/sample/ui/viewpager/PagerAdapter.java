package com.breakout.sample.ui.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.util.img.ImageLoader;

import java.util.ArrayList;


public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private Context _context;
    private BaseActivity _baseActivity;

    private RecyclerView _recyclerView;

    private ArrayList<NaverImageDto.Item> _list;
    private PageViewStatus _pageViewStatus;

    private ImageLoader _imageLoader;
    private SharedData _shared;
    private Bitmap _baseBitmap;

    public PagerAdapter(Context context, ArrayList<NaverImageDto.Item> list, PageViewStatus pageViewStatus) {
        _context = context;
        _list = list;
        _pageViewStatus = pageViewStatus;

        _imageLoader = ImageLoader.getInstance(context);
        _shared = SharedData.getInstance(context);

        /*try {
            _baseBitmap = ImageUtil.drawableToBitmap(ContextCompat.getDrawable(_context, R.drawable.base_image));
        } catch (OutOfMemoryError | Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }*/

        _baseActivity = (BaseActivity) context;
    }

    @Override
    public int getItemCount() {
        return _list.size();
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
        final NaverImageDto.Item item = _list.get(position);

        holder.tvTitle.setText(item.title);
        _imageLoader.download(item.imageUrl, holder.ivImage, null, false);
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }

    public void addItems(ArrayList<NaverImageDto.Item> list) {
        int size = _list.size();
        _list.addAll(list);
        notifyItemRangeInserted(size, _list.size());
    }

    public void clearItems() {
        _list.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        _list.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(@NonNull NaverImageDto.Item item) {
        _list.add(item);
        int size = _list.size();
        notifyItemInserted(size - 1);
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
                    break;
            }
        }

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTitle.setOnClickListener(this);
        }
    }
}
