package com.example.newsup.database;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.example.newsup.activity.LockScreenActivity;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

public class Article extends SugarRecord<Article> implements Serializable {
	
	private static int auto = 0;
	private final static int TWO_DAY_SECOND = 172800;
	
	
	private static final long serialVersionUID = 1L;
	private int category, articleId;
	private String body, description, author, title, timestamp, provider, firstImageURL, firstImageColor, articleURL;
	private double score;
	private boolean isExistFirstImage;
	
	private int idx;
	
	public Article() {
		
	}
	
	public int getIdx() { return idx; }
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
	public String getArticleURL() { return articleURL; }
	public boolean getIsExistFirstImage() { return isExistFirstImage; }
	
	public void setIdx(int idx) { this.idx = idx; }
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
	public void setArticleURL(String articleURL) { this.articleURL = articleURL; }
	public void setIsExistFirstImage(boolean isExistFirstImage) { this.isExistFirstImage = isExistFirstImage; }
	
	public static void saveArticle(JSONObject articleJSONObject) {
		ArticleInsertAsyncTask task = new ArticleInsertAsyncTask();
		task.execute(articleJSONObject);
	}
	static class ArticleInsertAsyncTask extends AsyncTask<JSONObject, Void, Void> {
		
		private static Semaphore resource = new Semaphore(1);;
		@Override
		protected Void doInBackground(JSONObject... params) {
			
			JSONObject article = params[0];
			
			try {
				
				resource.acquire();
				Article articleORM;
				articleORM = getArticle(article.getInt("id"));
				
				if(articleORM == null) {
					articleORM = new Article();
					articleORM.setIdx(++Article.auto);
				}
				
				articleORM.setArticleId(article.getInt("id"));
				articleORM.setCategory(article.getInt("category"));
				articleORM.setBody(article.getString("body"));
				articleORM.setDescription(article.getString("description"));
				articleORM.setAuthor(article.getString("author"));
				articleORM.setTitle(article.getString("title"));
				articleORM.setTimestamp(article.getString("timestamp"));
				articleORM.setProvider(article.getString("provider"));
				articleORM.setArticleURL(article.getString("article_url"));
				articleORM.setScore(article.getDouble("score"));
				if(article.isNull("first_image")) {
					articleORM.setIsExistFirstImage(false);
				} else {
					articleORM.setIsExistFirstImage(true);
					articleORM.setFirstImageURL(article.getJSONObject("first_image").getString("url"));
					articleORM.setFirstImageColor(article.getJSONObject("first_image").getString("color"));
				}
				articleORM.save();
				resource.release();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public static void refreshArticleScore(JSONObject article) {
		try {
			Article articleORM = getArticle(article.getInt("id"));
			if(articleORM != null) {
				articleORM.setScore(article.getDouble("score"));
				articleORM.save();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Article> selectMainArticleList(int offset) {
		Article.executeQuery("VACUUM");
		 List<Article>  result = Article.findWithQuery(Article.class, "SELECT * FROM Article WHERE SCORE != 0 ORDER BY score DESC, idx asc LIMIT 10 OFFSET ?", "" + offset);
		return result;
	}
	
	public static List<Article> selectOtherArticleList(int category, int offset) {
		Article.executeQuery("VACUUM");
		// TODO : 점수별 소팅 
		List<Article> result = Article.findWithQuery(Article.class, "SELECT * FROM Article where category = ? and SCORE != 0 ORDER BY idx asc LIMIT 10 OFFSET ?", "" + category , "" + offset);
		return result;
	}
	
	public static Article getArticle(int articleId) {
		Article article = Select.from(Article.class).where(Condition.prop("article_id").eq(articleId)).first();
		return article;
	}
	
	public static void removeyArticle(){
		int twoDayAgo = (int)(System.currentTimeMillis() / 1000L) - TWO_DAY_SECOND;
		Article.deleteAll(Article.class, "timestamp <= ?", "" + twoDayAgo);
		
		LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
		
		if(lockScreenActivity != null) {
			lockScreenActivity.reFresh();
		}
		
	}
	
	
	public static void setZeroScore(Stack<Integer> viewArticleList) {
		SetZeroScoreAsyncTask setZeroScoreAsyncTask = new SetZeroScoreAsyncTask();
		setZeroScoreAsyncTask.execute(viewArticleList);
	}
	
	
	static class SetZeroScoreAsyncTask extends AsyncTask<Stack<Integer>, Void, Void> {
		@Override
		protected Void doInBackground(Stack<Integer>... params) {
			Stack<Integer> viewArticleList = params[0];
			
			while(!viewArticleList.empty()) {
				Article articleORM = getArticle(viewArticleList.pop());
				articleORM.setScore(0);
				articleORM.save();
			}
			return null;
		}
	}
}
