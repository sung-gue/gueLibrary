package com.breakout.sample.ui.viewpager;


interface OnRequestListener {
    enum Type {
        SORT_SIM,
        SORT_DATE,
    }

    void onRequest(Type sortType, int display, int start);
}
