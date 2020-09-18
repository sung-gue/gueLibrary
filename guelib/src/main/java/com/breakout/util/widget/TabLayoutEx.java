package com.breakout.util.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;


/**
 * {@link ViewPager}와 {@link TabLayout}을 사용하여 UI를 구현할 때 좌우 스크롤을 무한으로 가능하게 도와준다.<br/>
 * {@link ViewPager}에서 {@link #getModifyTabPosition(int, int)}<br/>
 * {@link ViewPager}의 {@link PagerAdapter}의 method를 아래의 붉은 부분처럼 수정한다.
 * <pre>
 *      public class SectionsPagerAdapter extends PagerAdapter {
 *          ...
 *          private String[] _pagerTitle = new String[] {"one", "two", "three"};
 *          &#64;Override
 *          public int getCount() {
 *              // ViewPager의 총 페이지수에 두개의 페이지수를 더한다.
 *              return <font color=red>TabLayoutEx.getCalcTotalPageCount(_pagerTitle.length)</font>;
 *          }
 *          &#64;Override
 *          public CharSequence getPageTitle(int position) {
 *              position = <font color=red>TabLayoutEx.getModifyTabPosition(getCount(), position);</font>
 *              return _pagerTitle[position];
 *          }
 *          ...
 *     }
 * </pre>
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2016.gue.All rights reserved.
 * @since 2016.02.18
 */
public class TabLayoutEx extends TabLayout {

    public TabLayoutEx(Context context) {
        super(context);
    }

    public TabLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 무한스크롤을 적용하기 위해 page의 위치를 받아 수정된 tab의 위치를 반환.
     *
     * @param pageCnt  page total cnt
     * @param position current pager position
     * @return the modify position
     */
    public static final int getModifyTabPosition(int pageCnt, int position) {
        // TODO 2016-03-29 무한루프 제거
//        int modifyPosition = position - 1;
//        if (position == 0) {
//            modifyPosition = pageCnt - 2;
//        } else if (position == pageCnt - 1) {
//            modifyPosition = 1;
//        }
//        return modifyPosition;
        return position;
    }

    /**
     * 무한스크롤을 적용하기 위해 page의 위치를 받아 수정된 위치를 반환.
     *
     * @param pageCnt  page total cnt
     * @param position current pager position
     * @return the modify position
     */
    public static final int getModifyPagerPosition(int pageCnt, int position) {
        // TODO 2016-03-29 무한루프 제거
//        int modifyPosition = position - 1;
//        if (position == 0) {
//            modifyPosition = pageCnt - 3;
//        } else if (position == pageCnt - 1) {
//            modifyPosition = 0;
//        }
//        return modifyPosition;
        return position;
    }

    public static final int getFirstPagerPosition() {
        // TODO 2016-03-29 무한루프 제거
//        return 1;
        return 0;
    }

    /**
     * 실제 작업할 페이지의 수를 입력받고 무한스크롤 적용을 위해 가상의 페이지수를 더한 수치를 반환
     *
     * @param realPageCnt the real pagecnt
     * @return the int
     */
    public static final int getCalcTotalPageCount(int realPageCnt) {
        // TODO 2016-03-29 무한루프 제거
//        return realPageCnt + 2;
        return realPageCnt;
    }

    /**
     * ViewPager의 page count를 입력받아 실제 tab size를 반환
     *
     * @param realPagerCnt the real viewPager count
     * @return the int
     */
    public static final int getCalcTotalTabCount(int realPagerCnt) {
        // TODO 2016-03-29 무한루프 제거
//        return realPagerCnt - 2;
        return realPagerCnt;
    }

    /**
     * modify method {@link #setupWithViewPager(ViewPager)}
     *
     * @param viewPager
     */
    public final void setupWithViewPagerEx(@NonNull ViewPager viewPager) {
//        super.setupWithViewPager(viewPager);
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }

        // TODO 2016-03-29 무한루프 제거
//        viewPager.setCurrentItem(TabLayoutEx.getFirstPagerPosition(), false);

        // First we'll add Tabs, using the adapter's page titles
        setTabsFromPagerAdapterEx(adapter);

        // Now we'll add our page change listener to the ViewPager
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListenerEx(this, viewPager));

        // Now we'll add a tab selected listener to set ViewPager's current item
        setOnTabSelectedListener(new ViewPagerOnTabSelectedListenerEx(viewPager));

        // Make sure we reflect the currently set ViewPager item
        int pageCnt;
        if ((pageCnt = adapter.getCount()) > 0) {
            final int curItem = TabLayoutEx.getModifyTabPosition(pageCnt, viewPager.getCurrentItem());
            if (getSelectedTabPosition() != curItem) {
                getTabAt(curItem).select();
            }
        }
    }


    public interface TabViewDefine {
        /**
         * tab view에 사용될 모양정의
         */
        enum ViewType {
            /**
             * resource Arraylist : string, string resource id
             */
            TEXT_ONLY,
            /**
             * resource Arraylist : drawable resource id, drawable
             */
            ICON_ONLY,
            /**
             * resource Arraylist : drawable resource id
             */
            TEXT_AND_ICON,
            /**
             * resource Arraylist : drawable resource id
             */
            CUSTOM
        }

        ViewType getTabViewType();

        /**
         * {@link ViewType}에 따른 리소스 리스트
         *
         * @return the tab view source
         */
        ArrayList<TabViewResource> getTabViewSource();

    }

    public static class TabViewResource<TEXT, DRAWABLE, CUSTOM_VIEW> {
        public TEXT text;
        public DRAWABLE drawable;
        public CUSTOM_VIEW view;
    }

    public void setTabsFromPagerAdapterEx(@NonNull PagerAdapter adapter) {
        removeAllTabs();
        if (adapter instanceof TabViewDefine) {
            TabViewDefine delegate = (TabViewDefine) adapter;
            ArrayList<TabViewResource> list = delegate.getTabViewSource();
            for (int i = 0, count = getCalcTotalTabCount(adapter.getCount()); i < count; i++) {
                switch (delegate.getTabViewType()) {
                    case TEXT_ONLY:
                        if (list.get(i).text instanceof Integer) {
                            addTab(newTab().setText((Integer) list.get(i).text));
                        } else {
                            addTab(newTab().setText((String) list.get(i).text));
                        }
                        break;
                    case ICON_ONLY:
                        if (list.get(i).drawable instanceof Integer) {
                            addTab(newTab().setIcon((Integer) list.get(i).drawable));
                        } else {
                            addTab(newTab().setIcon((Drawable) list.get(i).drawable));
                        }
                        break;
                    case TEXT_AND_ICON:
                        if (list.get(i).text instanceof Integer) {
                            addTab(newTab().setText((Integer) list.get(i).text));
                        } else {
                            addTab(newTab().setText((String) list.get(i).text));
                        }
                        if (list.get(i).drawable instanceof Integer) {
                            addTab(newTab().setIcon((Integer) list.get(i).drawable));
                        } else {
                            addTab(newTab().setIcon((Drawable) list.get(i).drawable));
                        }
                        break;
                    case CUSTOM:
                        if (list.get(i).view instanceof Integer) {
                            addTab(newTab().setCustomView((Integer) list.get(i).drawable));
                        } else {
                            addTab(newTab().setCustomView((View) list.get(i).drawable));
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            for (int i = 0, count = getCalcTotalTabCount(adapter.getCount()); i < count; i++) {
                addTab(newTab().setText(adapter.getPageTitle(i)));
            }
        }
    }

    /**
     * modify {@link TabLayoutOnPageChangeListener} class
     */
    public static class TabLayoutOnPageChangeListenerEx implements ViewPager.OnPageChangeListener {
        private final WeakReference<TabLayout> mTabLayoutRef;
        private final WeakReference<ViewPager> mViewPager;
        private int mPreviousScrollState;
        private int mScrollState;

        public TabLayoutOnPageChangeListenerEx(TabLayout tabLayout, ViewPager viewPager) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
            mViewPager = new WeakReference<>(viewPager);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            /*
            // TODO 2016-03-29 무한루프 제거
            int pageCnt;
            if (tabLayout != null && position != 0 && position != (pageCnt = TabLayoutEx.getCalcTotalPageCount(tabLayout.getTabCount())) - 1) {
                position = TabLayoutEx.getModifyTabPosition(pageCnt, position);

                // Update the scroll position, only update the text selection if we're being
                // dragged (or we're settling after a drag)
                final boolean updateText = (mScrollState == ViewPager.SCROLL_STATE_DRAGGING)
                        || (mScrollState == ViewPager.SCROLL_STATE_SETTLING && mPreviousScrollState == ViewPager.SCROLL_STATE_DRAGGING);
                tabLayout.setScrollPosition(position, positionOffset, updateText);
            }*/
            if (tabLayout != null) {
                // Only update the text selection if we're not settling, or we are settling after
                // being dragged
                final boolean updateText = mScrollState != SCROLL_STATE_SETTLING ||
                        mPreviousScrollState == SCROLL_STATE_DRAGGING;
                // Update the indicator if we're not settling after being idle. This is caused
                // from a setCurrentItem() call and will be handled by an animation from
                // onPageSelected() instead.
                final boolean updateIndicator = !(mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
                tabLayout.setScrollPosition(position, positionOffset, updateText);
            }
        }

        @Override
        public void onPageSelected(int position) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE
                        || (mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
//                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
                tabLayout.getTabAt(position).select();
            }
            // TODO 2016-03-29 무한루프 제거
            /*final ViewPager viewPager = mViewPager.get();
            *//*Log.i("onPageSelected | " + position + " / tab position | " + tabLayout.getSelectedTabPosition() + " / scrollstate | " + mScrollState);*//*
            if (tabLayout != null && viewPager != null) {
                int pageCnt = TabLayoutEx.getCalcTotalPageCount(tabLayout.getTabCount());
                int modifyPosition = TabLayoutEx.getModifyTabPosition(pageCnt, position);
                if (position == 0 || position == pageCnt - 1) {
                    viewPager.setCurrentItem(modifyPosition, false);
//                    tabLayout.getTabAt(modifyPosition - 1).select();
//                    tabLayout.setScrollPosition(modifyPosition - 1, 0, true);
                } else if (tabLayout.getSelectedTabPosition() != modifyPosition) {
                    // Select the tab, only updating the indicator if we're not being dragged/settled
                    // (since onPageScrolled will handle that).
                    tabLayout.getTabAt(modifyPosition).select();
                }
            }*/
        }
    }

    /**
     * modify {@link ViewPagerOnTabSelectedListener}
     */
    public static class ViewPagerOnTabSelectedListenerEx implements TabLayout.OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListenerEx(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            // TODO 2016-03-29 무한루프 제거
//            mViewPager.setCurrentItem(tab.getPosition() + 1);
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // No-op
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // No-op
        }
    }
}
