package com.yeyaxi.android.xkcd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.util.JsonReader;
import android.util.Log;

public class JsonParser {
	
	private static final String TAG = "JsonParser";

	public HashMap<String, String> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readInfoToHashMap(reader);
		}
		finally {
			reader.close();
		}

	}

	public HashMap<String, String> readInfoToHashMap (JsonReader reader) throws IOException {
		
		long num = 0;
		String alt = null;
		String img = null;
		String title = null;		

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("num")) {
				num = reader.nextLong();
			} else if (name.equals("alt")) {
				alt = reader.nextString();
			} else if (name.equals("img")) {
				img = reader.nextString();
			} else if (name.equals("title")) {
				title = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("num", Long.toString(num));
		result.put("alt", alt);
		result.put("img", img);
		result.put("title", title);
		
		Log.i(TAG, num + alt + img + title);
		return result;
	}

//	public List readDoublesArray(JsonReader reader) throws IOException {
//		List doubles = new ArrayList();
//
//		reader.beginArray();
//		while (reader.hasNext()) {
//			doubles.add(reader.nextDouble());
//		}
//		reader.endArray();
//		return doubles;
//	}

	//	public User readUser(JsonReader reader) throws IOException {
	//		String username = null;
	//		int followersCount = -1;
	//
	//		reader.beginObject();
	//		while (reader.hasNext()) {
	//			String name = reader.nextName();
	//			if (name.equals("name")) {
	//				username = reader.nextString();
	//			} else if (name.equals("followers_count")) {
	//				followersCount = reader.nextInt();
	//			} else {
	//				reader.skipValue();
	//			}
	//		}
	//		reader.endObject();
	//		return new User(username, followersCount);
	//	}
}

