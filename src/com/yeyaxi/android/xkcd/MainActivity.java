package com.yeyaxi.android.xkcd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.yeyaxi.android.xkcd.Uitilities.Constants;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	LinearLayout mGalleryLayout;
	private int sampleSize = 500;
	private int sampleFrameSize = 530; // Frame Border Thickness = 30
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGalleryLayout = (LinearLayout)findViewById(R.id.galleryLayout);
		
		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		        
		String testPath = ExternalStorageDirectoryPath + "/Download/";
		
		File targetDirector = new File(testPath);

		File[] files = targetDirector.listFiles();
		for (File file : files){
			mGalleryLayout.addView(insertPhoto(file.getAbsolutePath()));
		} 
		// Begin to parse the RSS
//		new fetchRSS().execute(Constants.XKCD_RSS_URL);
		new fetchRSS().execute(Constants.XKCD_JSON_URL);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private View insertPhoto(String path){
		Bitmap bm = decodeSampledBitmapFromUri(path, sampleSize, sampleSize);

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(sampleFrameSize, sampleFrameSize));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(sampleSize, sampleSize));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}
	
	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options); 

		return bm;  
	}
	
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight);   
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth);   
			}   
		}

		return inSampleSize;   
	}

	private class fetchRSS extends AsyncTask<String, Void, File> {

		@Override
		protected File doInBackground(String... params) {
			File f = null;
			
			if (isNetworkConnected()) {				
				try {
					f = downloadUrl(params[0]);
					InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
//					XMLParser parser = new XMLParser();
//					parser.parse(inputStream);
					new JsonParser().readJsonStream(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("MainActivity", "Exception while downloading URL contents.");
//				} catch (XmlPullParserException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			} else {
				return null;
			}
			return f;
		}
		
		protected void onPostExecute(File file) {
			
		}
		
	}
	
	/**
	 * Download file from given URL
	 * @param urlString the URL of file to be downloaded
	 * @return downloaded file
	 * @throws IOException
	 */
	private File downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000 /* milliseconds */);
        conn.setConnectTimeout(20000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();     
//        File cacheDir = getStorageDir(getApplicationContext());
        File cacheDir;
        if (getExternalCacheDir() != null) {
        	cacheDir = getExternalCacheDir(); // Priorly use External Cache
        } else {
        	cacheDir = getCacheDir();// Use Internal cache instead if external one is not available
        }
        File cache = new File(cacheDir, Constants.CACHE_FILE);
        FileOutputStream fos = new FileOutputStream(cache);
        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        while ((bufferLength = stream.read(buffer)) > 0) {
        	fos.write(buffer, 0, bufferLength);
        }	            
        fos.flush();
        fos.close();
        return cache;
    }
	
	/**
	 * Check network connectivity.
	 * @return true if the current network is connected.
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/** 
	 * Checks if external storage is available for read and write
	 * @return true if external storage is available for read & write
	 */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/** 
	 * Checks if external storage is available to at least read
	 * @return true if external storage is readable
	 */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Get the directory for the app's private pictures directory. 
	 * @param context
	 * @return storage directory
	 */
	public File getStorageDir(Context context) {
	    File file = context.getExternalCacheDir();
	    if (!file.mkdirs()) {
	        Log.e("XML_CreateFile", "Directory not created");
	    }
	    return file;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
