package com.breakout.util.device;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.breakout.util.Log;
import com.breakout.util.Util;


/**
 * {@link Util}의 method를 속성에 따라 class로 분리<br/>
 * Camera Util
 * @author gue
 * @since 2013. 10. 2.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class CameraUtil {
	private final static String TAG = "CameraUtil";
	/**
	 * camera instance, use in {@link CameraUtil}<br>
	 * <code>Camera.open();</code>
	 */
	private static Camera _camera;
	
	
	private CameraUtil(){}
	
	
	/**
     * Check if this device has a camera
     * @return false : no camera
     * @author gue
     * @since 2013. 10. 1.
     */
    public final static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    
    /**
     * A safe way to get an instance of the Camera object.
     * @return android.hardware.Camera or null
     * @author gue
     * @since 2013. 10. 1.
     */
    private final static synchronized Camera getCameraInstance() {
		try {
			if (_camera == null) {
				_camera = Camera.open();
				Log.d(TAG, "get new camera object");
			}
		}
		catch (Exception e){
			Log.e(TAG, e.getMessage(), e);
		}
        return _camera;
    }
    
    /**
     * destroy camera instance
     * @author gue
     * @since 2013. 10. 4.
     */
    public final static void destroyCameraInstance() {
    	try {
    		if (_camera != null) {
    			_camera.release(); 
    			_camera = null;
    			Log.d(TAG, "destroy camera object");
    		}
    	}
    	catch (Exception e){
    		Log.e(TAG, e.getMessage(), e);
    	}
    }
    
    /**
     * flash turn on and off
     * @return flash on is true
     * @author gue
     * @since 2013. 10. 2.
     */
    public final static boolean flashOnOff(Camera camera) {
    	boolean isOn = false;
    	try {
			if (camera != null) {
				Parameters param = camera.getParameters();
				List<String> flashModes = param.getSupportedFlashModes();
				if (flashModes == null) return false;	// not exist flash mode
				
				String setFlashMode = Parameters.FLASH_MODE_OFF;
				String currentFlashMode = param.getFlashMode();
				if (Parameters.FLASH_MODE_TORCH.equals(currentFlashMode) || Parameters.FLASH_MODE_ON.equals(currentFlashMode)) {
					/*setFlashMode = Parameters.FLASH_MODE_OFF;*/
					Log.d(TAG, "camera flash off");
				}
				else if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
					setFlashMode = Parameters.FLASH_MODE_TORCH;
					isOn = true;
					Log.d(TAG, "camera flash torch");
				}
				else if (flashModes.contains(Parameters.FLASH_MODE_ON)) {
					setFlashMode = Parameters.FLASH_MODE_ON;
					isOn = true;
					Log.d(TAG, "camera flash on");
				}
				param.setFlashMode(setFlashMode);
				camera.setParameters(param);
				if (isOn) camera.startPreview();
				else camera.stopPreview();
			}
		} catch (Exception e) {
			isOn = false;
			Log.e(TAG, e.getMessage(), e);
		}
    	return isOn;
    }
    
    /**
     * control flash ( use {@link CameraUtil#_camera} )<br>
     * flash turn on and off
     * @return flash on is true
     * @author gue
     * @since 2013. 10. 4.
     */
    public final static boolean flashOnOff() {
    	getCameraInstance();
    	boolean result = flashOnOff(_camera);
    	if (!result) destroyCameraInstance();
    	return result;
    }

    /**
     * get camera flash mode
     * @return {@link Parameters#getFlashMode()}
     * @author gue
     * @since 2013. 10. 4.
     */
    public final static String getCameraInstanceFlashMode(Camera camera) {
    	String mode = null;
    	try {
    		if (camera != null) {
    			mode = camera.getParameters().getFlashMode();
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    	}
    	return mode;
    }
    
    /**
     * get {@link CameraUtil#_camera} FlashMode<br>
     * @return {@link Parameters#getFlashMode()}
     * @author gue
     * @since 2013. 10. 4.
     */
    public final static String getCameraInstanceFlashMode() {
    	return getCameraInstanceFlashMode(_camera);
    }
 
    /**
     * Camera 객체의 flash의 on / off 상태
     * @return true is falsh on
     * @author gue
     * @since 2013. 10. 7.
     */
    public final static boolean isCameraFlashOn(Camera camera) {
    	boolean isOn = false;
    	try {
    		if (camera != null) {
    			Parameters param = camera.getParameters();
    			List<String> flashModes = param.getSupportedFlashModes();
    			if (flashModes != null) {
    				String currentFlashMode = param.getFlashMode();
    				if (Parameters.FLASH_MODE_TORCH.equals(currentFlashMode) || Parameters.FLASH_MODE_ON.equals(currentFlashMode)) {
    					isOn = true;
    				}
    			}
    		}
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
    	}
    	return isOn;
    }
    
    /**
     * get {@link CameraUtil#_camera} FlashMode<br>
     * @return true is falsh on
     * @author gue
     * @since 2013. 10. 7.
     */
    public final static boolean isCameraFlashOn() {
    	return isCameraFlashOn(_camera);
    }
    
    /*private final static String nullCheck(String str){
		if (str != null && str.length()!=0)	return str;
		else return null;
	}
	private final static boolean nullCheckB(String str) {
		return str != null && str.length() != 0;
	}*/
}
