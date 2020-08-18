package com.breakout.sample.ui.viewpager;

import com.breakout.sample.R;

public enum PagerTab {
    MENU1(R.id.tvMenu1, OnRequestListener.Type.SORT_SIM),
    MENU2(R.id.tvMenu2, OnRequestListener.Type.SORT_DATE);

    public int id;
    public OnRequestListener.Type type;

    PagerTab(int id, OnRequestListener.Type type) {
        this.id = id;
        this.type = type;
    }
}
