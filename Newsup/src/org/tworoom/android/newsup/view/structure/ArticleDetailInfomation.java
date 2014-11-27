package org.tworoom.android.newsup.view.structure;

import java.util.ArrayList;

public class ArticleDetailInfomation {
	private int facebookLikeCount;
	private int twitterCount;
	private ArrayList<RelatedArticle> relatedArticleList;
	private ArrayList<RelatedVideo> relatedVideoList;
	private String videoId;
	
	public ArticleDetailInfomation() {
		relatedArticleList = new ArrayList<RelatedArticle>();
		relatedVideoList = new ArrayList<RelatedVideo>();
	}
	
	public void setFacebookLikeCount(int facebookLikeCount) { this.facebookLikeCount = facebookLikeCount; }
	public void setTwitterCount(int twitterCount) { this.twitterCount = twitterCount; }
	public void addRelatedArticle(RelatedArticle relatedArticle) { relatedArticleList.add(relatedArticle); }
	public void addRelatedVideo(RelatedVideo relatedVideo) { relatedVideoList.add(relatedVideo);}
	
	public int getFacebookLikeCount() { return facebookLikeCount; }
	public int getTwitterCount() { return twitterCount; }
	public ArrayList<RelatedArticle> getRelatedArticleList() { return relatedArticleList;}
	public ArrayList<RelatedVideo> getRelatedVideoList() { return relatedVideoList;}
	public String getVideoId() { return videoId;}
}
