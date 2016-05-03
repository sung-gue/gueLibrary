package com.breakout.util.img;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.breakout.util.Log;

/**
 * 
 * @author gue
 * @since 2012. 11. 22.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class ImageAlter {
	private final static String TAG = "ImageFilter";
	
/* ************************************************************************************************
 * INFO filter
 */
	public final static Bitmap FilterSaturation(Bitmap bitmapSrc, float saturation, boolean recycle) throws Exception, OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
		
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(saturation);
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}
	
	public final static Bitmap FilterSaturation(Bitmap bitmapSrc, float saturation) throws Exception, OutOfMemoryError {
		return FilterSaturation(bitmapSrc, saturation, true);
	}
	
	public final static Bitmap FilterGray(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
		
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		/*colorMatrix.set(new float[] {
				0.299f,	0.587f,	0.114f,	0,	0,
				0.299f,	0.587f,	0.114f,	0,	0,
				0.299f,	0.587f,	0.114f,	0,	0,
				0,		0,		0,		1,	0,
		});*/
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public final static Bitmap FilterGray(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterGray(bitmapSrc, true);
	}
	
	/*public static Bitmap FilterGray1(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		
		// constant grayscale
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;
		int A, R, G, B;
		int pixel;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; ++y) {
				pixel = bitmapSrc.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				
				// apply grayscale sample
				R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				output.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}*/
	
	public final static Bitmap FilterMilky(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
		
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		canvas.drawColor(Color.WHITE);
		paint.setMaskFilter(new BlurMaskFilter(100, Blur.SOLID));
		
		ColorMatrix colorMatrix = new ColorMatrix();
		float contrast = 1.2f;
		float brightness = 10;
		colorMatrix.set(new float[] {
				contrast,	0,			0,			0,			brightness,
				0,			contrast,	0,			0,			brightness,
				0,			0,			contrast,	0,			brightness,
				0,			0,			0,			contrast,	0,
		});
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle(); 
			bitmapSrc = null;
		}
		return output;
	}
	
	public final static Bitmap FilterMilky(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterMilky(bitmapSrc, true);
	}
	
	public final static Bitmap FilterSharpen(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
		
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		ColorMatrix colorMatrix = new ColorMatrix();
		float contrast = 1.35f;
		float brightness = -45;
		colorMatrix.set(new float[] {
				contrast,	0,			0,			0,			brightness,
				0,			contrast,	0,			0,			brightness,
				0,			0,			contrast,	0,			brightness,
				0,			0,			0,			1,	0,
		});
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public final static Bitmap FilterSharpen(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterSharpen(bitmapSrc, true);
	}
	
	
	/*public final static Bitmap FilterSharpen(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		double[][] config = new double[][] {
				{ 0 , -2    , 0  },
				{ -2, 11	, -2 },
//				{ -2, weight, -2 },
				{ 0 , -2    , 0  }
			};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(config);
		convMatrix.Factor = 11 - 8;
		Bitmap output = ConvolutionMatrix.computeConvolution3x3(bitmapSrc, convMatrix);
		
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}
	
	public final static Bitmap FilterSharpen(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterSharpen(bitmapSrc, true);
	}*/
	
	public final static Bitmap FilterInvert(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
		
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(new float[] {
				-1,	0,	0,	0,	255,
				0,	-1,	0,	0,	255,
				0,	0,	-1,	0,	255,
				0,	0,	0,	1,	0,
		});
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
	    return output;
	}

	public final static Bitmap FilterInvert(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterInvert(bitmapSrc, true);
	}
	
	/*public final static Bitmap FilterInvert1(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int height = bitmapSrc.getHeight();
		int width = bitmapSrc.getWidth();
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		
		int A, R, G, B;
		int pixelColor;
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
	            pixelColor = bitmapSrc.getPixel(x, y);
	            A = Color.alpha(pixelColor);
	            R = 255 - Color.red(pixelColor);
	            G = 255 - Color.green(pixelColor);
	            B = 255 - Color.blue(pixelColor);
	            
	            output.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	    if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}*/
	
	/**
	 * 모두 0일경우 회색<br>
	 * @author gue
	 */
	public final static Bitmap FilterSepiaTone(Bitmap bitmapSrc, int depth, double red, double green, double blue, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		
		// constant grayscale
		final double GS_RED = 0.3;
		final double GS_GREEN = 0.59;
		final double GS_BLUE = 0.11;
		int A, R, G, B;
		int pixelColor;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				pixelColor = bitmapSrc.getPixel(x, y);
				A = Color.alpha(pixelColor);
				R = Color.red(pixelColor);
				G = Color.green(pixelColor);
				B = Color.blue(pixelColor);
				
				// apply grayscale sample
				B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				
				// apply intensity level for sepid-toning on each channel
				R += (depth * red);
				if(R > 255) { R = 255; }

				G += (depth * green);
				if(G > 255) { G = 255; }

				B += (depth * blue);
				if(B > 255) { B = 255; }

				output.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}
	
	public final static Bitmap FilterSepiaTone(Bitmap bitmapSrc, int depth, double red, double green, double blue) throws Exception, OutOfMemoryError {
		return FilterSepiaTone(bitmapSrc, depth, red, green, blue, true);
	}
	
	public static Bitmap FilterReflection(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();      
		
	    Matrix matrix = new Matrix();
	    matrix.preScale(1, -1);
	      
	    Bitmap reflectionBitmap = Bitmap.createBitmap(bitmapSrc, 0, height/4*2, width, height/4, matrix, false);  
	    
	    Canvas canvas = new Canvas(reflectionBitmap);
	    
	    Paint paint = new Paint();
	    LinearGradient shader = new LinearGradient(	0, 0, 
	    											0, height , 
	    											0x70ffffff, 0x00ffffff,
	    											TileMode.CLAMP);
	    paint.setShader(shader);
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	    canvas.drawRect(0, 0, width, height/4, paint);
	    
	    Bitmap output = Bitmap.createBitmap(width, height, Config.RGB_565);
	    canvas = new Canvas(output);
	    canvas.drawBitmap(bitmapSrc, 0, 0, null);
	    
	    paint = new Paint();
	    paint.setColor(Color.WHITE);
	    canvas.drawRect(0, height/4*3, width, height, paint);
	    canvas.drawBitmap(reflectionBitmap, 0, height/4*3, null);
	    reflectionBitmap.recycle();
		reflectionBitmap = null;
	    
	    if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}
	/*public static Bitmap FilterReflection(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();          
		
		final int reflectionGap = 1;
		
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		
		Bitmap reflectionBitmap = Bitmap.createBitmap(bitmapSrc, 0, height/4*2, width, height/4, matrix, false);     
		
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);
		canvas.drawBitmap(bitmapSrc, 0, 0, null);
		
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height/4*3, width, height/4*3 + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionBitmap, 0, height/4*3, null);
		reflectionBitmap.recycle();
		reflectionBitmap = null;
		
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(	0, height/4*3 + reflectionGap, 
				0, height , 
				0x70ffffff, 0x00ffffff,
				TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height/4*3, width, output.getHeight(), paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}*/

	public final static Bitmap FilterReflection(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterReflection(bitmapSrc, true);
	}
	
	/*public static Bitmap FilterReflection1(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();          
		
		// gap space between original and reflected
		final int reflectionGap = 2;
		
		// this will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		
		// create a Bitmap with the flip matrix applied to it.
		// we only want the bottom half of the image
		Bitmap reflectionBitmap = Bitmap.createBitmap(bitmapSrc, 0, height/4*3, width, height/4, matrix, false);     
//	    Bitmap reflectionBitmap = Bitmap.createBitmap(bitmapSrc, 0, height/2, width, height/2, matrix, false);     
		
		// create a new bitmap with same width but taller to fit reflection
	    Bitmap output = Bitmap.createBitmap(width, (height + height/4), Config.ARGB_8888);
//	    Bitmap output = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);
		
		// create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(output);
		// draw in the original image
		canvas.drawBitmap(bitmapSrc, 0, 0, null);
		// draw in the gap
	    Paint defaultPaint = new Paint();
	    canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// draw in the reflection
	    canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);
		reflectionBitmap.recycle();
		reflectionBitmap = null;
		
		// create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
	    LinearGradient shader = new LinearGradient(	0, height, 
										    		0, output.getHeight() + reflectionGap, 
										    		0x70ffffff, 0x00ffffff,
										    		TileMode.CLAMP);
		// set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// draw a rectangle using the paint with our linear gradient
	    canvas.drawRect(0, height, width, output.getHeight() + reflectionGap, paint);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}*/
	
	public static Bitmap FilterEmboss(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		double[][] config = new double[][] {
			{ -1 ,  0, -1 },
			{  0 ,  4,  0 },
			{ -1 ,  0, -1 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(config);
		convMatrix.Factor = 1;
		convMatrix.Offset = 127;
		
		Bitmap output = ConvolutionMatrix.computeConvolution3x3(bitmapSrc, convMatrix);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public final static Bitmap FilterEmboss(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterEmboss(bitmapSrc, true);
	}
	
	public static Bitmap FilterEngrave(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		double[][] config = new double[][] {
				{ -1 , -1, 	0 },
				{ -1 ,  1,  1 },
				{  0 ,  1, 	1 }
			};
//		double[][] config = new double[][] {
//				{ -2 ,  0, 	0 },
//				{  0 ,  2,  0 },
//				{  0 ,  0, 	0 }
//		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(config);
		/*convMatrix.setAll(0);
		convMatrix.Matrix[0][0] = -2;
		convMatrix.Matrix[1][1] = 2;*/
		convMatrix.Factor = 1;
		convMatrix.Offset = 5;
//		convMatrix.Offset = 95;
		
		Bitmap output = ConvolutionMatrix.computeConvolution3x3(bitmapSrc, convMatrix);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public final static Bitmap FilterEngrave(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterEngrave(bitmapSrc, true);
	}

	public static Bitmap FilterNoise(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		
//		final int COLOR_MIN = 0x00;
		final int COLOR_MAX = 0xFF;
		int[] pixels = new int[width * height];
		bitmapSrc.getPixels(pixels, 0, width, 0, 0, width, height);
		Random random = new Random();

		int index = 0;
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// get random color
				int randColor = Color.rgb(	random.nextInt(COLOR_MAX),
											random.nextInt(COLOR_MAX), 
											random.nextInt(COLOR_MAX));
				// OR
				pixels[index] |= randColor;
			}
		}
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		output.setPixels(pixels, 0, width, 0, 0, width, height);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public static Bitmap FilterNoise(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterNoise(bitmapSrc, true);
	}
	
	public static Bitmap FilterSnow(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		
//		final int COLOR_MIN = 0x00;
		final int COLOR_MAX = 0xFF;
		int[] pixels = new int[width * height];
		bitmapSrc.getPixels(pixels, 0, width, 0, 0, width, height);
		Random random = new Random();
		
		int R, G, B, index = 0, thresHold = 250;
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;				
				R = Color.red(pixels[index]);
				G = Color.green(pixels[index]);
				B = Color.blue(pixels[index]);
				// generate threshold
				thresHold = random.nextInt(COLOR_MAX);
				if(R > thresHold && G > thresHold && B > thresHold) {
					pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
				}							
			}
		}
		
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		output.setPixels(pixels, 0, width, 0, 0, width, height);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}

	public static Bitmap FilterSnow(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterSnow(bitmapSrc, true);
	}
	
	public static Bitmap FilterMeanRemoval(Bitmap bitmapSrc, boolean recycle) throws Exception, OutOfMemoryError {
		double[][] MeanRemovalConfig = new double[][] {
			{ -1 , -1, -1 },
			{ -1 ,  9, -1 },
			{ -1 , -1, -1 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(MeanRemovalConfig);
		convMatrix.Factor = 1;
		convMatrix.Offset = 0;
		
		Bitmap output = ConvolutionMatrix.computeConvolution3x3(bitmapSrc, convMatrix);
		if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
				
		return output;
	}

	
	public static Bitmap FilterMeanRemoval(Bitmap bitmapSrc) throws Exception, OutOfMemoryError {
		return FilterMeanRemoval(bitmapSrc, true);
	}
	
	
	/*public static Bitmap FilterDecreaseColor(Bitmap bitmapSrc, int bitOffset, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		
		int A, R, G, B;
		int pixel;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				pixel = bitmapSrc.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				
				// round-off color offset
				R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
				if(R < 0) { R = 0; }
				G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
				if(G < 0) { G = 0; }
				B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
				if(B < 0) { B = 0; }
				
				output.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
	    if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
		return output;
	}*/

	/*public final static Bitmap FilterTint(Bitmap bitmapSrc, int degree, boolean recycle) throws Exception, OutOfMemoryError {
		int width = bitmapSrc.getWidth();
		int height = bitmapSrc.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, bitmapSrc.getConfig());
		
		final double PI = 3.14159;
		final double FULL_CIRCLE_DEGREE = 360.0;
		final double HALF_CIRCLE_DEGREE = 180.0;
		final double RANGE = 256.0;
	
	    int[] pixels = new int[width * height];
	    bitmapSrc.getPixels(pixels, 0, width, 0, 0, width, height);
	
	    int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
	    double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;
	   
	    int S = (int)(RANGE * Math.sin(angle));
	    int C = (int)(RANGE * Math.cos(angle));
	
	    for (int y = 0; y < height; y++) {
	    	for (int x = 0; x < width; x++) {
	    		int index = y * width + x;
	    		int r = ( pixels[index] >> 16 ) & 0xff;
	    		int g = ( pixels[index] >> 8 ) & 0xff;
	    		int b = pixels[index] & 0xff;
	    		RY = ( 70 * r - 59 * g - 11 * b ) / 100;
	    		GY = (-30 * r + 41 * g - 11 * b ) / 100;
	    		BY = (-30 * r - 59 * g + 89 * b ) / 100;
	    		Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
	    		RYY = ( S * BY + C * RY ) / 256;
	    		BYY = ( C * BY - S * RY ) / 256;
	    		GYY = (-51 * RYY - 19 * BYY ) / 100;
	    		R = Y + RYY;
	    		R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
	    		G = Y + GYY;
	    		G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
	    		B = Y + BYY;
	    		B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
	    		pixels[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
	    	}
	    }
	    output.setPixels(pixels, 0, width, 0, 0, width, height);
	    if (bitmapSrc != output && recycle) {
			bitmapSrc.recycle();
			bitmapSrc = null;
		}
	    pixels = null;
	    return output;
	}*/
	
/* ************************************************************************************************
 * INFO convert
 */
	/**
	 * bitmap을 주어진 degree로 회전하여 생성한 bitmap으로 return
	 * @param bitmapSrc 변환할 bitmap 객체
	 * @param degree 회전각
	 * @param recycle 변수로 받은 bitmap을 recycle 처리 할지 여부, 해당 bitmap이 특정 view안에 draw 되어진 상태일때 recycle을 하게 되면 RunTimeException 발생 
	 * @author gue
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmapSrc, int degree, boolean recycle) {
		if (degree != 0 && bitmapSrc != null && !bitmapSrc.isRecycled()) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degree, (float) bitmapSrc.getWidth() / 2, (float) bitmapSrc.getHeight() / 2);
			try {
				Bitmap output = Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
				if (recycle && bitmapSrc != output) {
					bitmapSrc.recycle();
					bitmapSrc = null;
				}
				bitmapSrc = output;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, String.format("getRotateBitmap : bitmap [%s] rotate degree=%d, recycle=[%s] ", bitmapSrc, degree, recycle));
		}
		return bitmapSrc;
	}
	
	/**
	 * bitmap을 주어진 degree로 회전하여 생성한 bitmap으로 return<br>
	 * 입력된 bitmap은 recycle 된다. 
	 * @param bitmap 변환할 bitmap 객체
	 * @param degree 회전각
	 * @author gue
	 * @see #getRotateBitmap(Bitmap, int, boolean)
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmap, int degree) {
		return getRotateBitmap(bitmap, degree, true);
	}

	
	

/* ************************************************************************************************
 * INFO image round corner
 */
	public final static Bitmap roundCorner(Bitmap bitmapSrc, int roundPixel, int width, int height) throws OutOfMemoryError, Exception {
		if (width == 0 ) {
			width = bitmapSrc.getWidth();
			height = bitmapSrc.getHeight();
		} else {
			bitmapSrc = ImageUtil.sizeConvert(bitmapSrc, width, height, false);
		}
		
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff424242);
		
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPixel, roundPixel, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmapSrc, rect, rect, paint);
		
		return output;
	}
	
	public final static Bitmap roundCorner(Bitmap bitmapSrc, int roundPixel) throws OutOfMemoryError, Exception {
		return roundCorner(bitmapSrc, roundPixel, 0, 0);
	}
	
	public final static Bitmap roundCorner(Drawable drawable, int roundPixel, int width, int height) throws OutOfMemoryError, Exception {
		return roundCorner(ImageUtil.drawableToBitmap(drawable), roundPixel, width, height);
		
		/*Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		canvas = new Canvas(output);
		int color = 0xff424242; 
		Paint paint = new Paint(); 
		Rect rect = new Rect(0, 0, width, height); 
		RectF rectF = new RectF(rect); 
		
		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPixel, roundPixel, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return bitmap;*/
	}
	
	public final static Bitmap roundCorner(Drawable drawable, int roundPixel) throws OutOfMemoryError, Exception {
		return roundCorner(drawable, roundPixel, 0, 0);
	}
	
	
/* ************************************************************************************************
 * INFO effect
 */
	/**
	 * 이미지외곽에 하이라이트 삽입, 아이콘류의 투명배경을 가지는 이미지에 적용
	 * @param bitmap
	 * @author gue
	 */
	public static Bitmap doHighlightImage(Bitmap bitmap) {
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth() + 96, bitmap.getHeight() + 96, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		
		Paint paint = new Paint();
		paint.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));
		int[] offsetXY = new int[2];
		Bitmap temp = bitmap.extractAlpha(paint, offsetXY);
		System.out.println(offsetXY[0] + " / " + offsetXY[1] + " / " + bitmap.getWidth() + " / " + bitmap.getHeight() );
		
		Paint paintAlpha = new Paint();
		paintAlpha.setColor(Color.WHITE);
		canvas.drawBitmap(temp, offsetXY[0], offsetXY[1], paintAlpha);
		temp.recycle();

		canvas.drawBitmap(bitmap, 0, 0, null);
		return output;
	}
	
	
}
