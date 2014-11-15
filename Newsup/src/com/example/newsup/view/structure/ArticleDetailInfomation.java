package com.example.newsup.view.structure;

import java.util.ArrayList;

public class ArticleDetailInfomation {
	private int facebookLikeCount;
	private int twitterCount;
	private ArrayList<RelatedArticle> relatedArticleList;
	private String videoId;
	
	public ArticleDetailInfomation() {
		relatedArticleList = new ArrayList<RelatedArticle>();
	}
	
	public void setFacebookLikeCount(int facebookLikeCount) { this.facebookLikeCount = facebookLikeCount; }
	public void setTwitterCount(int twitterCount) { this.twitterCount = twitterCount; }
	public void addRelatedArticle(RelatedArticle relatedArticle) { relatedArticleList.add(relatedArticle); }
	public void setVideoId(String videoId) { this.videoId = videoId; }
	
	public int getFacebookLikeCount() { return facebookLikeCount; }
	public int getTwitterCount() { return twitterCount; }
	public ArrayList<RelatedArticle> getRelatedArticleList() { return relatedArticleList;}
	public String getVideoId() { return videoId;}
}
