package com.yeyaxi.android.xkcd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

public class JsonParser {
	
	private static final String TAG = "JsonParser";

	public Comic readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readInfoToHashMap(reader);
		}
		finally {
			reader.close();
		}

	}

	private Comic readInfoToHashMap (JsonReader reader) throws IOException {
		Comic comic = new Comic();
//		long num = 0;
//		String alt = null;
//		String img = null;
//		String title = null;		

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("month")) {
				comic.setMonth(reader.nextInt());
			} else if (name.equals("num")) {
//				num = reader.nextLong();
				comic.setNum(reader.nextLong());
			} else if (name.equals("year")) {
				comic.setYear(reader.nextInt());
			} else if (name.equals("safe_title")) {
//				title = reader.nextString();
				comic.setSafe_title(reader.nextString());
			} else if (name.equals("transcript") 
						&& reader.peek() != JsonToken.NULL) {
				comic.setTranscript(reader.nextString());
			} else if (name.equals("alt")) {
//				alt = reader.nextString();
				comic.setAlt(reader.nextString());
			} else if (name.equals("img")) {
				String url = reader.nextString();
				URL imgUrl = new URL(url);
				comic.setImg(imgUrl);
				Log.i(TAG, "URL: " + imgUrl);
			} else if (name.equals("day")) {
				comic.setDay(reader.nextInt());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
//		HashMap<String, String> result = new HashMap<String, String>();
//		result.put("num", Long.toString(num));
//		result.put("alt", alt);
//		result.put("img", img);
//		result.put("title", title);
//		
//		Log.i(TAG, num + alt + img + title);
//		return result;
		return comic;
	}

}

