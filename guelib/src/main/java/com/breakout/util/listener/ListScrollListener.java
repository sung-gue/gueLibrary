package com.breakout.util.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;


/**
 * 리스트 형식의 View의 스크롤시에 현재 상태를 전달해줄 수 있는 listenenr<br>
 * 사용 목적 : 리스트를 스크롤 할때 상단영역과 하단 영역의 사라짐에 대한 상태값을 정의
 * @author gue
 * @since 2012. 7. 16.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public class ListScrollListener implements OnScrollListener {
	/** tag */
	protected final String TAG = getClass().getSimpleName();
	
	/** ListScrollListener를 사용하는 activity */
//	private BaseActivity _baseActivity;
//	private BasePop _basePop;
	
	private ListScroll _listScroll; 
	
	/** 스크롤 중이면 true*/
	public boolean _scrolling;
	
	/** current scroll state */
	public int _scrollState = -1;

	/** 리스트의 아이템이 마지막일 때 */
	public boolean _lastChange;
	
	/** 마지막 아이템일경우의 position */ 
	public int _lastItmeCount;
	private int _firstVisibleItem;
	
	
	
	public ListScrollListener(Object object) {
		/*if (object instanceof BaseActivity) {
			this._baseActivity =  (BaseActivity) object;
		} else if (object instanceof BasePop) {
			this._basePop = (BasePop) object;
		}*/
		this._listScroll = (ListScroll) object;
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		_firstVisibleItem = firstVisibleItem;
		_lastItmeCount = totalItemCount - visibleItemCount;
		
		// 상하단바 사라짐 정의 (현재는 사용하지 않음): y축방향일때 bar toggle
		/*if (_baseActivity != null) {
			if (_baseActivity._scrollY) {
				if (_baseActivity._scrollDown && _baseActivity._titleBar.isShown()){
					Log.e(TAG, "---------------visible !!");
					_baseActivity._llFooter.setVisibility(View.GONE);
					if (_baseActivity._llBodyHeader != null) _baseActivity._llBodyHeader.setVisibility(View.GONE);
					_baseActivity._titleBar.setVisibility(View.GONE);
				}else if (!_baseActivity._scrollDown && !_baseActivity._llFooter.isShown() ){
					Log.e(TAG,"gone !!");
					_baseActivity._llFooter.setVisibility(View.VISIBLE);
					if (_baseActivity._llBodyHeader != null) _baseActivity._llBodyHeader.setVisibility(View.VISIBLE);
					_baseActivity._titleBar.setVisibility(View.VISIBLE);
				}
			}
		} else if (_basePop != null) {
		}*/
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		_scrollState = scrollState;
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:			// 0
				_scrolling = false;
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:	// 1
				_scrolling = false;
				break;
			case OnScrollListener.SCROLL_STATE_FLING:			// 2
				_scrolling = true;
				break;
		}
		_listScroll.onScroll(_firstVisibleItem, this);
	}
	
	
	/*@Override
	public void onScroll(int firstVisibleItem, Object object) {
		Log.g(4, String.format( "%s | scrolling : %s / adapter scrolling : %s / scroll state : %s / firstItem : %s", 
				TAG, _scrollListener._scrolling, _galleryPhotoAdapter._scrolling, _scrollListener._scrollState, firstVisibleItem));  
		
		// 리스트가 스크롤 중이면 getView()시에 빈 이미지만 보여준다.
		if ( 	!_scrollListener._scrolling											// 스크롤 상태가 아닐때
				&& !_scrollListener._lastChange										// 리스트의 마지막 아이템 위치가 아닐때
				&& _galleryPhotoAdapter._scrolling									// adapter의 scrooling 값이 true 였을때 
				&& !(_scrollListener._scrollState == 1 && _scrollListener._scrolling != _galleryPhotoAdapter._scrolling)	// 스크롤중 터치가 들어왔을때를 배제
				) {
			Log.g(5, "notifyDataSetChanged");
			_galleryPhotoAdapter.notifyDataSetChanged();
		} 
		
		// 스크롤중에 touch가 들어왔을때는 스크롤로 간주
		if (_scrollListener._scrollState == 1 && _scrollListener._scrolling != _galleryPhotoAdapter._scrolling) {
			_galleryPhotoAdapter._scrolling = true;
		} else {
			_galleryPhotoAdapter._scrolling = _scrollListener._scrolling;
		}
		
		// 스크롤이 멈추고 마지막 아이템일 경우엔 한번만 notifyDataSetChanged가 일어날 수 있도록 _lastChange를 설정한다.
		if ( _scrollListener._lastItmeCount == firstVisibleItem) {
			_scrollListener._lastChange = true;
		}
		else {
			_scrollListener._lastChange = false;
		}
	}*/
	
	
	/**
	 * {@link ListScrollListener}에서 전달되는 상태값을 가지고 해당 activity에서 수행 할수 있는 작업을 한다.
	 * @author gue
	 * @since 2012. 7. 17.
	 * @copyright Copyright.2011.gue.All rights reserved.
	 * @version 
	 * @history <ol>
	 * 		<li>변경자/날짜 : 변경사항</li>
	 * </ol>
	 */
	public interface ListScroll {
		/**
		 * 
		 * @param firstVisibleItem 현재 리스트의 보여지는 최상단 아이템의 position
		 * @param object 현재 listener의 변수를 사용하기 위해 현재 {@link ListScrollListener}를 전달 
		 * @author gue
		 * @since 2012. 7. 17.
		 * @history <ol>
		 * 		<li>변경자/날짜 : 변경사항</li>
		 * </ol>
		 */
		public void onScroll(int firstVisibleItem, Object object);
	}
}
