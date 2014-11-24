package com.tworoom.android.newsup.activity.transmission.structure;

public class Image {
	private String url;
	private String color;
	public Image(String url, String color) {
		this.url = url;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getURL() {
		return url;
	}
}
