package com.yeyaxi.android.xkcd;

import java.net.URL;


public class Comic {
	int month;
	long num;
	int year;
	int day;
	String news;
	String safe_title;
	String transcript;
	String alt;
	URL img;
	
	public Comic (int month, int day, int year, 
			long num, String news, String safe_title, String transcript, String alt, URL img) {
		this.month = month;
		this.num = num;
		this.year = year;
		this.day = day;
		this.news = news;
		this.safe_title = safe_title;
		this.transcript = transcript;
		this.alt = alt;
		this.img = img;
	}
}
