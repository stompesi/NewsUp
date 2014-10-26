package database;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

public class ArticleORM extends SugarRecord<ArticleORM> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int category, articleId;
	private String body, description, author, title, timestamp, provider, firstImageURL, firstImageColor;
	private double score;
	
	public ArticleORM() {}
	
	public ArticleORM(int category,
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
	
//	public int getIdx() { return idx; }
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
	
//	public void setIdx(int idx) { this.idx = idx; }
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
	
	
	static class ArticleInsertAsyncTask extends AsyncTask<JSONObject, Void, Void> {
		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject article = params[0];
			ArticleORM articleORM = new ArticleORM();
			try {
				articleORM.setArticleId(article.getInt("id"));
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
	
	public static void saveArticle(JSONObject articleJSONObject) {
		ArticleInsertAsyncTask task = new ArticleInsertAsyncTask();
		task.execute(articleJSONObject);
	}
	
	public static List<ArticleORM> selectArticleList(int category, int offset) {
//		List<ArticleORM> result = ArticleORM.findWithQuery(ArticleORM.class, 
//				"SELECT * FROM ArticleORM where category = ? ORDERY score DESC LIMIT 10 OFFSET ?", "" + category , "" + offset);
		List<ArticleORM> result = Select.from(ArticleORM.class).where(Condition.prop("category").eq(category)).list();
		return result;
	}
	
	public static int getCategoryArticleCount(int category) {
		return (int) Select.from(ArticleORM.class).where(Condition.prop("category").eq(category)).count();
	}
	
	public static void removeCategoryArticle(int category){
		
	}
	
	
}
