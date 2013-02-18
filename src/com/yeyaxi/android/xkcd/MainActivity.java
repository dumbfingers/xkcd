package com.yeyaxi.android.xkcd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
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
	private static final int MAX_NUM_OF_LOADING = 5;
	LinearLayout mGalleryLayout;
	// The Thumbnail size of the gallery view, will auto-crop the image to square
	private int sampleSize = 500;
	// Frame Border Thickness = 30
	private int sampleFrameSize = 530; 
	// Array to hold the comics' info
	private ArrayList<HashMap<String, String>> comicList;
	private HashMap<String, String> singleComic;
	private String[] urls;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGalleryLayout = (LinearLayout)findViewById(R.id.galleryLayout);
		
//		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//		        
//		String testPath = ExternalStorageDirectoryPath + "/Download/";
//		
//		File targetDirector = new File(testPath);
//
//		File[] files = targetDirector.listFiles();
//		for (File file : files){
//			mGalleryLayout.addView(insertPhoto(file.getAbsolutePath()));
//		} 
		// Begin to parse the RSS
//		new fetchRSS().execute(Constants.XKCD_RSS_URL);
		comicList = new ArrayList<HashMap<String,String>>();
		singleComic = new HashMap<String, String>();
		new FetchComicData().execute(Constants.XKCD_JSON_URL);
		
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
	
	private View insertPhoto(Bitmap bm){
//		Bitmap bm = decodeSampledBitmapFromUri(path, sampleSize, sampleSize);
		
//		Bitmap bm = decodeSampledBitmapFromUrl(path, sampleSize, sampleSize);


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
	
//	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
//		Bitmap bm = null;
//
//		// First decode with inJustDecodeBounds=true to check dimensions
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, options);
//
//		// Calculate inSampleSize
//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//		// Decode bitmap with inSampleSize set
//		options.inJustDecodeBounds = false;
//		bm = BitmapFactory.decodeFile(path, options); 
//
//		return bm;  
//	}
	
	public Bitmap decodeSampledBitmapFromUrl(String urlString, String filename, int reqWidth, int reqHeight) {
		Bitmap bm = null;

		File file;
		try {
			file = downloadUrl(urlString, filename);


			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options); 
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	private class FetchComicImage extends AsyncTask<String, Void, ArrayList<Bitmap>> {

		@Override
		protected ArrayList<Bitmap> doInBackground(String... params) {
			ArrayList<Bitmap> bmList = new ArrayList<Bitmap>();
			for (int i = 0; i < params.length; i++) {
				bmList.add(i, decodeSampledBitmapFromUrl(params[i], comicList.get(i).get("num") + ".png", sampleSize, sampleSize));
			}
			return bmList;
		}
		
		protected void onPostExecute(ArrayList<Bitmap> bmList) {
			for (Bitmap bitmap : bmList) {
				mGalleryLayout.addView(insertPhoto(bitmap));
			}
		}
		
	}

	private class FetchComicData extends AsyncTask<String, Void, File> {
//		boolean isMultiple = false;
		@Override
		protected File doInBackground(String... params) {
			File f = null;
			int count = params.length;
			
//			if (count == 1) {
//				isMultiple = false;
				
				if (isNetworkConnected()) {				
					try {
						f = downloadUrl(params[0], Constants.CACHE_FILE);
						InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
						comicList.add(new JsonParser().readJsonStream(inputStream));
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("MainActivity", "Exception while downloading URL contents.");
					}
				} else {
					return null;
				}
			return f;
		}
		
		protected void onPostExecute(File file) {
			new FetchComicImage().execute(comicList.get(0).get("img"));
		}
		
	}
	
	/**
	 * Download file from given URL
	 * @param urlString the URL of file to be downloaded
	 * @return downloaded file
	 * @throws IOException
	 */
	private File downloadUrl(String urlString, String filename) throws IOException {
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
        if (getExternalFilesDir(null) != null) {
        	cacheDir = getExternalFilesDir(null); // Priorly use External Cache
        } else {
        	cacheDir = getFilesDir();// Use Internal cache instead if external one is not available
        }
        File cache = new File(cacheDir, filename);
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
	
	/**
	 * This method is used to generate URLs for accessing Json data of archived xkcd comics.
	 * @param latestNumberOfURL is the latest number of comics, which is used to build the JSON URLs
	 * @return ArrayList contains the URLs of Json
	 */
	public String[] buildURLsOfJsonToArrayList (long latestNumberOfURL) {
		String url = null;
//		ArrayList<String> result = new ArrayList<String>();
//		String[] result = new String[(int)latestNumberOfURL];
		String[] result = new String[MAX_NUM_OF_LOADING];
//		for (long i = (latestNumberOfURL - 1); i >= 0; i--) {
		for (long i = MAX_NUM_OF_LOADING; i >= 0; i--) {
			url = "http://xkcd.com/" + Long.toString(i) + "/info.0.json";
			result[(int) i] = url;
		}
		return result;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
