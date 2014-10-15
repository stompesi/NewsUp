package com.example.pagetest;



public class ImageInfo {
	String ImageURL;
	int Image_width;
	int Image_height; 
	int Image_start;

	
	public ImageInfo(String ImageURL, int Image_width,int Image_height,int Image_start) {
	
		this.ImageURL = ImageURL;
		this.Image_height = Image_height;
		this.Image_width = Image_width;
		this.Image_start = Image_start;
	}
	
	public int getImage_start(){
		return Image_start;
	}
	public String getImageURL() {
		return ImageURL;
	}
	public void setImage_start(int Image_start)
	{
		this.Image_start = Image_start;
	}
	
	public int getImage_height() {
		return Image_height;
	}

	
	public int getImage_width() {
		return Image_width;
	}

}
