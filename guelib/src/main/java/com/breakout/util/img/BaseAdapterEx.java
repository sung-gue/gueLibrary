package com.breakout.util.img;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import com.breakout.util.Log;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.widget.ViewUtil;

/**
 * scroll시에 해당 리스트내용의 이미지를 {@link #_scrolling}의 값으로 구분하여 표시함으로서 
 * 리스팅시 속도 향상과 {@link ImageLoader} 사용시에 불필요한 thread의 생성을 방지한다.<br>
 * {@link OnScrollListener}를 별도로 구현한 경우 {@link #setOnScrollListener(OnScrollListener)}를 호출한다.<br>
 * <dl>※ 정의된 전역변수
 * 	<dl>
 * 		<li>{@link #TAG} : TAG, getClass().getSimpleName()</li>
 * 		<li>{@link #_context} : Activity Context, 초기값 null</li>
 * 	</dl>
 * </dl>
 * @author gue
 * @since 2012. 9. 10.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public abstract class BaseAdapterEx extends BaseAdapter implements OnScrollListener {
	/**
	 * final value, TAG
	 */
	protected final String TAG = getClass().getSimpleName();
	/**
	 * Activity Context
	 */
	protected Context _context; 
	/** 
	 * 스크롤 중이면 true, 아니면 false 
	 */
	public boolean _scrolling;
	/** 
	 * 리스트의 아이템이 마지막일 때 
	 */
	private boolean _lastChange;
	/** 
	 * 마지막 아이템일경우의 position 
	 */ 
//	private int _lastItmeCount;
	/** 
	 * 보여지는 첫번째 아이템의 position 
	 */ 
	public int _firstVisibleItem;
	
	private OnScrollListener _listener;
	
	/**
	 * 별도의 {@link OnScrollListener}를 사용하는 경우 호출한다.
	 * @param listener
	 * @author gue
	 * @since 2013. 11. 11.
	 */
	public void setOnScrollListener(OnScrollListener listener){
		_listener = listener;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		_firstVisibleItem = firstVisibleItem;
//		_lastItmeCount = totalItemCount - visibleItemCount;
		
		/*// 스크롤이 멈추고 마지막 아이템일 경우엔 한번만 notifyDataSetChanged가 일어날 수 있도록 _lastChange를 설정한다.
		if ( _lastItmeCount == firstVisibleItem) {
			_lastChange = true;
		}
		else {
			_lastChange = false;
		}*/
		
		// 상하단바 사라짐 정의 (현재는 사용하지 않음): y축방향일때 bar toggle
		/*if (_baseActivity != null) {
			if (_baseActivity._scrollY) {
				if (_baseActivity._scrollDown && _baseActivity._titleBar.isShown()){
					if (DEBUG) Log.e(TAG, "---------------visible !!");
					_baseActivity._llFooter.setVisibility(View.GONE);
					if (_baseActivity._llBodyHeader != null) _baseActivity._llBodyHeader.setVisibility(View.GONE);
					_baseActivity._titleBar.setVisibility(View.GONE);
				}else if (!_baseActivity._scrollDown && !_baseActivity._llFooter.isShown() ){
					if (DEBUG) Log.e(TAG,"gone !!");
					_baseActivity._llFooter.setVisibility(View.VISIBLE);
					if (_baseActivity._llBodyHeader != null) _baseActivity._llBodyHeader.setVisibility(View.VISIBLE);
					_baseActivity._titleBar.setVisibility(View.VISIBLE);
				}
			}
		} else if (_basePop != null) {
		}*/
		
		if (_listener != null) _listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		boolean scrolling =  false;
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:			// 0
				scrolling = false;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:	// 1
				scrolling = false;
				break;
			case OnScrollListener.SCROLL_STATE_FLING:			// 2
				scrolling = true;
				break;
		}
		
		Log.v(TAG, String.format( "%s | scrolling : %s / adapter scrolling : %s / scroll state : %s / firstItem : %s", 
								TAG, scrolling, _scrolling, scrollState, _firstVisibleItem));  
		
		// 0. 리스트가 스크롤 중이면 getView()시에 빈 이미지만 보여준다.
		if ( 	!scrolling											// 스크롤 상태가 아닐때
				&& !_lastChange										// 리스트의 마지막 아이템 위치가 아닐때
				&& _scrolling										// adapter의 scrooling 값이 true 였을때 
				&& !(scrollState == 1 && scrolling != _scrolling)	// 스크롤중 터치가 들어왔을때를 배제
			) {
			Log.v(TAG, "notifyDataSetChanged");
			notifyDataSetChanged();
		} 
		
		// 1. 스크롤중에 touch가 들어왔을때는 스크롤로 간주
		if (scrollState == 1 && scrolling != _scrolling) {
			_scrolling = true;
		} else {
			_scrolling = scrolling;
		}
		
		// 2. 스크롤이 멈추고 마지막 아이템일 경우엔 한번만 notifyDataSetChanged가 일어날 수 있도록 _lastChange를 설정한다.
		/*if ( _lastItmeCount == _firstVisibleItem) {
			_lastChange = true;
		}
		else {
			_lastChange = false;
		}*/
		
		if (_listener != null) _listener.onScrollStateChanged(view, scrollState);
	}
	

/* ************************************************************************************************
 * INFO useful method
 */
	/**
	 * device 크기에 맞추어 cell width 계산<br/>
	 * 사용하기 위해서는 {@link #_context}에 값이 입력되어 있어야 한다.
	 * @param totalGap total gap (dp)
	 * @param numColumns 가로에 들어가는 cell의 수
	 * @return cell width (px)
	 * @author gue
	 * @since 2013. 2. 1.
	 */
	protected int calcColumnWidth(int totalGap, int numColumns){
		int returnWidth = 0;
		if(_context != null) {
			int displayWidth = DeviceUtil.getDisplaySize(_context)[0];
			returnWidth = (int) (displayWidth - ViewUtil.dp2px(totalGap, _context) ) / numColumns;
		}
		return returnWidth;
	}
	
}