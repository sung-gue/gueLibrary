package com.breakout.sample.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.breakout.sample.R;
import com.breakout.sample.constant.SharedData;
import com.breakout.util.Util;
import com.breakout.util.string.StringUtil;
import com.breakout.util.widget.CustomDialog;
import com.breakout.util.widget.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * menu layout
 *
 * @author gue
 * @version 1.0
 * @since 2016.02.11
 */
public class SlideMenuLayout extends LinearLayout implements OnItemClickListener {
    /**
     * 전체 메뉴를 눌렀을 경우 작동
     *
     * @author gue
     * @version 1.0
     * @since 2016.02.24
     */
    public interface OnSlideMenuClickListener {
        /**
         * 메뉴를 눌렀을 경우 작동
         */
        void onSlideMenuClick();
    }

    protected final String TAG = getClass().getSimpleName();
    /**
     * activity context
     */
    private Context _context;
    private ListView _lvMenu;
    private DrawerLayout _dlRoot;

    private String[] _arrMenu;
    private String[] _arrMenuClickEvent;
    private ArrayList<Integer> _arrDrawable = new ArrayList<>();
    private ArrayList<HashMap<String, Class<?>>> _item;


    public SlideMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SlideMenuLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        init(null);
    }

    private void init(AttributeSet attrs) {
        _context = getContext();

//        LayoutInflater.from(_context).inflate(R.layout.ui_base_layout_slide, this, true);
        LayoutInflater.from(_context).inflate(R.layout.ui_base_slide, this, true);
        _lvMenu = (ListView) findViewById(R.id.lvMenu);
        /*setLayoutParams(new LayoutParams((int)ViewUtil.dp2px(200, _context), LayoutParams.MATCH_PARENT));
        ((LayoutParams)getLayoutParams()).gravity = Gravity.LEFT;*/

        if (isInEditMode()) {
            _arrMenu = new String[10];
            for (int i = 0; i < 10; i++) _arrMenu[i] = i + " row";
        } else {
            _arrMenu = getResources().getStringArray(R.array.slide_menu_names);
            _arrMenuClickEvent = getResources().getStringArray(R.array.slide_menu_click_event);
            TypedArray arrDrawable = getResources().obtainTypedArray(R.array.slide_menu_drawables);
            for (int i = 0; i < arrDrawable.length(); i++) {
                _arrDrawable.add(arrDrawable.getResourceId(i, -1));
            }
            arrDrawable.recycle();
        }
        setContents();
    }

    private void setContents() {
        MenuListAdapter adapter = new MenuListAdapter();
        _lvMenu.setAdapter(adapter);
        _lvMenu.setOnItemClickListener(this);
    }

    public void setDrawerLayout(DrawerLayout dlRoot) {
        _dlRoot = dlRoot;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (_dlRoot != null) _dlRoot.closeDrawers();
        String event[] = StringUtil.BAR_PATTERN.split((String) parent.getAdapter().getItem(position));
        if ("action".equals(event[0])) {
            if ("logout".equals(event[1])) {
                new CustomDialog(_context)
                        .setContents(0, R.string.al_logout)
                        .setOkBt(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedData.getInstance(_context).clearUserInfo();
                                Util.forceMove(_context);
                            }
                        })
                        .setCancelBt(R.string.cancel, null)
                        .show();
            }
        } else if ("intent".equals(event[0])) {
            Intent intent = new Intent();
            intent.setClassName(_context, _context.getPackageName() + event[1]);
            _context.startActivity(intent);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class MenuListAdapter extends BaseAdapter {
        private int _baseSize;

        public MenuListAdapter() {
            super();
            _baseSize = (int) ViewUtil.dp2px(50, _context);
        }

        @Override
        public int getCount() {
            return _arrMenu.length;
        }

        @Override
        public Object getItem(int position) {
            return _arrMenuClickEvent[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                TextView tv = new TextView(_context);
//                tv.setTextAppearance(_context, R.style.text_pt24_b_c51);
                tv.setSingleLine(false);
                tv.setMinHeight(_baseSize);
                tv.setPadding(_baseSize / 5, _baseSize / 5, _baseSize / 5, _baseSize / 5);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, _baseSize));
                tv.setCompoundDrawablePadding(_baseSize * 7 / 100);

                holder = new ViewHolder();
                holder.tvMenu = tv;
                convertView = tv;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvMenu.setText(_arrMenu[position]);
            if (isInEditMode()) {
                Drawable drawable = getResources().getDrawable(android.R.drawable.ic_delete);
                drawable.setBounds(0, 0, _baseSize * 45 / 100, _baseSize * 45 / 100);
                holder.tvMenu.setCompoundDrawables(drawable, null, null, null);
            } else {
                holder.tvMenu.setCompoundDrawablesWithIntrinsicBounds(_arrDrawable.get(position), 0, 0, 0);
            }

            return convertView;
        }

        private class ViewHolder {
            TextView tvMenu;
        }
    }
}