package com.breakout.sample.ui.viewpager.viewpager3;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.breakout.sample.constant.Extra;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.ui.viewpager.PageViewStatus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


class PagerAdapter extends FragmentStatePagerAdapter {

    private final String TAG = getClass().getSimpleName();

    private SparseArray<Fragment> _fragments = new SparseArray<>();

    private ArrayList<NaverImageDto.Item> _list;
    private PageViewStatus _pageViewStatus;


    public PagerAdapter(@NonNull FragmentManager fm, ArrayList<NaverImageDto.Item> list, PageViewStatus pageViewStatus) {
        super(fm);
        _list = list;
        _pageViewStatus = pageViewStatus;
    }

    private DetailFragment getFragment(int position) {
        return (DetailFragment) _fragments.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        NaverImageDto.Item item = _list.get(position);
        Bundle args = new Bundle();
        args.putParcelable(Extra.ITEM, item);
        Fragment fragment = new DetailFragment(item, _pageViewStatus);
        fragment.setArguments(args);
        _fragments.put(position, fragment);
        return fragment;
    }

    public void addItems(ArrayList<NaverImageDto.Item> list) {
        int size = _list.size();
        _list.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        _list.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        _list.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(@NonNull NaverImageDto.Item item) {
        _list.add(item);
        int size = _list.size();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        _fragments.put(position, fragment);
        return fragment;
//        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        _fragments.remove(position);
        super.destroyItem(container, position, object);
    }


}
