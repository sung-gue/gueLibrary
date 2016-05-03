package com.breakout.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.breakout.sample.image_filter_gue.ImageSelectActivity;
import com.breakout.util.string.StringUtil;

public class ImageEntryActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("이미지필터 선택", false);
		
		/*boolean aliveAppTask = false;
		String className = getClass().getName();
		String packageName = getPackageName();
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procesesList = am.getRunningAppProcesses();
		List<RunningTaskInfo> taskList = am.getRunningTasks(procesesList.size());
		for (RunningTaskInfo info : taskList) {
			System.out.println(info.numActivities + " / " + info.numRunning + " / " + info.topActivity);
			if ( 	StringUtil.nullCheck(packageName) != null && packageName.equals(info.baseActivity.getPackageName()) &&
					StringUtil.nullCheck(className) != null && !className.equals(info.baseActivity.getClassName()) ) {
				aliveAppTask = true;
				break;
			}
		}*/
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
        TextView(_vParent, "intent-filter process view");
        
        final Intent intent = getIntent();
        String action = intent.getAction();
        
        Uri uri = intent.getData();
        
        String _uriSchemeHost = null;
        String _uriMsg = null;
		if (uri != null) {
			_uriSchemeHost = uri.getQueryParameter("scheme_host");
			try {
				_uriMsg = java.net.URLDecoder.decode(uri.getQueryParameter("msg"), "utf-8");
			} catch (Exception e) { }
		}
		
		String uriSchemeHost = intent.getStringExtra("scheme_host");
		String uriMsg = intent.getStringExtra("msg");
		if (StringUtil.nullCheckB(uriSchemeHost)) _uriSchemeHost = uriSchemeHost;
		if (StringUtil.nullCheckB(uriMsg)) _uriMsg = uriMsg;
		
		if (StringUtil.nullCheckB(_uriSchemeHost)) {
			String contents = String.format(	"-----------------------------------------\n" +
												"%s | -- from custom scheme uri --\n" +
												"|    uri : %s\n" +
												"|    scheme host : %s\n" +
												"|    msg : %s\n" +
												"|    extra scheme host : %s\n" +
												"|    extra msg : %s\n" +
												"-----------------------------------------", 
												TAG, uri, _uriSchemeHost, _uriMsg, uriSchemeHost, uriMsg) ;
			
			Log.i(TAG, contents );
			TextView(_vParent, contents);
		}
		
		
		
        if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_VIEW.equals(action)) {
        	TextView(_vParent, "entry action name : " + action );
        	
        	Button(_vParent, "Image Filter gue").setOnClickListener(new OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			intent(intent, ImageSelectActivity.class);
        		}
        	});
        	
        	Button(_vParent, "준비중").setOnClickListener(new OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			
        		}
        	});
        }
	}

	@Override
	protected void refreshUI() {
	}
	
	private void intent(Intent intent, Class<?> activity){
		intent.setClass(this, activity);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
}
