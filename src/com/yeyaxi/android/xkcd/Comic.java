package com.yeyaxi.android.xkcd;

import java.net.URL;


public class Comic {
	private int month = 0;
	private long num = 0;
	private int year = 0;
	private int day = 0;
//	private String news = null;
	private String safe_title = null;
	private String transcript = null;
	private String alt = null;
	private URL img = null;
	
	public Comic () {
		
	}
	
	public Comic (int month, int day, int year, 
			long num, String safe_title, String transcript, String alt, URL img) {
		this.month = month;
		this.num = num;
		this.year = year;
		this.day = day;
//		this.news = news;
		this.safe_title = safe_title;
		this.transcript = transcript;
		this.alt = alt;
		this.img = img;
	}

	public int getMonth() {
		return month;
	}

	public long getNum() {
		return num;
	}

	public int getYear() {
		return year;
	}

	public int getDay() {
		return day;
	}

//	public String getNews() {
//		return news;
//	}

	public String getSafe_title() {
		return safe_title;
	}

	public String getTranscript() {
		return transcript;
	}

	public String getAlt() {
		return alt;
	}

	public URL getImg() {
		return img;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setDay(int day) {
		this.day = day;
	}

//	public void setNews(String news) {
//		this.news = news;
//	}

	public void setSafe_title(String safe_title) {
		this.safe_title = safe_title;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setImg(URL img) {
		this.img = img;
	}
	
	
}
