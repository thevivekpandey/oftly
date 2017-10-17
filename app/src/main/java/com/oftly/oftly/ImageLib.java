package com.oftly.oftly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ImageLib {
	static int rand_idx = 0;
	public static final String colors[] = {"#6B4F73", "#C55241", "#D69F48", "#456ABB", "#A2A64A", "#4DA165"}; // Google dialer colors
	public static final String EXTERNAL_DIRECTORY = "Oftly";
	public static Bitmap normalize(Bitmap bitmap, int lpwidth, int lpheight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float aspectRatio = (float)height / (float)width;
		float desiredAspectRatio = (float)lpheight / (float) lpwidth;
		Bitmap bResized;
		
		if (desiredAspectRatio > aspectRatio) {
			float newWidth = (float)lpwidth * (float)height / (float)lpheight;
			float newHeight = (float)height;
			Bitmap bCropped = Bitmap.createBitmap(bitmap, (int)(width - newWidth) / 2, 0, (int)newWidth, (int)newHeight);
			bResized = Bitmap.createScaledBitmap(bCropped, lpwidth, lpheight, false);
		} else {
			float newHeight = (float)lpheight * (float)width / (float)lpwidth;
			float newWidth = (float)width;
			Bitmap bCropped = Bitmap.createBitmap(bitmap, 0, (int)(height - newHeight) / 2, (int)newWidth, (int)newHeight);
			bResized = Bitmap.createScaledBitmap(bCropped, lpwidth, lpheight, false);
		}
		
		Bitmap newResized = bResized.copy(Config.ARGB_8888, true);
		
		int height1 = bResized.getHeight();
		int width1 = bResized.getWidth();

		int [] allpixels = new int[height1 * width1];
		bResized.getPixels(allpixels, 0, width1, 0, 0, width1, height1);
		for (int j = 0; j < height1 * width1; j++) {
			int r = Color.red(allpixels[j]);
			int g = Color.green(allpixels[j]);
			int b = Color.blue(allpixels[j]);
			float mul1 = 1.0f;
			
			if (j < height1 * width1 / 2) {
				mul1 = 1.0f;
			} else {
				float frac = ((float)(j - height1 * width1 / 2) / (float)width1) / (float)height1;
				mul1 = 1.0f * (1 - frac);
			}
			
			float mul2;
			if (j % width1 < width1 / 2) {
				mul2 = 1.0f;
			} else {
				float frac = (float)(j % width1 - width1 / 2) / (1.0f * (float)width1); 
				mul2 = 1 - frac;
			}
			float mul = mul1 < mul2 ? mul1 : mul2;
			allpixels[j] = Color.rgb((int)(mul * (float)r), 
									 (int)(mul * (float)g), 
									 (int)(mul * (float)b));
			
			
		}
		newResized.setPixels(allpixels, 0, width1, 0, 0, width1, height1);
		//return toGrayscale(newResized);
		return newResized;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	    bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 12;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	}
	/* Looks only at photo URI */
	public static Bitmap getPhotoBitmap(Context context, String photoURI) {
		if (photoURI == null) {
			return null;
		}
		try {
			return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(photoURI));
		} catch (Exception e) {
			Log.v("VIVEK", "Encountered exception");
			return null;
		}
	}
	
	/* Looks at photo URI and local storage */
	public static Bitmap getPhotoBitmap(Context context, String photoURI, String phone) {
		Bitmap b = getPhotoBitmap(context, photoURI);
		if (b != null) {
			return b;
		}
		return retrieveImage(context, phone);
	}
	
	public static Drawable getContactDrawable(Context context, String photoURI, String phone) {
		Bitmap bitmap = getPhotoBitmap(context, photoURI);
		if (bitmap != null) {
			Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
			return drawable;
		} else {
			bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
			bitmap.eraseColor(getRandomColor(phone));
			Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
			return drawable;
		}
	}
	public static Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}
	public static int getRandomColor(String phone) {
		int i, sum = 0;
		for (i = 0; i< phone.length(); i++) {
			sum += phone.charAt(i);
		}
		return Color.parseColor(colors[sum % colors.length]);
	}
	public static int getRandomColor() {
		return Color.parseColor(colors[getRandomImageIndex()]);
	}
	public static int getRandomImageIndex() {
		rand_idx = (rand_idx + 1) % colors.length;
		return rand_idx;
	}
	public static void storeImage(Context context, Bitmap b, String targetPhone) {
		createDir();
		String path = Environment.getExternalStorageDirectory().toString();
		Util.Log("path = " + path);
		OutputStream fOut = null;
		Util.Log("jpg is " + targetPhone + ".jpg");
		File file = new File(path + "/" + EXTERNAL_DIRECTORY, targetPhone + ".jpg");
		try {
			fOut = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();	
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
												file.getAbsolutePath(),
												file.getName(),
												file.getName());
		} catch (FileNotFoundException e){
			Util.Log("File not found exception happned");
			e.printStackTrace();
		} catch (IOException e) {
			Util.Log("io exception happned");
			e.printStackTrace();
		}
	}
	public static Bitmap retrieveImage(Context context, String targetPhone) {
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path + "/" + EXTERNAL_DIRECTORY, targetPhone + ".jpg");
		if (file.exists()) {
			Util.Log("file exists for " + targetPhone);
			Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
			return bitmap;
		} else {
			Util.Log("No, file does not exist for " + targetPhone);
			return null;
		}
	}
	private static void createDir() {
		File folder = new File(Environment.getExternalStorageDirectory() + "/" + EXTERNAL_DIRECTORY);
	    String path = folder.getPath();
	    
	    if (!folder.exists()) {
	    	folder.mkdir();
	    }
	    if (!folder.exists()) {
	    	Util.Log("Folder does not exist and could not be made either");
	    }
	}
	public static boolean giftedImageExists(String phone) {
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path + "/" + EXTERNAL_DIRECTORY, phone + ".jpg");
		return file.exists();
	}
}
