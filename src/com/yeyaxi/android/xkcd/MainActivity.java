package com.yeyaxi.android.xkcd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;

import com.yeyaxi.android.xkcd.Uitilities.Constants;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new fetchRSS().execute(Constants.XKCD_RSS_URL);
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

	private class fetchRSS extends AsyncTask<String, Void, File> {

		@Override
		protected File doInBackground(String... params) {
			File f = null;
			
			if (isNetworkConnected()) {				
				try {
					f = downloadUrl(params[0]);
					InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
					XMLParser parser = new XMLParser();
					parser.parse(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("MainActivity", "Exception while downloading URL contents.");
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
