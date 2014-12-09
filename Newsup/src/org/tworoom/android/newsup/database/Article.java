package org.tworoom.android.newsup.database;

import com.orm.SugarRecord;

import java.io.Serializable;


public class Article extends SugarRecord<Article> implements Serializable {
	
	private static int autoincrement = 0;

	private static final long serialVersionUID = 1L;
	private int category;
	private String body, description, author, title, timestamp, provider, firstImageURL, firstImageColor, articleURL;
	private double score;
	private boolean isExistFirstImage;
	
	private int idx;

	public Article() {
        idx = autoincrement++;
    }
	
	public int getIdx() { return idx; }
	public double getScore() { return score; }
	public int getCategory() { return category; }
	public String getBody() { return body; }
	public String getDescription() { return description; }
	public String getAuthor() { return author; }
	public String getTitle() { return title; }
	public String getTimestamp() { return timestamp; }
	public String getProvider() { return provider; }
	public String getFirstImageURL() { return firstImageURL; }
	public String getFirstImageColor() { return firstImageColor; }
	public String getArticleURL() { return articleURL; }
	public boolean getIsExistFirstImage() { return isExistFirstImage; }
	
	public void setIdx(int idx) { this.idx = idx; }
	public void setCategory(int category) { this.category = category; }
	public void setScore(double score) { this.score = score; }
	public void setBody(String body) { this.body = body; }
	public void setDescription(String description) { this.description = description; }
	public void setAuthor(String author) { this.author = author; }
	public void setTitle(String title) { this.title = title; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
	public void setProvider(String provider) { this.provider = provider; }
	public void setFirstImageURL(String firstImageURL) { this.firstImageURL = firstImageURL; }
	public void setFirstImageColor(String firstImageColor) { this.firstImageColor = firstImageColor; }
	public void setArticleURL(String articleURL) { this.articleURL = articleURL; }
	public void setIsExistFirstImage(boolean isExistFirstImage) { this.isExistFirstImage = isExistFirstImage; }
}
