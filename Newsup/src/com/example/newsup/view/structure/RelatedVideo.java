package com.example.newsup.view.structure;


public class RelatedVideo {
	private String id;
	private String title;
	private String imageUrl;
	
	public RelatedVideo() {}
	
	public void setId(String id){ this.id = id; };
	public void setTitle(String title){ this.title = title; };
	public void setImageURL(String imageUrl){ this.imageUrl = imageUrl;};
	
	public String getTitle(){ return title; };
	public String getId(){ return id; };
	public String getImageURL(){ return imageUrl;};
}
