package com.breakout.sample.ui.viewpager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.breakout.sample.dto.NaverImageDto;

import java.util.ArrayList;

/**
 * pager, grid 화면의 데이터 공유
 */
public class MultipleViewPagerViewModel extends ViewModel {
    /**
     * 리스트 초기 데이터 연동
     */
    public MutableLiveData<ArrayList<NaverImageDto.Item>> initImageList;
    /**
     * 리스트 더보기 데이터 연동
     */
    public MutableLiveData<ArrayList<NaverImageDto.Item>> updateImageList;
    /**
     * 상품 리스트의 호출 기준
     */
    public MutableLiveData<PagerTab> pagerTab = new MutableLiveData<>();
    /**
     * grid > pager 로 전달할 position
     */
    public MutableLiveData<Integer> gridLinkPosition = new MutableLiveData<>();
    /**
     * pager > grid 로 전달할 position
     */
    public MutableLiveData<Integer> pagerLinkPosition = new MutableLiveData<>();
    /**
     * {@link #getPagerLinkPosition} 로 전달할 pager의 position
     */
    public Integer pagerLastPosition;
    public ArrayList<NaverImageDto.Item> pagerImageList = new ArrayList<>();
    public ArrayList<NaverImageDto.Item> gridImageList = new ArrayList<>();

    public MultipleViewPagerViewModel() {
        super();
        pagerTab.setValue(PagerTab.MENU1);
    }

    public LiveData<ArrayList<NaverImageDto.Item>> getInitImageList() {
        if (initImageList == null) {
            initImageList = new MutableLiveData<>();
        }
        return initImageList;
    }

    public void setInitImageList(ArrayList<NaverImageDto.Item> list) {
        pagerImageList.clear();
        pagerImageList.addAll(list);
        gridImageList.clear();
        gridImageList.addAll(list);
        initImageList.setValue(list);
    }

    public LiveData<ArrayList<NaverImageDto.Item>> getUpdateImageList() {
        if (updateImageList == null) {
            updateImageList = new MutableLiveData<>();
        }
        return updateImageList;
    }

    public void setUpdateImageList(ArrayList<NaverImageDto.Item> list) {
        updateImageList.setValue(list);
    }

    public LiveData<PagerTab> getPagerTab() {
        return pagerTab;
    }

    public void setPagerTab(PagerTab tab) {
        pagerTab.setValue(tab);
    }


    public LiveData<Integer> getGridLinkPosition() {
        return gridLinkPosition;
    }

    public void setGridLinkPosition(Integer position) {
        gridLinkPosition.setValue(position);
    }

    public LiveData<Integer> getPagerLinkPosition() {
        return pagerLinkPosition;
    }

    public void setPagerLinkPosition(Integer position) {
        pagerLinkPosition.setValue(position);
    }
}

