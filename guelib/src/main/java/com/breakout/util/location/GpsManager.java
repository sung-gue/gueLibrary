package com.breakout.util.location;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings.Secure;

import com.breakout.util.Log;


/**
 * gps와 network를 사용하여 현재 위치를 가져온다.
 * @author gue
 * @since 2013. 2. 15.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class GpsManager {
	private final String TAG = "GpsManager";
	
	private final int NET_DEFAULT_CANCEL_TIME;
	private final int GPS_DEFAULT_CANCEL_TIME;
	private Activity act;
	private GpsResultListener gpsResultListener;
	private LocationManager locationManager;
	private Timer gpstimer;
	
	
	/**
	 * {@link #getLocationOfNetwork()}, {@link #getLocationOfGps()} 를 사용하여 위치를 받아올수 있다.
	 */
	public GpsManager(Activity act, GpsResultListener gpsResultListener) {
		this.act = act;
		this.gpsResultListener = gpsResultListener;
		this.NET_DEFAULT_CANCEL_TIME = 10000;
		this.GPS_DEFAULT_CANCEL_TIME = 7000;
		locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * {@link #getLocationOfNetwork()}, {@link #getLocationOfGps()} 를 사용하여 위치를 받아올수 있다.
	 */
	public GpsManager(Activity act, GpsResultListener gpsResultListener, int netDefaultCancelTime, int gpsDefaultCancelTime) {
		this.act = act;
		this.gpsResultListener = gpsResultListener;
		this.NET_DEFAULT_CANCEL_TIME = netDefaultCancelTime;
		this.GPS_DEFAULT_CANCEL_TIME = gpsDefaultCancelTime;
		locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * @return true gps on, false gps off
	 * @author gue
	 */
	private boolean checkGpsState(final String providerName) {
		String gs = Secure.getString(act.getContentResolver(), Secure.LOCATION_PROVIDERS_ALLOWED);
		if (gs.indexOf(providerName, 0) < 0) {
			if (act != null) {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gpsResultListener.closeProgress();
						gpsResultListener.offGps(providerName);
					}
				});
			}
			return false;
		} 
		return true;
	}
	
	/*private android.location.GpsStatus.Listener gpsStatusListener = new android.location.GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
				// Gps 연결시도시에 발생 : requestLocationUpdates() 호출
				case android.location.GpsStatus.GPS_EVENT_STARTED:
					System.out.println("xxx onGpsStatusChanged : GPS_EVENT_STARTED");
					break;
				// Gps 연결이 끝났을시에 발생 : removeUpdates() 호출
				case android.location.GpsStatus.GPS_EVENT_STOPPED:
					System.out.println("xxx onGpsStatusChanged : GPS_EVENT_STOPPED");
					break;
				// Gps 연결 시도후 최초 연결시에 발생
				case android.location.GpsStatus.GPS_EVENT_FIRST_FIX:
					System.out.println("xxx onGpsStatusChanged : GPS_EVENT_FIRST_FIX");
					break;
				// Gps와 연결이 되어있는 위성의 상태를 
				case android.location.GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					System.out.println("xxx onGpsStatusChanged : GPS_EVENT_SATELLITE_STATUS");
					android.location.GpsStatus status =  locationManager.getGpsStatus(null);
					System.out.println("xxx onGpsStatusChanged : getMaxSatellites " + status.getMaxSatellites());
					System.out.println("xxx onGpsStatusChanged : getMaxSatellites " + status.getTimeToFirstFix());
					System.out.println("xxx onGpsStatusChanged : getMaxSatellites " + status.getSatellites());
					break;
				default:
					System.out.println("xxx onGpsStatusChanged : default = " + event);
					break;
			}
		}
	};*/
	
	private LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location == null) {
				Log.i(TAG, "onLocationChanged | location null");
				return;
			} else {
				Log.i(TAG, "onLocationChanged | latitude=" + location.getLatitude() + ", longitude=" + location.getLongitude());
			}
			
			if (gpstimer != null ) {
				gpstimer.cancel();
				gpstimer = null;
			}
			cancelUpdates();
			
			gpsResultListener.closeProgress();
			gpsResultListener.success(location);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
				case LocationProvider.AVAILABLE:
					Log.i(TAG, "onStatusChanged | AVAILABLE");
					break;
				case LocationProvider.OUT_OF_SERVICE:
					Log.i(TAG, "onStatusChanged | OUT_OF_SERVICE");
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Log.i(TAG, "onStatusChanged | TEMPORARILY_UNAVAILABLE");
					break;
				default:
					Log.i(TAG, "onStatusChanged | default=" + status);
					break;
			}
		}
		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "onProviderEnabled | provider : " + provider);
		}
		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "onProviderDisabled | provider : " + provider);
		}
	};
	
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			if (gpstimer != null ) {
				gpstimer.cancel();
				gpstimer = null;
			}
			cancelUpdates();
			if (act != null) {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gpsResultListener.closeProgress();
						gpsResultListener.fail();
					}
				});
			}
		}
	};
	
	private void cancelUpdates() {
		locationManager.removeUpdates(listener);
//		locationManager.removeGpsStatusListener(gpsStatusListener);
	}
	
	
	/**
	 * gps가 꺼져 있다면 {@link GpsResultListener#offGps()}가 호출되고 
	 * 켜져 있다면 7초간 gps의 위치를 찾고 7초 후에도 위치를 찾지 못하면  NETWORK_PROVIDER 로 변경하여 5초간 위치를 재탐색 한다.
	 * @author gue
	 */
	public void getLocationOfGps() {
		if (checkGpsState(LocationManager.GPS_PROVIDER)) {
			/*Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			String bestProvider = locationManager.getBestProvider(criteria, true);*/
			
			gpstimer = new Timer();
			gpstimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Log.i(TAG, "getLocationOfGps | change Network Provider");
					getLocationOfNetwork(NET_DEFAULT_CANCEL_TIME / 2);
				}
			}, GPS_DEFAULT_CANCEL_TIME);
			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0, listener);
//			locationManager.addGpsStatusListener(gpsStatusListener);
		}
	}
	
	/**
	 * 위치를 network 기반으로 가져온다.
	 * @author gue
	 * @since 2013. 2. 15.
	 * @history <ol>
	 * 		<li>변경자/날짜 : 변경사항</li>
	 * </ol>
	 */
	public void getLocationOfNetwork() {
		getLocationOfNetwork(NET_DEFAULT_CANCEL_TIME);
	}
	
	private void getLocationOfNetwork(int cancelTime){
		if (checkGpsState(LocationManager.NETWORK_PROVIDER)) {
			gpstimer = new Timer();
			gpstimer.schedule(timerTask, cancelTime);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0, listener);
//			locationManager.addGpsStatusListener(gpsStatusListener);
			
		}
	}
	
	
	
/* *************************************************************************************************
 * interface
 */
	/**
	 * {@link GpsManager}에서 결과를 전달하기 위한 callback interface
	 * @author gue
	 * @since 2013. 2. 15.
	 * @copyright Copyright.2011.gue.All rights reserved.
	 * @version 
	 * @history <ol>
	 * 		<li>변경자/날짜 : 변경사항</li>
	 * </ol>
	 */
	public interface GpsResultListener {
		/**
		 * gps 꺼져 있을시에 작업정의
		 * @param offProviderName {@link LocationManager#NETWORK_PROVIDER} or {@link LocationManager#GPS_PROVIDER}
		 */
		public void offGps(String offProviderName);
		/**
		 * 위치수신 성공시 작업 정의 
		 */
		public void success(Location location);
		/**
		 * 위치수신 실패시 작업 정의 
		 */
		public void fail();
		/**
		 * 모든 작업을 끝낸후에 최초로 실행되는 method<br>
		 * progressBar 등을 띄웠다면 progressBar 종료 코드 정의 
		 */
		public void closeProgress();
	}

/* ************************************************************************************************
 * Use
 */
	class Use {
		/**
		 * GpsManager 사용법
		 * @author gue
		 */
		public void LocationInit() {
			new GpsManager(new Activity(), new GpsResultListener() {
				@Override
				public void success(Location location) {
					Log.i(TAG, "Location.getLongitude() = " + location.getLongitude() + " / Location.getLatitude() = " + location.getLatitude());
				}
				@Override
				public void offGps(String offProviderName) {
					if (LocationManager.GPS_PROVIDER.equalsIgnoreCase(offProviderName) ) {
						Log.i(TAG, "GPS 기능이 꺼져 있습니다.");
					}
					else if (LocationManager.NETWORK_PROVIDER.equalsIgnoreCase(offProviderName) ) {
						Log.i(TAG, "모바일 위치 측정 기능이 꺼져 있습니다.");
					}
				}
				@Override
				public void fail() {
					Log.i(TAG, "GPS 수신에 실패하였습니다.");
				}
				@Override
				public void closeProgress() {
				}
			}).getLocationOfNetwork(); // network 상태 확인 후 network로 위치받기
			// }).getLocationOfGps(); // gps 상태 확인 후 gps로 위치받기
		}
	}
}