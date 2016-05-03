package com.breakout.sample.hybrid.phonegap;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CordovaInterface;

import android.os.Bundle;
import android.view.KeyEvent;

public class PhonegapActivity extends DroidGap implements CordovaInterface {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		super.onCreate(savedInstanceState);
		
		super.loadUrl(Config.getStartUrl());
		
	}
	
	@Override
	public void init(CordovaWebView webView, CordovaWebViewClient webViewClient, CordovaChromeClient webChromeClient) {
		super.init(webView, webViewClient, webChromeClient);
		/*super.appView.setWebViewClient(new CordovaWebViewClient(this, webView) {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("shouldOverrideUrlLoading : " + url);
				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				System.out.println("onPageStarted : " + url);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				System.out.println("onPageFinished : " + url);
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				System.out.println("onReceivedError : " + failingUrl);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
		});*/
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if (keyCode == KeyEvent.KEYCODE_BACK && !super.appView.canGoBack()) {
			super.appView.loadUrl("javascript:[onBackKeyDown()]");
			return true;
		}*/
		return super.onKeyDown(keyCode, event);
	}
	
		
//	@Override
//	public Activity getActivity() {
//		System.out.println("getActivity : ");
//		return this;
//	}
//
//	@Override
//	public ExecutorService getThreadPool() {
//		System.out.println("getThreadPool : ");
//		return null;
//	}
//
//	@Override
//	public Object onMessage(String arg0, Object arg1) {
//		System.out.println("onMessage : " + arg0 + " / " + arg1);
//		return null;
//	}
//
//	@Override
//	public void setActivityResultCallback(CordovaPlugin arg0) {
//		System.out.println("setActivityResultCallback : " + arg0);
//	}
//
//	@Override
//	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
//		System.out.println("CordovaPlugin : " + arg0 + " / " + arg1 + " / " + arg2);
//	}

		
		

}