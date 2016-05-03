package com.breakout.util.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.breakout.util.res.AnimationSuite;


/**
 * GridView를 사용하여 cell의 이동을 가능하게 하는 View<br>
 * cell 선택시 설정된 color로 background color가 설정이 되는데 이때 adapter의 getview에서 
 * {@link #checkChildBackGroundColor(int, View)}를 필수로 실행하여 주어야 한다.<br>
 * adapter에서 아래와 같이 설정한다.
 * 
 * <pre>
 * 1. implements OnDragListener, OnDropListener
 * 
 * 2. set listener
 * 	{@link #setOnDropListener(OnDropListener)}
 * 	{@link #setOnDragListener(OnDragListener)}
 * 
 * 3. {@link #checkChildBackGroundColor(int, View)} 설정
 * 	ex)
 * 	public View getView(final int position, View convertView, ViewGroup parent) {
 * 		[getView() 작성]
 * 		{@link #checkChildBackGroundColor(int, View)}
 * 		return convertView;
 * 	}
 * 
 * 4. OnDragListener, OnDropListener callback method 작성
 * 	ex)	
 * 	public void drop(int fromIndex, int toIndex) {
 * 		Object item = dtoList.remove(fromIndex);
 * 		dtoList.add(toIndex, item);
 *  	notifyDataSetChanged();
 * 	}
 * 	
 * </pre>
 * 
 * @author gue
 * @since 2013. 1. 30.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public class DragDropGridView extends GridView {
	/**
	 * Activity context
	 */
	private Context _context;
	private WindowManager _windowManager;
	private WindowManager.LayoutParams _windowParams;
	private OnDropListener _onDropListener;
	private OnDragListener _onDragListener;
	private Bound bound = new Bound();
	/**
	 * 현재 GridView의 DecorView에서의 x(left),y(top) 좌표
	 */
	private Point _rawXY;
	/**
	 * current touch coordinate 
	 */
	private Point _touchPosition = new Point(0, 0);
	/**
	 * {@link #_ivDragView} offset coordinate 
	 */
	private Point _ivDragViewOffset = new Point(0, 0);
	/**
	 * drag view를 하나 생성한다.
	 */
	private ImageView _ivDragView;
	/**
	 * 현재 선택된 item index
	 */
	private int _selectItemIndex = -1;
	/**
	 * 0x77bae34b
	 */
	private int _selectColor = 0x77bae34b;
	/**
	 * convertView의 재사용으로 인한 선택된background color를 convertView에 다시 그려주고 
	 * 선택이 취소됬을때 해당 background를 복원하기 위한View 
	 */
	private View _selectMoveView;
	/**
	 * select cell
	 */
	private View _selectView;
	/**
	 * select cell background
	 */
	private Drawable _selectViewBg;
	/**
	 * true면 drag 활성화 상태
	 */
	private boolean _enableDrag;
	/**
	 * true면 icon wiggle 활성화 상태, 기본값 true
	 */
	private boolean _enableWiggle = true;
	/**
	 * icon wiggle timer
	 */
	private Timer _wiggleTimer;
	
	
	public DragDropGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public DragDropGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public DragDropGridView(Context context) {
		super(context);
		init(context);
	}

	
	private void init(Context context) {
		_context = context;
		setOnItemLongClickListener(onItemLongClickListener);

		_windowManager = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
		_windowParams = new WindowManager.LayoutParams();
		_windowParams.gravity = Gravity.LEFT | Gravity.TOP;
		_windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		_windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		_windowParams.format = PixelFormat.TRANSLUCENT;
	}

	/**
	 * drag start<br>
	 * 드래그하는 동안 보일 view를 bitmap 으로 캐싱하여 window에 addView 시킨다 
	 */
	@SuppressWarnings("deprecation")
	private void doMakeDragview() {
		// 0. drag listener start
		if (_onDragListener != null) _onDragListener.dragStart(_selectItemIndex);
		
		// 1. get select view
		_selectView = getChildAt(_selectItemIndex - getFirstVisiblePosition()); // child position = dto position - firstVisiblePosition
		_selectMoveView = _selectView;
		_selectViewBg = _selectView.getBackground();
		
		// 2. get drawing cache of select view
		_selectView.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(_selectView.getDrawingCache());
		_selectView.setDrawingCacheEnabled(false);
		_selectView.setBackgroundColor(_selectColor);
		
		// 3. create drag view 
		_ivDragView = new ImageView(_context);
		_ivDragView.setImageBitmap(bitmap);
		_ivDragView.setScaleType(ScaleType.FIT_CENTER);
		if (android.os.Build.VERSION.SDK_INT < 16) _ivDragView.setAlpha(200);
		else _ivDragView.setImageAlpha(200);

		// 4-1. add view : 선택된 이미지 확대
		_windowParams.height = (int) (_selectView.getWidth() * 1.2);
		_windowParams.width = (int) (_selectView.getWidth() * 1.2);
		
		// 4-2. add view : drag view의 offset을 계산
		_ivDragViewOffset.x = (int) (_windowParams.width * 0.5);
		_ivDragViewOffset.y = (int) (_windowParams.height * 0.8);
		
		// 4-3 add view : drag view의 시작 좌표 설정 
		_windowParams.x = _touchPosition.x + _rawXY.x - _ivDragViewOffset.x;		// [현재 터치좌표] + [rootview기준의 그리드뷰 시작좌표] - [drag view의 가로]
		_windowParams.y = _touchPosition.y + _rawXY.y - _ivDragViewOffset.y;
		
		_windowManager.addView(_ivDragView, _windowParams);
		
		if (_enableWiggle) {
			_wiggleTimer = new Timer();
			_wiggleTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					_wiggleHandler.sendEmptyMessage(0);
				}
			}, 100, 320);
		}
	}
	
	/** 
	 * icon_wiggle 
	 */
	private final Handler _wiggleHandler = new Handler() {
		public void handleMessage(Message msg) {
			childAnimation(AnimationSuite.wiggleIconF(200), AnimationSuite.wiggleIconR(200));
		}
	};
	
	private void childAnimation(Animation aniF, Animation aniR){
		int count = getChildCount();
		for (int i=0 ; i < count ; i++) {
			if (aniF != null && aniR != null) {
				if(i%2==0) getChildAt(i).startAnimation(aniF);
				else getChildAt(i).startAnimation(aniR);
			} else {
				if(i%2==0) getChildAt(i).clearAnimation();
				else getChildAt(i).clearAnimation();
			}
		}
	}
	
	
	/**
	 * 드래그하기, autoscroll 구현
	 */
	private void doDragView(int x, int y) {
		_windowParams.x = x + _rawXY.x - _ivDragViewOffset.x;
		_windowParams.y = y + _rawXY.y - _ivDragViewOffset.y;
		_windowManager.updateViewLayout(_ivDragView, _windowParams);
		scrollList(y + _rawXY.y);
		
		if (_onDragListener != null) {
			int toIndex = pointToPosition(x, y);
			_onDragListener.drag(_selectItemIndex, toIndex);
		}
	}

	/**
	 * 드랍
	 */
	private void doDropView(int x, int y) {
		int toIndex = pointToPosition(x, y);
		if (toIndex > INVALID_POSITION && _onDropListener != null) {
			_onDropListener.drop(_selectItemIndex, toIndex);
		}
		setNullDragView();
	}

	/**
	 * 드래그 뷰 지우기
	 */
	@SuppressWarnings("deprecation")
	private void setNullDragView() {
		_selectItemIndex = -1;
		
		if (_ivDragView != null) {
			_windowManager.removeView(_ivDragView);
			_ivDragView.setImageBitmap(null);
			if (android.os.Build.VERSION.SDK_INT < 16 ) _ivDragView.setBackgroundDrawable(null);
			else _ivDragView.setBackground(null);
			_ivDragView = null;
		}
		
		if (_selectView != null) {
			if (android.os.Build.VERSION.SDK_INT < 16 ) {
				_selectView.setBackgroundDrawable(_selectViewBg);
				_selectMoveView.setBackgroundDrawable(_selectViewBg);
			}
			else {
				_selectView.setBackground(_selectViewBg);
				_selectMoveView.setBackground(_selectViewBg);
			}
			_selectView = null;
			_selectViewBg = null;
			_selectMoveView = null;
		}
		
		if (_wiggleTimer != null) {
			_wiggleTimer.cancel();
			_wiggleTimer = null;
			childAnimation(null, null);
		}
	}

	private void scrollList(int y) {
		Bound bound = getNewScrollBounds(y);
		int height = getHeight();
		int speed = 0;
		
		// scroll the list up a bit
		if (y > bound.lowerBound) {
			speed = y > (height + bound.lowerBound) / 2 ? 15 : 4;
		} 
		// scroll the list down a bit
		else if (y < bound.upperBound) {
			speed = y < bound.upperBound / 2 ? -15 : -4;
		}
		
		if (speed != 0) {
			int distance = (int) (2 * getResources().getDisplayMetrics().density * speed);
			smoothScrollBy(distance, 10);
		}
	}

	private Bound getNewScrollBounds(int y) {
		int height = getHeight();
		if (y >= (height / 3) ) {
			bound.upperBound = height / 3 + _rawXY.y;
		}
		if (y <= (height * 2 / 3) ) {
			bound.lowerBound = height * 2 / 3 + _rawXY.y;
		}
		return bound;
	}


/* ************************************************************************************************
 * INFO useful method
 */
	public void setOnDropListener(OnDropListener listener) {
		_onDropListener = listener;
	}

	public void setOnDragListener(OnDragListener listener) {
		_onDragListener = listener;
	}
	
	/**
	 * 기본값 false
	 * @author gue
	 */
	public void setEnableDrag(boolean enable) {
		_enableDrag = enable;
	}
	
	/**
	 * 기본값 true
	 * @author gue
	 */
	public void setIconWiggler(boolean enable){
		_enableWiggle = enable;
	}
	
	/**
	 * cell 선택시의 background color 설정, 기본값 0x77bae34b
	 * @author gue
	 */
	public void setSelectColor(int color){
		_selectColor = color;
	}
	
	/**
	 * adapter에서  return convertView; 바로 전행에 작성되어야 한다.
	 * @author gue
	 */
	@SuppressWarnings("deprecation")
	public void checkChildBackGroundColor (int position, View convertView) {
		if (_enableDrag && _selectItemIndex == position) {
			_selectMoveView = convertView;
			convertView.setBackgroundColor(_selectColor);
		}
		else if (_enableDrag && _selectItemIndex > -1) {
			if (android.os.Build.VERSION.SDK_INT < 16 ) convertView.setBackgroundDrawable(_selectViewBg);
			else convertView.setBackground(_selectViewBg);
		}
	}
	
	/**
	 * 뷰의 절대 좌표 구하기 - DecorView(topmost view)에서의 x(left),y(top) 좌표
	 */
	public Point toViewRawXY(View view) {
		View parentView = view.getRootView();
		int x = 0;
		int y = 0;
		while (true) {
			x = x + view.getLeft();
			y = y + view.getTop();
			view = (View) view.getParent();
			if (parentView == view) break;
		}
		return new Point(x, y);
	}
	
	
/* ************************************************************************************************
 * INFO Listener
 */
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if (_enableDrag && position > INVALID_POSITION) {
				_selectItemIndex = position;
				doMakeDragview();
			}
			return true;
		}
	};
	
	/**
	 * 부모뷰에서 차일드뷰로 가는 이벤트를 가로챈다 이벤트를 받아서 action_down 의 x, y 값을 저장한다.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (_rawXY == null) _rawXY = toViewRawXY(this);
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				_touchPosition.set( (int) e.getX(), (int) e.getY() );
				break;
		}
		return super.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (_ivDragView != null) {
			int x = (int) e.getX();
			int y = (int) e.getY();
			switch (e.getAction()) {
				case MotionEvent.ACTION_MOVE:
					doDragView(x, y);
					break;
				case MotionEvent.ACTION_UP:
					doDropView(x, y);
					break;
				case MotionEvent.ACTION_CANCEL:
					setNullDragView();
					break;
			}
		}
		return super.onTouchEvent(e);
	}
	

/* ************************************************************************************************
 * INFO inner class
 */
	/**
	 * 아이템을 선택후 이동시에 GridView의 리스트를 3등분하여 방향으로 나누어 리스트를 이동시킨다.
	 * @author gue
	 * @since 2013. 2. 1.
	 * @copyright Copyright.2011.gue.All rights reserved.
	 * @version 1.0
	 */
	private static class Bound {
		protected int lowerBound;
		protected int upperBound;
	}
	
/* ************************************************************************************************
 * INFO interface
 */
	/**
	 * drag의 시작과 진행에 대한 callback interface
	 */
	public interface OnDragListener {
		/**
		 * drag의 시작시 수행할 작업에 대한 정의 
		 */
		void dragStart(int selectIndex);
		/**
		 * drag중일경우 수행할 작업에 대한 정의
		 * @param fromIndex first index of view in list 
		 * @param currentIndex current drag index view in list
		 */
		void drag(int fromIndex, int currentIndex);
	}
	
	/**
	 * drop되었을 때의 행동에 대한 callback interface
	 */
	public interface OnDropListener {
		/**
		 * drop되었을 때 리스트상의 첫 위치와 드롭 위치를 전달
		 * @param fromIndex first index of view in list 
		 * @param toIndex drop index of view in list
		 */
		void drop(int fromIndex, int toIndex);
	}

	
/* ************************************************************************************************
 * INFO not used
 */
	/**
	 * EVENT HELPERS
	 */
/*	protected void animateDragged(View v) {
		// View v = getChildAt(dragged);
		// int x = getCoorFromIndex(dragged).x + childSize / 2, y = getCoorFromIndex(dragged).y + childSize / 2;
		// int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4);
		// v.layout(l, t, l + (childSize * 3 / 2), t + (childSize * 3 / 2));
		android.view.animation.AnimationSet animSet = new android.view.animation.AnimationSet(true);
		android.view.animation.ScaleAnimation scale = new android.view.animation.ScaleAnimation(.667f, 1, .667f, 1, 1 * 3 / 4, 1 * 3 / 4);
		scale.setDuration(150);
		android.view.animation.AlphaAnimation alpha = new android.view.animation.AlphaAnimation(1, .5f);
		alpha.setDuration(150);

		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);

		v.clearAnimation();
		v.startAnimation(animSet);
	}*/
	/**
	 * Clamp val to be &gt;= min and &lt; max.
	 */
/*	protected int clamp(int val, int min, int max) {
		if (val < min) {
			return min;
		} else if (val >= max) {
			return max - 1;
		} else {
			return val;
		}
	}*/
}