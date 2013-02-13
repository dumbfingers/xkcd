package com.yeyaxi.android.xkcd;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;


/**
 * XMLParser customed to return IMG url and descriptions
 * @author Yaxi Ye
 *
 */
public class XMLParser {

	private Context c = null;
	private static final String ns = null;
//	private static final String COLUMN_DATE = "DAY";
//	private static final String COLUMN_TIME = "time";
//	private static final String COLUMN_SID = "sid";
//	private static final String COLUMN_SHOWNAME = "ShowName";
//	private static final String COLUMN_EP = "Ep";
//	private static final String COLUMN_TITLE = "Title";
//	long newRowId;
	private ArrayList<HashMap<String, String>> feed = new ArrayList<HashMap<String, String>>();
	
	public XMLParser(Context context) {
		this.c = context;
	}
		
	public ArrayList<HashMap<String, String>> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            //return readSchedule(parser);
            readRSS(parser);
//            Log.i("DB_ROW", "Row " + newRowId);
        } finally {
            in.close();
        }
		return feed;
    }
	
	private void readRSS(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		
	    parser.require(XmlPullParser.START_TAG, ns, "rss");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the channel tag
	        if (name.equals("channel")) {
	            //scheduleList.add(readDAY(parser));
	        	//parser.getAttributeValue(0);
	        	//if (this.compareDate(parser.getAttributeValue(0), this.locale)){
//		        	Log.i("DAY", parser.getAttributeValue(0));
		        	//DB_TABLE_NAME = parser.getAttributeValue(0);
//		        	contentValues = new ContentValues();
//		        	contentValues.put(COLUMN_DATE, parser.getAttributeValue(0));
		        	readChannel(parser);

	        } else {
	            skip(parser);
	        }
	    }
//	    return scheduleList;
	}
	
	private void readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "channel");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the item tag
	        if (name.equals("item")) {
	        	//contentValues.clear();
	        	
	        	feed.add(readItem(parser));
	            
	        } else {
	            skip(parser);
	        }
	    }  
	}

	private HashMap<String, String> readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		HashMap<String, String> map = new HashMap<String, String>();
		String title = "";
		String link = "";
		String description = "";
		
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String item = parser.getName();

	        if (item.equals("title")) {
	        	title = readText(parser);
	        	Log.d("Parser", title);
	        	map.put("title", title);
	        } else if (item.equals("link")) {
	        	link = readText(parser);
	        	map.put("link", link);
	        } else if (item.equals("description")) {
	        	description = readText(parser);
	        	map.put("description", description);
	        } else {
	            skip(parser);
	        }
	    }
		
		return map;
	}

//	private void readShow(XmlPullParser parser) throws XmlPullParserException, IOException {
//		parser.require(XmlPullParser.START_TAG, ns, "show");
//		while (parser.next() != XmlPullParser.END_TAG) {
//			if (parser.getEventType() != XmlPullParser.START_TAG) {
//	            continue;
//	        }
//	        String s = parser.getName();
//	        String parsed = "";
//	        // Starts by looking into the show tag
//	        if (s.equals("sid")) {
//	        	parsed = readText(parser);
////	        	Log.i("sid", parsed);	        	
//	        	contentValues.put(COLUMN_SID, parsed);
//	        } else if (s.equals("network")) {
//	        	readText(parser);
////	        	Log.i("network", readText(parser));
//	        } else if (s.equals("title")) {
//	        	parsed = readText(parser);
////	        	Log.i("title", parsed);	        	
//	        	contentValues.put(COLUMN_TITLE, parsed);
//	        } else if (s.equals("ep")) {	        	
//	        	parsed = readText(parser);
////	        	Log.i("ep", parsed);
//	        	contentValues.put(COLUMN_EP, parsed);
//	        } else if (s.equals("link")) {
//	        	readText(parser);
////	        	Log.i("link", readText(parser));
//	        } else {
//	            skip(parser);
//	         
//	        }
//	    }  
//	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	
}