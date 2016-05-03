package com.breakout.sample.image_filter_gue;

import java.io.File;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Const;
import com.breakout.sample.R;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.breakout.util.Util;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.img.ImageUtil;
import com.breakout.util.string.StringUtil;

public class ImageSelectActivity extends BaseActivity implements OnClickListener {
	private final int ACTION_REQUEST_GALLERY = 10;
	private final int ACTION_REQUEST_CAMERA = 11;
	private final int ACTION_REQUEST_FILTER = 12;
	
	
	private Bitmap _bitmapOriginal;
	private Bitmap _bitmapThumb1;
	private Bitmap _bitmapThumb2;
	
	private final String SAVE_FOLDER_NAME = "ius";
	private File _galleryFolder;
	
	/**
	 * 필터로 넘기기전 원본 이미지의 uri
	 */
	private Uri _originalImageUri;
	private String _originalImagePath;
	
	/**
	 * 필터에서 변경된 이미지의 저장 경로
	 */
	private String _outputImageFilePath;
	
	private int _displayWidthDP, _displayHeightDP;
	private int _displayWidth, _displayHeight;

	private Button _btGallery;
	private Button _btCamera;
	private Button _btEdit;
	private ImageView _ivImage;
	private ImageView _ivImageThumb1;
	private ImageView _ivImageThumb2;
	private ProgressBar _progress;
	private View _llIimageBox;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_select);
		setTitle("Image Filter gue : 이미지 선택");
		
		final DisplayMetrics m = getResources().getDisplayMetrics();
		_displayWidth = m.widthPixels;
		_displayHeight = m.heightPixels;
		_displayWidthDP = (int) (  _displayWidth * 1.0 / m.density );
		_displayHeightDP = (int) (  _displayHeight * 1.0 / m.density );
		
		Log.i(TAG, "onCreate() device screen size - px : " + _displayWidth + " x " + _displayHeight + " / dp : " + _displayWidthDP + " x " + _displayHeightDP);
		Log.i(TAG, "onCreate() device density = " + m.density + " / densityDpi = " + m.densityDpi);
		
		if (Util.isExternalStorageAvailable()) {
			_galleryFolder = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ), SAVE_FOLDER_NAME);
			_galleryFolder.mkdir();
		} else {
			AlertDialog dialog = new AlertDialog.Builder( this )
				.setTitle( "알림" )
				.setMessage( "폰에 외장메모리가 없다면 사용불가!!" )
				.setPositiveButton("종료", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
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
		_btGallery = (Button) findViewById( R.id.btGallery );
		_btCamera = (Button) findViewById( R.id.btCamera );
		_btEdit = (Button) findViewById( R.id.btEdit );
		_ivImage = (ImageView) findViewById( R.id.ivImage );
		_ivImageThumb1 = (ImageView) findViewById( R.id.ivThumb1 );
		_ivImageThumb2 = (ImageView) findViewById( R.id.ivThumb2 );
		_progress = (ProgressBar) findViewById( R.id.progress );
		_llIimageBox = findViewById( R.id.llIimageBox );
		
		_btGallery.setOnClickListener(this);
		_btCamera.setOnClickListener(this);
		_btEdit.setOnClickListener(this);
		_llIimageBox.setOnClickListener(this);
		registerForContextMenu( _llIimageBox );
	}

	@Override
	protected void refreshUI() {
	}
	
	/**
	 * uri를 사용하여 image의 실제 경로를 구한다. 
	 * @author gue
	 */
	private void loadImage(final Uri uri) {
		if (uri == null) {
			Toast.makeText(this, "loadImage() image uri load fail", Toast.LENGTH_SHORT).show();
			return;
		} 
		
		String scheme = uri.getScheme();
		initVariable();
		_btEdit.setEnabled( false );
		
		if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor c = ImageUtil.getCursorOfUri(getApplicationContext(), uri, null, null, null, null);
			if (c != null ) {
				if (c.moveToNext()){
					_originalImagePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					long mediaId = c.getLong(c.getColumnIndex(BaseColumns._ID));
					Log.i( TAG, "loadImage() pick content scheme image - id : " + mediaId + " / path : " + _originalImagePath );
					run(uri);
				}
				c.close();
			}
			return;
		} 
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			_originalImagePath = uri.getPath();
			Log.i( TAG, "loadImage() pick file scheme image - path : " + _originalImagePath );
			run(uri);
			return;
			
		} 
		
		Toast.makeText(this, "loadImage() fail image read", Toast.LENGTH_SHORT).show();
	}
	
	private void run(final Uri uri){
		// progress visible
		_progress.setVisibility(View.VISIBLE);
		_ivImage.setVisibility(View.GONE);
		
		// bitmap thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = _loadImageHandler.obtainMessage();
				try {
					ViewGroup topView = (ViewGroup) _llIimageBox.getParent().getParent();
					Log.i(TAG, 	topView.getChildCount() + " : " + 
								topView.getChildAt(0).getHeight() + " / " +
								topView.getChildAt(1).getHeight() + " / " +
								topView.getChildAt(2).getHeight());
					int height = topView.getChildAt(1).getHeight() + topView.getChildAt(2).getHeight();
					try {
						_bitmapThumb1 = ImageUtil.getBitmapTinyRatio(_originalImagePath, 100, 100);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					try {
						_bitmapThumb2 = ImageUtil.getBitmapThumb(getApplicationContext(), uri);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					_bitmapOriginal = ImageUtil.getBitmapResize(_originalImagePath, _displayWidth-100, _displayHeight-38-height-100);
					_originalImageUri = uri;
					msg.what = 1;
				} catch (OutOfMemoryError e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				} catch (Exception e) {
					msg.what = -2;
					msg.obj = e;
					e.printStackTrace();
				} finally {
					if (!isFinishing()) _loadImageHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	/**
	 * 이미지를 decode 한 후 imageView에 해당 bitmap을 할당한다.
	 */
	private Handler _loadImageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			_progress.setVisibility(View.GONE);
			_ivImage.setVisibility(View.VISIBLE);
			Message newMsg = Message.obtain(msg);
			switch (newMsg.what) {
				case 1:
					_ivImage.setImageBitmap( _bitmapOriginal );
					ImageUtil.setStaticBitmap(_bitmapOriginal);
					_ivImageThumb1.setImageBitmap( _bitmapThumb1 );
					_ivImageThumb2.setImageBitmap( _bitmapThumb2 );
					_btEdit.setEnabled( true );
					Log.i(TAG, "convert image size : " + _bitmapOriginal.getWidth() + "x" + _bitmapOriginal.getHeight() );
					if (_bitmapThumb1 != null) Log.i(TAG, "convert image size thumb1 : " + _bitmapThumb1.getWidth() + "x" + _bitmapThumb1.getHeight());
					if (_bitmapThumb2 != null) Log.i(TAG, "convert image size thumb2 : " + _bitmapThumb2.getWidth() + "x" + _bitmapThumb2.getHeight());
					break;
				case -1:
				case -2:
					Toast.makeText(getApplicationContext(), ((Throwable)newMsg.obj).toString(), Toast.LENGTH_SHORT).show();
					Log.i(TAG, "convert image error: " + ((Throwable)newMsg.obj).toString());
					break;
			}
		}
	};
	
	private void initVariable(){
		_originalImageUri = null;
		_originalImagePath = null;
		_outputImageFilePath = null;
		ImageUtil.recycleBitmapInImageView(_ivImage);
		ImageUtil.recycleBitmapInImageView(_ivImageThumb1);
		ImageUtil.recycleBitmapInImageView(_ivImageThumb2);
		_bitmapOriginal = null;
		_bitmapThumb1 = null;
		_bitmapThumb2 = null;
	}
	
	
/* ************************************************************************************************
 * TODO listener
 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btGallery:
				ImageUtil.callGallery(this, ACTION_REQUEST_GALLERY);
				break;
			case R.id.btCamera:
				_originalImageUri = Uri.fromFile(new File( _galleryFolder, "ius_" + System.currentTimeMillis() + ".jpg" ));
				try {
					ImageUtil.callCamera(this, ACTION_REQUEST_CAMERA, _originalImageUri);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.btEdit:
				if ( _originalImageUri != null && _galleryFolder != null)  {
					Intent intent = new Intent(this, ImageFilterActivity.class);
					intent.setData(_originalImageUri);
					_outputImageFilePath = _galleryFolder.getAbsolutePath()+ "/ius_" + System.currentTimeMillis() + ".jpg" ;
					intent.putExtra(Const.IMAGE_PATH, _originalImagePath);
					intent.putExtra(Const.OUTPUT_PATH, _outputImageFilePath);
					startActivityForResult(intent, ACTION_REQUEST_FILTER);
				}
				break;
			case R.id.llIimageBox:
				if (StringUtil.nullCheckB(_originalImagePath))  {
					new AlertDialog.Builder( this )
						.setMessage( "uri : " + _originalImageUri +"\n\npath : " + _originalImagePath )
						.setPositiveButton("확인", null).show();
				}
				break;
		}
	}
	
	
/* ************************************************************************************************
 * TODO call back
 */
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if ( resultCode == RESULT_OK ) {
			switch ( requestCode ) {
				case ACTION_REQUEST_GALLERY:{
					Uri uri = data.getData();
					Log.i(TAG, "onActivityResult() gallery uri : " + uri);
					loadImage(uri);
					break;
				}
				case ACTION_REQUEST_CAMERA:{
					if (_originalImageUri == null) _originalImageUri = ImageUtil._ouputUri;
					DeviceUtil.updateMediaScan(this, _originalImageUri.getPath());
					Log.i(TAG, "onActivityResult() camera uri : " + _originalImageUri);
					loadImage( _originalImageUri );
					break;
				}
				case ACTION_REQUEST_FILTER:{
					Uri uri = data.getData();
					Log.i(TAG, "onActivityResult() filter uri : " + uri);
					loadImage( uri );
					break;
				}
			}
		} else if ( resultCode == RESULT_CANCELED ) {
			switch ( requestCode ) {
				case ACTION_REQUEST_FILTER:
					Log.i(TAG, "onActivityResult() cancel uri : " + _originalImageUri);
					
					if ( _originalImageUri != null && ContentResolver.SCHEME_FILE.equals(_originalImageUri.getScheme())) {
						try {
							File file = new File(_originalImageUri.getPath());
							if (file.exists()) file.delete();
						} catch (Exception e) { }
					}
					initVariable();
					_btEdit.setEnabled( false );
					break;
			}
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
		initVariable();
		super.onDestroy();
	}
	
}
