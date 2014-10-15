package database;

import java.io.Serializable;

public class Article implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int idx, category, articleId;
	private String body, description, author, title, timestamp, provider, firstImageURL, firstImageColor;
	
	public Article() {}
	
	public Article(int category,
			int articleId,
			String body,
			String description,
			String author,
			String title,
			String timestamp,
			String provider,
			String firstImageURL,
			String firstImageColor) {
		this.category = category;
		this.articleId = articleId;
		this.body = body;
		this.description = description;
		this.author = author;
		this.title = title;
		this.timestamp = timestamp;
		this.provider = provider;
		this.firstImageURL = firstImageURL;
		this.firstImageColor = firstImageColor;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public int getArticleId() {
		return articleId;
	}
	
	public int getCategory() {
		return category;
	}
	
	public String getBody() {
		return body;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public String getFirstImageURL() {
		return firstImageURL;
	}
	
	public String getFirstImageColor() {
		return firstImageColor;
	}
	
	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public void setCategory(int category) {
		this.category = category;
	}
	
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public void setFirstImageURL(String firstImageURL) {
		this.firstImageURL = firstImageURL;
	}
	
	public void setFirstImageColor(String firstImageColor) {
		this.firstImageColor = firstImageColor;
	}
	
}
