package com.example.newsup.view.structure;

import com.example.newsup.activity.transmission.structure.Image;

public class RelatedArticle {
	private String title;
	private String description;
	private String url;
	private Image imageInfo;
	
	public RelatedArticle() {}
	
	public void setTitle(String title){ this.title = title; };
	public void setDescription(String description){ this.description = description; };
	public void setURL(String url){ this.url = url; };
	public void setImageInfo(Image imageInfo){ this.imageInfo = imageInfo;};
	
	public String getTitle(){ return title; };
	public String getDescription(){ return description; };
	public String getURL(){ return url; };
	public Image getImageInfo(){ return imageInfo;};
}