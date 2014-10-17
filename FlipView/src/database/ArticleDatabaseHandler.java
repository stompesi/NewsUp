package database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class ArticleDatabaseHandler {
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private static ArticleDatabaseHandler dbHandler;

	private static final String TABLE_NAME = "article_list";
	private static final String COLUMN_IDX = "idx";
	private static final String COLUMN_CATEGORY = "category";
	private static final String COLUMN_BODY = "body";
	private static final String COLUMN_ARTICLE_ID = "article_id";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_AUTHOR = "author";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_TIMESTAMP = "timestamp";
	private static final String COLUMN_PROVIDER = "provider";
	private static final String COLUMN_FIRST_IMAGE_URL = "first_image_url";
	private static final String COLUMN_FIRST_IMAGE_COLOR = "first_image_color";

	private ArticleDatabaseHandler(Context context) {
		this.dbHelper = new DatabaseHelper(context);
		this.db = dbHelper.getWritableDatabase();
	}

	public static ArticleDatabaseHandler getInstance(Context context)
			throws SQLException {
		if (dbHandler == null) {
			dbHandler = new ArticleDatabaseHandler(context);
		}
		return dbHandler;
	}
	
	public static ArticleDatabaseHandler getInstance()
	{
		return dbHandler;
	}
	
	public Article selectArticle(int articleId) {
		// TODO : COLUMN_IDX 를 COLUMN_ARTICLE_ID로 바꺼야한다  
		String sql = "SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_IDX + "=" + articleId + ";";
		Cursor result = db.rawQuery(sql, null);
	
		if (result.moveToFirst()) {
			Article info = makeArticle(result);
		    result.close();
		    return info;
		}
		result.close();
		return null;
	}
	 
	public ArrayList<Article> selectArticleList(int category, int offset) {
		String sql = "SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + "=" + category + " ORDER BY " + COLUMN_IDX + " DESC LIMIT 10 OFFSET " + offset + ";";
		ArrayList<Article> infos = new ArrayList<Article>();
		Cursor results = db.rawQuery(sql, null);
		results.moveToFirst();
		while (!results.isAfterLast()) {
			Article info = makeArticle(results);
			infos.add(info);
			results.moveToNext();
		}
		results.close();
		return infos;
	}
	
	public int selectArticleCount(int category) {
		int count;
		String sql = "SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + "=" + category + ";";
		Cursor results = db.rawQuery(sql, null);
		count = results.getCount();
		results.close();
		return count;
	}
	
	private Article makeArticle(Cursor result) {
		Article article = new Article();
		article.setIdx(result.getInt(result.getColumnIndex(COLUMN_IDX)));
		article.setCategory(result.getInt(result.getColumnIndex(COLUMN_CATEGORY)));
		article.setArticleId(result.getInt(result.getColumnIndex(COLUMN_ARTICLE_ID)));
		article.setBody(result.getString(result.getColumnIndex(COLUMN_BODY)));
		article.setDescription(result.getString(result.getColumnIndex(COLUMN_DESCRIPTION)));
		article.setAuthor(result.getString(result.getColumnIndex(COLUMN_AUTHOR)));
		article.setTitle(result.getString(result.getColumnIndex(COLUMN_TITLE)));
		article.setTimestamp(result.getString(result.getColumnIndex(COLUMN_TIMESTAMP)));
		article.setProvider(result.getString(result.getColumnIndex(COLUMN_PROVIDER)));
		article.setFirstImageURL(result.getString(result.getColumnIndex(COLUMN_FIRST_IMAGE_URL)));
		article.setFirstImageColor(result.getString(result.getColumnIndex(COLUMN_FIRST_IMAGE_COLOR)));
		return article;
	}
	
	public void remveOldArticle(int category) {
	     ArticleRemoveAsyncTask task = new ArticleRemoveAsyncTask();
	     task.execute(category);
	}
	
	public void removeArticle(int articleId) {
		db.delete(TABLE_NAME, COLUMN_ARTICLE_ID + "=?" , new String[] { ""+ articleId });
	}
	
	public void insertArticle(JSONObject articleJSONObject) {
		ArticleInsertAsyncTask task = new ArticleInsertAsyncTask();
		task.execute(articleJSONObject);
	}
	
	class ArticleInsertAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject article = params[0];
			ContentValues values = new ContentValues();
			try {
				values.put(COLUMN_ARTICLE_ID, article.getInt("id"));
				values.put(COLUMN_CATEGORY, article.getInt("category"));
				values.put(COLUMN_BODY, article.getString("body"));
				values.put(COLUMN_DESCRIPTION, article.getString("description"));
				values.put(COLUMN_AUTHOR, article.getString("author"));
				values.put(COLUMN_TITLE, article.getString("title"));
				values.put(COLUMN_TIMESTAMP, article.getString("timestamp"));
				values.put(COLUMN_PROVIDER, article.getString("provider"));
				values.put(COLUMN_FIRST_IMAGE_URL, article.getJSONObject("first_image").getString("url"));
				values.put(COLUMN_FIRST_IMAGE_COLOR, article.getJSONObject("first_image").getString("color"));
				db.insert(TABLE_NAME, null, values);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	class ArticleRemoveAsyncTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			Integer category = params[0];
			String sql = "DELETE FROM " + TABLE_NAME +" WHERE " + COLUMN_IDX + " = (select MIN(" + COLUMN_IDX + ") from " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " = " + category + ");";
		    db.execSQL(sql);
			return null;
		}
	}
	
	class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "newsup.db";
		private static final int DATABASE_VER = 2;
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VER);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createSql = "CREATE TABLE " + TABLE_NAME + " ("
	              + COLUMN_IDX + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
	              + COLUMN_CATEGORY + " INTEGER, "
	      		+ COLUMN_ARTICLE_ID + " INTEGER, "
	      		+ COLUMN_BODY + " TEXT, "
	      		+ COLUMN_DESCRIPTION + " TEXT, "
	      		+ COLUMN_AUTHOR + " VARCHAR(255), "
	      		+ COLUMN_TITLE + " TEXT, "
	      		+ COLUMN_TIMESTAMP + " CHAR(10), "
	      		+ COLUMN_PROVIDER + " VARCHAR(255), "
	      		+ COLUMN_FIRST_IMAGE_URL + " TEXT, "
	      		+ COLUMN_FIRST_IMAGE_COLOR + " CHAR(7))";
			db.execSQL(createSql);
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	}
}
