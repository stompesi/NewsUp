package com.example.newsup.database;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.newsup.activity.LockScreenActivity;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

public class Article extends SugarRecord<Article> implements Serializable {
	
	private final static int TWO_DAY_SECOND = 172800;
	
	
	private static final long serialVersionUID = 1L;
	private int category, articleId;
	private String body, description, author, title, timestamp, provider, firstImageURL, firstImageColor;
	private double score;
	
	public Article() {}
	
	public Article(int category,
			int articleId,
			double score,
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
		this.score = score;
		this.body = body;
		this.description = description;
		this.author = author;
		this.title = title;
		this.timestamp = timestamp;
		this.provider = provider;
		this.firstImageURL = firstImageURL;
		this.firstImageColor = firstImageColor;
	}
	
	public int getArticleId() { return articleId; }
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
	
	public void setCategory(int category) { this.category = category; }
	public void setScore(double score) { this.score = score; }
	public void setArticleId(int articleId) { this.articleId = articleId; }
	public void setBody(String body) { this.body = body; }
	public void setDescription(String description) { this.description = description; }
	public void setAuthor(String author) { this.author = author; }
	public void setTitle(String title) { this.title = title; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
	public void setProvider(String provider) { this.provider = provider; }
	public void setFirstImageURL(String firstImageURL) { this.firstImageURL = firstImageURL; }
	public void setFirstImageColor(String firstImageColor) { this.firstImageColor = firstImageColor; }
	
	public static void saveArticle(JSONObject articleJSONObject) {
		ArticleInsertAsyncTask task = new ArticleInsertAsyncTask();
		task.execute(articleJSONObject);
	}
	
	static class ArticleInsertAsyncTask extends AsyncTask<JSONObject, Void, Void> {
		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject article = params[0];
			Article articleORM = new Article();
			try {
				articleORM.setArticleId(article.getInt("id"));
				articleORM.setScore(article.getDouble("score"));
				articleORM.setCategory(article.getInt("category"));
				articleORM.setBody(article.getString("body"));
				articleORM.setDescription(article.getString("description"));
				articleORM.setAuthor(article.getString("author"));
				articleORM.setTitle(article.getString("title"));
				articleORM.setTimestamp(article.getString("timestamp"));
				articleORM.setProvider(article.getString("provider"));
				articleORM.setFirstImageURL(article.getJSONObject("first_image").getString("url"));
				articleORM.setFirstImageColor(article.getJSONObject("first_image").getString("color"));
				articleORM.save();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public static List<Article> selectMainArticleList(int offset) {
		Log.d("NewsUp", "추천기사 요청");
		Article.executeQuery("VACUUM");
		List<Article> result = Article.findWithQuery(Article.class, "SELECT * FROM Article ORDER BY score DESC LIMIT 10 OFFSET ?", "" + offset);
		return result;
	}
	
	public static List<Article> selectCategoryArticleList(int category, int offset) {
		Log.d("NewsUp", "카테고리 기사 요청");
		Article.executeQuery("VACUUM");
		List<Article> result = Article.findWithQuery(Article.class, "SELECT * FROM Article WHERE category = ? ORDER BY timestamp DESC LIMIT 10 OFFSET ?", "" + category, "" + offset);
		return result;
	}
	
	public static Article getArticle(int articleId) {
		Log.d("NewsUp", "상세기사 요청");
		Article article = Select.from(Article.class).where(Condition.prop("article_id").eq(articleId)).first();
		return article;
	}
	
	public static void removeyArticle(){
		Log.d("NewsUp", "오래된 기사 제거 ");
		int twoDayAgo = (int)(System.currentTimeMillis() / 1000L) - TWO_DAY_SECOND;
		Article.deleteAll(Article.class, "timestamp <= ?", "" + twoDayAgo);
		LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
		lockScreenActivity.reFresh();
	}
}
