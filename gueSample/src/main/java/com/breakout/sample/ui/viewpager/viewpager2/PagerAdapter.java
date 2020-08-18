package com.breakout.sample.ui.viewpager.viewpager2;

import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.breakout.sample.Log;
import com.breakout.sample.constant.Extra;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.ui.viewpager.PageViewStatus;

import java.util.ArrayList;
import java.util.List;


class PagerAdapter extends FragmentStateAdapter {

    private final String TAG = getClass().getSimpleName();

    private SparseArray<Fragment> _fragments = new SparseArray<>();
    private ArrayList<Long> _pageIdList = new ArrayList<>();

    private ArrayList<NaverImageDto.Item> _list;
    private PageViewStatus _pageViewStatus;


    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<NaverImageDto.Item> list, PageViewStatus pageViewStatus) {
        super(fragmentActivity);
        _list = list;
        _pageViewStatus = pageViewStatus;

        for (NaverImageDto.Item item : list) _pageIdList.add(generatePageId(item));
        _pageViewStatus = pageViewStatus;
        registerFragmentTransactionCallback(_fragmentTransactionCallback);
    }

    private DetailFragment getFragment(int position) {
        return (DetailFragment) _fragments.get(position);
    }

    private void log(String log) {
        if (false) Log.i(TAG, log);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        log("createFragment " + position);
        NaverImageDto.Item item = _list.get(position);
        Bundle args = new Bundle();
        args.putParcelable(Extra.ITEM, item);
        Fragment fragment = new DetailFragment(item, _pageViewStatus);
        fragment.setArguments(args);
        _fragments.put(position, fragment);
        return fragment;
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
//        try {
//            GoodsDetailFragment goodsDetailFragment = getFragment(position);
//            goodsDetailFragment.refreshUI();
//        } catch (Exception ignored) {
//        }
    }

    public void addItems(ArrayList<NaverImageDto.Item> list) {
        int size = _list.size();
        for (NaverImageDto.Item item : list) _pageIdList.add(generatePageId(item));
        _list.addAll(list);
        notifyItemRangeInserted(size, _list.size());
    }

    public void clearItems() {
        _pageIdList.clear();
        _list.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        _pageIdList.remove(position);
        _fragments.remove(position);
        for (int i = position + 1; i < _list.size(); i++) {
            Fragment fragment = _fragments.get(i);
            if (fragment != null) {
                _fragments.put(i - 1, fragment);
            }
        }
        _list.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(@NonNull NaverImageDto.Item item) {
        _pageIdList.add(generatePageId(item));
        _list.add(item);
        int size = _list.size();
        notifyItemInserted(size - 1);
    }

    private long generatePageId(Object object) {
        return object.hashCode();
    }

    @Override
    public long getItemId(int position) {
//        long itemId = _list.get(position).hashCode();
        long itemId = RecyclerView.NO_ID;
        if (position < _pageIdList.size()) {
            itemId = _pageIdList.get(position);
        }
        log(String.format("getItemId pos=%s, itemId=%s ", position, itemId));
        return itemId;
    }

    @Override
    public boolean containsItem(long itemId) {
        log("containsItem : " + itemId);
        /*for (NaverImageDto.Item item : _list) {
            if (itemId == item.hashCode()) {
                return true;
            }
        }
        return false;*/
        return _pageIdList.contains(itemId);
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    private FragmentTransactionCallback _fragmentTransactionCallback = new FragmentTransactionCallback() {
        @NonNull
        @Override
        public OnPostEventListener onFragmentPreAdded(@NonNull Fragment fragment) {
            log("onFragmentPreAdded " + fragment);
            return super.onFragmentPreAdded(fragment);
        }

        @NonNull
        @Override
        public OnPostEventListener onFragmentPreRemoved(@NonNull Fragment fragment) {
            log("onFragmentPreRemoved " + fragment);
            int index = _fragments.indexOfValue(fragment);
            if (index > -1) {
                _fragments.removeAt(_fragments.indexOfValue(fragment));
            }
            return super.onFragmentPreRemoved(fragment);
        }

        @NonNull
        @Override
        public OnPostEventListener onFragmentMaxLifecyclePreUpdated(@NonNull Fragment fragment, @NonNull Lifecycle.State maxLifecycleState) {
            log("onFragmentMaxLifecyclePreUpdated " + fragment + " / " + maxLifecycleState);
            return super.onFragmentMaxLifecyclePreUpdated(fragment, maxLifecycleState);
        }
    };

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        log("onBindViewHolder : " + holder + " / " + payloads);
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull FragmentViewHolder holder) {
        log("onViewDetachedFromWindow : " + holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        log("onAttachedToRecyclerView : " + recyclerView);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        log("onDetachedFromRecyclerView : " + recyclerView);
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterFragmentTransactionCallback(_fragmentTransactionCallback);
    }

}
