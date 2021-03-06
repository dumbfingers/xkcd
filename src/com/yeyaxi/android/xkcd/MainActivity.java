package com.yeyaxi.android.xkcd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fima.cardsui.views.CardUI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yeyaxi.android.xkcd.Uitilities.Constants;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	// If set this MAX NUM to 0, means unlimited loading
	private static int MAX_NUM_OF_LOADING = 0;
	private CardUI mCardView;
	private ProgressBar pgWheel;
//	private ProgressBar pgBar;
	
	private int cardWidth;
	private int cardHeight;
	
	DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
		pgWheel = (ProgressBar)findViewById(R.id.progressBar1);
//		pgBar = (ProgressBar)findViewById(R.id.progressBar2);
		
		cardWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 154f, getResources().getDisplayMetrics());
		cardHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 258f, getResources().getDisplayMetrics());
			
		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.build();
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder()
				.cacheOnDisc(true)
				.build();
		
//		new FetchSingleComicData().execute(Constants.XKCD_JSON_URL);
		fetchSingleComicJson.execute(Constants.XKCD_JSON_URL);
		

		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	public Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		
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
	
	AsyncTask<String, Void, Comic> fetchSingleComicJson = new AsyncTask<String, Void, Comic>() {

		@Override
		protected Comic doInBackground(String... params) {
			Comic comic = null;
			
//			int count = params.length;
			if (isNetworkConnected()) {				
				try {
					URL url = new URL(params[0]);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(15000); // milliseconds
					conn.setConnectTimeout(20000); // milliseconds
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					// Starts the query
					conn.connect();
					InputStream stream = conn.getInputStream();
					comic = new JsonParser().readJsonStream(stream);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("MainActivity", "Exception while downloading URL contents.");
				}
			} else {
				return null;
			}

			return comic;
		}
		
		@Override
		protected void onPostExecute(Comic comic) {
			if (comic != null) {
				ArrayList<String> urlList = buildURLsOfJsonToArrayList(comic.getNum());
				String[] urlArray = new String[urlList.size()];
				fetchComic.execute(urlList.toArray(urlArray));
			}
		}
		
	};
	
	AsyncTask<String, Void, ArrayList<Comic>> fetchComic = new AsyncTask<String, Void, ArrayList<Comic>>() {

		@Override
		protected ArrayList<Comic> doInBackground(String... params) {
			ArrayList<Comic> comicList = null;
			ArrayList<File> fileList = null;
			if (isNetworkConnected()) {
				fileList = new ArrayList<File>(MAX_NUM_OF_LOADING);
				comicList = new ArrayList<Comic>(MAX_NUM_OF_LOADING);
				for (int i = 0; i < params.length; i++) {
					try {
						URL url = new URL(params[i]);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setReadTimeout(15000); // milliseconds
						conn.setConnectTimeout(20000); // milliseconds
						conn.setRequestMethod("GET");
						conn.setDoInput(true);
						// Starts the query
						conn.connect();
						InputStream stream = conn.getInputStream();
						Comic comic = new JsonParser().readJsonStream(stream);
						// Download the image to cache using the image number as file name
						fileList.add(downloadUrl(comic.getImg(), String.valueOf(comic.getNum())));
						comicList.add(comic);
						// Publish progress
//						publishProgress((int)(i/params.length) * 100);
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("MainActivity", "Exception while downloading URL contents.");
					}
				}
			}
			return comicList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Comic> comicList) {
			if (comicList != null) {
				for (Comic comic : comicList) {
					final String title = comic.getSafe_title();
					ImageLoader.getInstance().loadImage(comic.getImg().toString(), options, new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap bm) {
							ComicCard card = new ComicCard(title, bm);
							mCardView.addCardToLastStack(card, true);
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub
							
						}
					}); 
				}
			}
		}
		
	};
	
//	private class FetchSingleComicData extends AsyncTask<String, Void, Comic> {
//	
//		File imgFile = null;
//		
//		@Override
//		protected Comic doInBackground(String... params) {
//			Comic comic = null;
//			
////			int count = params.length;
//			if (isNetworkConnected()) {				
//				try {
//					URL url = new URL(params[0]);
//					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//					conn.setReadTimeout(15000); // milliseconds
//					conn.setConnectTimeout(20000); // milliseconds
//					conn.setRequestMethod("GET");
//					conn.setDoInput(true);
//					// Starts the query
//					conn.connect();
//					InputStream stream = conn.getInputStream();
//					comic = new JsonParser().readJsonStream(stream);
//					// Download the image to cache using the image number as file name
//					imgFile = downloadUrl(comic.getImg(), String.valueOf(comic.getNum()));
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//					Log.e("MainActivity", "Exception while downloading URL contents.");
//				}
//			} else {
//				return null;
//			}
//
//			return comic;
//		}
//		
//		@Override
//		protected void onProgressUpdate(Void... v) {
//			
//		}
//		
//		@Override
//		protected void onPostExecute(Comic comic) {
//			if (comic != null) {
//				Bitmap bm = decodeSampledBitmapFromFile(imgFile, cardWidth, cardHeight);
//				ComicCard card = new ComicCard(comic.getSafe_title(), bm);
//				mCardView.addCard(card);
//				mCardView.refresh();
//				new FetchComicsData().execute(buildURLsOfJsonToArrayList(comic.getNum()));
//			}
//		}
//	
//	}
//	
//	
//	private class FetchComicsData extends AsyncTask<String, Integer, ArrayList<Comic>> {
//
//		ArrayList<File> fileList;
//		@Override
//		protected ArrayList<Comic> doInBackground(String... params) {
//			ArrayList<Comic> comicList = null;
//			if (isNetworkConnected()) {
//				fileList = new ArrayList<File>(MAX_NUM_OF_LOADING);
//				comicList = new ArrayList<Comic>(MAX_NUM_OF_LOADING);
//				for (int i = 0; i < params.length; i++) {
//					try {
//						URL url = new URL(params[i]);
//						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//						conn.setReadTimeout(15000); // milliseconds
//						conn.setConnectTimeout(20000); // milliseconds
//						conn.setRequestMethod("GET");
//						conn.setDoInput(true);
//						// Starts the query
//						conn.connect();
//						InputStream stream = conn.getInputStream();
//						Comic comic = new JsonParser().readJsonStream(stream);
//						// Download the image to cache using the image number as file name
//						fileList.add(downloadUrl(comic.getImg(), String.valueOf(comic.getNum())));
//						comicList.add(comic);
//						// Publish progress
////						publishProgress((int)(i/params.length) * 100);
//					} catch (IOException e) {
//						e.printStackTrace();
//						Log.e("MainActivity", "Exception while downloading URL contents.");
//					}
//				}
//			}
//			return comicList;
//		}
//		
//		@Override
//		protected void onProgressUpdate(Integer... progress) {
//			pgWheel.bringToFront();
////			pgBar.setProgress(progress[0]);
//		}
//		
//		@Override
//		protected void onPostExecute(ArrayList<Comic> comicList) {
//			// Add multiple card to stack (if we have that much cards)
////			CardStack stackPlay = new CardStack();
////			stackPlay.setTitle("Cards");
//			mCardView.clearCards();
//			pgWheel.setVisibility(View.GONE);
////			pgBar.setVisibility(View.GONE);
//			if (comicList != null) {
//				for (int i = comicList.size() - 1; i >= 0; i--) {
//					Bitmap bm = decodeSampledBitmapFromFile(fileList.get(i), cardWidth, cardHeight);
//					ComicCard card = new ComicCard(comicList.get(i).getSafe_title(), bm);
//					mCardView.addCardToLastStack(card);
//				}
//			}
//			mCardView.refresh();
//			mCardView.scrollToCard(MAX_NUM_OF_LOADING);
////			mCardView.addStack(stackPlay);
//		}
//		
//	}
	
	/**
	 * Download file from given URL
	 * @param url the URL of file to be downloaded
	 * @return downloaded file
	 * @throws IOException
	 */
	private File downloadUrl(URL url, String filename) throws IOException {
//        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000 /* milliseconds */);
        conn.setConnectTimeout(20000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();     
//        File cacheDir = getStorageDir(getApplicationContext());
        File cache = new File(getCacheDir(), filename);
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
	public ArrayList<String> buildURLsOfJsonToArrayList (long latestNumberOfURL) {
//		String[] result = new String[MAX_NUM_OF_LOADING];
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (MAX_NUM_OF_LOADING == 0) {
			for (long i = latestNumberOfURL; i >= 0; i--) {
				String url = "http://xkcd.com/" + Long.toString(i) + "/info.0.json";
//				result[(int)(latestNumberOfURL - i)] = url;
				result.add(url);
			}
		} else {
			for (long i = latestNumberOfURL; i >= (latestNumberOfURL - MAX_NUM_OF_LOADING); i--) {
				String url = "http://xkcd.com/" + Long.toString(i) + "/info.0.json";
//				result[(int)(latestNumberOfURL - i)] = url;
				result.add(url);
			}
		}
		return result;
		
	}
	
	public File getCacheDir() {
		File cacheDir;
        if (getExternalFilesDir(null) != null) {
        	cacheDir = getExternalFilesDir(null); // Priorly use External Cache
        } else {
        	cacheDir = getFilesDir();// Use Internal cache instead if external one is not available
        }
        return cacheDir;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_fetch:
//	    		new FetchComicData().execute(Constants.XKCD_JSON_URL);
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	

}
