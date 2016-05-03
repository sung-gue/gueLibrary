package com.breakout.sample.image_filter_gue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Const;
import com.breakout.sample.R;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.img.ImageAlter;
import com.breakout.util.img.ImageUtil;

public class ImageFilterActivity extends BaseActivity implements OnClickListener {
	private final String TAG = "ImageFilter";
//	private final String EXTRA_OUTPUT_PATH = "ex_output_path"; 
//	private final String EXTRA_IMAGE_PATH = "ex_iamge_path";
//	private final String SAVE_FOLDER_NAME = "ius";
	
	private Bitmap _bitmapOriginal;
	private Bitmap _bitmapAlter;
	/**
	 * 원본 이미지의 uri
	 */
	private Uri _originalImageUri;
	private String _originalImagePath;
	
	/**
	 * 필터에서 변경된 이미지의 저장 경로
	 */
	private String _outputImageFilePath;
	private int _currentDegree;
	private int _currentFilterType = R.id.llFOriginal;
	
	/**
	 * <li>[0] : width</li>
	 * <li>[1] : height</li>
	 */
	private int _displaySize[];

	private Button _btRotate;
	private Button _btOk;
	private Button _btCancel;
	private ImageView _ivImage;
	private ImageView _ivImageFilter;
	private ProgressBar _progress;
	
	private LinearLayout[] _llFilter;
	
	private boolean _isFiltering;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("Image Filter gue : 이미지 필터 선택");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_filter);
		
		_displaySize = DeviceUtil.getDisplaySize(this); 
		
		Intent intent = getIntent();
		if (intent != null ) {
			_originalImageUri = intent.getData();
			_originalImagePath = intent.getStringExtra(Const.IMAGE_PATH);
			_outputImageFilePath = intent.getStringExtra(Const.OUTPUT_PATH);
			_bitmapOriginal = ImageUtil.getStaticBitmap();
		}
		
		super.initUI();
	}
	
	@Override
	protected void initTitle() {
	}

	@Override
	protected void initFooter() {
	}
	
	@Override
	protected void initBody() {
		_btRotate = (Button) findViewById( R.id.btRotate );
		_btOk = (Button) findViewById( R.id.btOk );
		_btCancel = (Button) findViewById( R.id.btCancel );
		_ivImage = (ImageView) findViewById( R.id.ivImage );
		_ivImageFilter = (ImageView) findViewById( R.id.ivImageFilter );
		_progress = (ProgressBar) findViewById( R.id.progress );
		
		_llFilter = new LinearLayout[] {
				(LinearLayout) findViewById( R.id.llFOriginal ),
				(LinearLayout) findViewById( R.id.llFGray ),
				(LinearLayout) findViewById( R.id.llFilter02 ),
				(LinearLayout) findViewById( R.id.llFilter03 ),
				(LinearLayout) findViewById( R.id.llFilter04 ),
				(LinearLayout) findViewById( R.id.llFilter05 ),
				(LinearLayout) findViewById( R.id.llFilter06 ),
				(LinearLayout) findViewById( R.id.llFilter07 ),
				(LinearLayout) findViewById( R.id.llFilter08 ),
				(LinearLayout) findViewById( R.id.llFilter09 ),
				(LinearLayout) findViewById( R.id.llFilter10 ),
				(LinearLayout) findViewById( R.id.llFilter11 )
		};
		
		_btRotate.setOnClickListener(this);
		_btOk.setOnClickListener(this);
		_btCancel.setOnClickListener(this);
		for (LinearLayout ll : _llFilter) {
			ll.setOnClickListener(this);
		}
		
		if (_originalImageUri==null || _originalImagePath==null || _outputImageFilePath==null){ 
			Toast.makeText(this, "image data null", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "image data null - uri: " + _originalImageUri + " / path" + _originalImagePath + " / output" + _outputImageFilePath);
			finish();
		} else {
			if (_bitmapOriginal != null) {
				_bitmapAlter = _bitmapOriginal.copy(_bitmapOriginal.getConfig(), true);
				_imageHandler.sendEmptyMessage(1);
			}
			else {
				startImageAlter(R.id.llFOriginal);
			}
		}
	}

	@Override
	protected void refreshUI() {
	}
	
	private void loadImage(Uri uri) {
		System.out.println("loadImage : " + uri.toString());
//		_mediaUri = null;
//		_outputFilePath = null;
//		ImageUtil.recycleBitmap(_bitmapAddFilter);
//		_bitmapAddFilter = null;
		
		
//		Toast.makeText(this, "loadImage() fail image read", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * @param filterType
	 * R.id.llFOriginal : 이미지 초기화<br>
	 */
	private void startImageAlter(final int filterType){
		// progress visible
		_isFiltering = true;
		_progress.setVisibility(View.VISIBLE);
		_ivImageFilter.setImageBitmap(null);
		
		// bitmap thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = _imageHandler.obtainMessage();
				if (filterType == 0) msg.what = 1;
				else msg.what = 2;
				try {
					ViewGroup topView = (ViewGroup) _ivImage.getParent().getParent().getParent();
					Log.i(TAG, 	topView.getChildCount() + " : " + 
								topView.getChildAt(0).getHeight() + " / " +
								topView.getChildAt(1).getHeight() + " / " +
								topView.getChildAt(2).getHeight());
					int height = topView.getChildAt(1).getHeight() + topView.getChildAt(2).getHeight();
					
					if (filterType == R.id.llFOriginal ) {
						if (_bitmapOriginal == null) {
							_bitmapOriginal = ImageUtil.getBitmapResize(_originalImagePath, _displaySize[0], (_displaySize[1]-height)/2);
						}
						if (_bitmapAlter != null) {
							_bitmapAlter.recycle();
							_bitmapAlter = null;
						}
						_bitmapAlter = _bitmapOriginal.copy(_bitmapOriginal.getConfig(), true);
						_bitmapAlter = ImageAlter.getRotateBitmap(_bitmapAlter, (_currentDegree%360), true);
					} else if (filterType == R.id.btOk ) {
						msg.what = 3;
//						Bitmap bitmap = ImageUtil.getBitmapFixWidth(_originalImagePath, 800, Config.ARGB_8888);
//						filterProcess(_currentFilterType);
//						bitmap = ImageAlter.getRotateBitmap(_bitmapAlter, (_currentDegree%360), true);
					} else {
						filterProcess(filterType);
					}
					
				} catch (OutOfMemoryError e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				} catch (Exception e) {
					msg.what = -2;
					msg.obj = e;
					e.printStackTrace();
				} finally {
					if (!isFinishing()) _imageHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	private void filterProcess(int filterType) throws OutOfMemoryError, Exception{
		switch (filterType){
			case R.id.btRotate:{
				_currentDegree += 90;
				_bitmapAlter = ImageAlter.getRotateBitmap(_bitmapAlter, 90, true);
				break;
			}
			case R.id.llFGray:{
				_bitmapAlter = ImageAlter.FilterGray(_bitmapAlter, true);
				break;
			}
		
		}
	}
	
	/**
	 * 이미지를 decode 한 후 imageView에 해당 bitmap을 할당한다.
	 */
	private Handler _imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			_progress.setVisibility(View.GONE);
			Message newMsg = Message.obtain(msg);
			switch (newMsg.what) {
				case 1:
					_ivImage.setImageBitmap( _bitmapOriginal );
					Log.i(TAG, "original image size : " + _bitmapOriginal.getWidth() + "x" + _bitmapOriginal.getHeight() );
				case 2:
					_ivImageFilter.setImageBitmap( _bitmapAlter );
					Log.i(TAG, "original image size : " + _bitmapOriginal.getWidth() + "x" + _bitmapOriginal.getHeight() );
					Log.i(TAG, "alter image size : " + _bitmapAlter.getWidth() + "x" + _bitmapAlter.getHeight() );
					break;
				case 3:{
//					Intent intent = new Intent();
//					setResult(RESULT_OK, intent);
					Toast.makeText(getApplicationContext(), "파일이 저장 되었습니다.", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
				case -1:
				case -2:
					Toast.makeText(getApplicationContext(), ((Throwable)newMsg.obj).toString(), Toast.LENGTH_SHORT).show();
					Log.i(TAG, "convert image error: " + ((Throwable)newMsg.obj).toString());
					break;
			}
			_isFiltering = false;
		}
	};
	
	
/* ************************************************************************************************
 * TODO listener
 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (_isFiltering || _currentFilterType == id) return;
		if (id == R.id.btCancel) {
			setResult(RESULT_CANCELED);
			finish();
		} 
		else {
			if (id != R.id.btRotate && id != R.id.btOk) {
				for (LinearLayout ll : _llFilter) {
					if (ll.getId() == id) {
						ll.setBackgroundResource(R.drawable.box_square_white);
					} else {
						ll.setBackgroundResource(0);
					}
				}
				_currentFilterType = id;
			}
			startImageAlter(id);
		}
	}
	
	
	private void aa(){
		try {
			_bitmapOriginal = ImageAlter.FilterGray(_bitmapOriginal, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bitmap output = Bitmap.createBitmap(_bitmapOriginal.getWidth(), _bitmapOriginal.getHeight(), _bitmapOriginal.getConfig());
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		
		ColorMatrix cm = new ColorMatrix();
		float contrast = 2;
		float brightness = -80;
//		cm.set(new float[] {
//				contrast,	0,			0,			0,			brightness,
//				0,			contrast,	0,			0,			brightness,
//				0,			0,			contrast,	0,			brightness,
//				0,			0,			0,			contrast,	0,
//		});
		cm.setSaturation(0);
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		
		
		Matrix matrix = new Matrix();
		matrix.setScale(1, 1);
//		m.setRotate(90, (float) _bitmapOriginal.getWidth() / 2, (float) _bitmapOriginal.getHeight() / 2);
		try {
			canvas.drawBitmap(_bitmapOriginal, matrix, paint);
//			output = Bitmap.createBitmap(_bitmapOriginal, 0, 0, _bitmapOriginal.getWidth(), _bitmapOriginal.getHeight(), matrix, true);
			if (_bitmapOriginal != output) {
				_bitmapOriginal.recycle();
				_bitmapOriginal = null;
				_bitmapOriginal = output;
				output = null; 
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
/* ************************************************************************************************
 * TODO life cycle
 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);		// activity가 살아있을 경우 intent 전달
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// incoming image
		Intent intent = getIntent();
		if ( intent != null ) {
			String action = intent.getAction();
			if ( action != null ) {
				if ( Intent.ACTION_SEND.equals( action ) ) {
					Bundle extras = intent.getExtras();
					if ( extras != null && extras.containsKey( Intent.EXTRA_STREAM ) ) {
						Uri uri = (Uri) extras.get( Intent.EXTRA_STREAM );
						Log.i( TAG, "onResume() ACTION_SEND uri : " + uri );
						loadImage( uri );
					}
				} else if ( Intent.ACTION_VIEW.equals( action ) ) {
					Uri uri = intent.getData();
					Log.i( TAG, "onResume() ACTION_VIEW uri : " + uri );
					loadImage( uri );
				}
			}
			setIntent(new Intent());
		}
	}
	
	@Override
	protected void onDestroy() {
		ImageUtil.recycleBitmap(_bitmapOriginal);
		ImageUtil.recycleBitmap(_bitmapAlter);
		_bitmapOriginal = null;
		_bitmapAlter = null;
		super.onDestroy();
	}

}
