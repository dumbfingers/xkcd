package com.yeyaxi.android.xkcd;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;


/**
 * XMLParser customed to return IMG url and descriptions
 * @author Yaxi Ye
 *
 * Thanks for Jonathan Hedley (jonathan@hedley.net)'s JSoup
 *
 */
public class XMLParser {

	private static final String ns = null;
	private ArrayList<HashMap<String, String>> feed = new ArrayList<HashMap<String, String>>();
	
	public XMLParser() {
		
	}
		
	public ArrayList<HashMap<String, String>> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readRSS(parser);
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

		        	readChannel(parser);

	        } else {
	            skip(parser);
	        }
	    }
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
	        	Log.d("Title", title);
	        	map.put("title", title);
	        } else if (item.equals("link")) {
	        	link = readText(parser);
	        	map.put("link", link);
	        } else if (item.equals("description")) {
	        	description = readText(parser);
	        	Document doc = Jsoup.parse(description);
	        	Element element = doc.select("img").first();
	        	
	        	map.put("img_src", element.attr("src"));
	        	map.put("img_alt", element.attr("title"));
	        	
	        	Log.d("img src", element.attr("src"));
	        	Log.d("img title", element.attr("alt"));
	        } else {
	            skip(parser);
	        }
	    }
		
		return map;
	}

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