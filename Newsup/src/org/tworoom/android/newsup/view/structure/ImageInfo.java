package org.tworoom.android.newsup.view.structure;

import android.util.Log;

public class ImageInfo {
	private String imageURL;
	private int imageWidth, imageHeight; 
	private String color;
	
	public ImageInfo(String imageURL, int imageWidth,int imageHeight, String color) {
		Log.d("NewsUp", "이미지 정보 객체 생성 width : " + imageWidth + " height : " + imageHeight);
		
		this.imageURL = imageURL;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getURL() {
		return imageURL;
	}
	
	public int getHeight() {
		return imageHeight;
	}

	public int getWidth() {
		return imageWidth;
	}

}
