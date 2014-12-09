package org.tworoom.android.newsup.database;

import android.os.AsyncTask;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.tworoom.android.newsup.activity.LockScreenActivity;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;

public class ArticleService {

    private static ArticleService articleService;
    private Semaphore resource;
    private ArticleDao articleDao;

    private ArticleService() {
        articleDao = new ArticleDao();
        resource = new Semaphore(1);
    }

    public static ArticleService getInstance() {
        if (articleService == null) {
            articleService = new ArticleService();
        }
        return articleService;
    }

    public void saveArticle(JSONObject articleJSONObject) {
        ArticleSaveAsyncTask task = new ArticleSaveAsyncTask();
        task.execute(articleJSONObject);
    }
    class ArticleSaveAsyncTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            JSONObject articleJSONObject = params[0];
            try {
                resource.acquire();
                Article article = new Article();
                article.setId((long) articleJSONObject.getInt("id"));
                article.setCategory(articleJSONObject.getInt("category"));
                article.setBody(articleJSONObject.getString("body"));
                article.setDescription(articleJSONObject.getString("description"));
                article.setAuthor(articleJSONObject.getString("author"));
                article.setTitle(articleJSONObject.getString("title"));
                article.setTimestamp(articleJSONObject.getString("timestamp"));
                article.setProvider(articleJSONObject.getString("provider"));
                article.setArticleURL(articleJSONObject.getString("article_url"));
                article.setScore(articleJSONObject.getDouble("score"));
                if(articleJSONObject.isNull("first_image")) {
                    article.setIsExistFirstImage(false);
                } else {
                    article.setIsExistFirstImage(true);
                    article.setFirstImageURL(articleJSONObject.getJSONObject("first_image").getString("url"));
                    article.setFirstImageColor(articleJSONObject.getJSONObject("first_image").getString("color"));
                }

                articleDao.saveArticle(article);
                resource.release();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                return null;
            }
        }
    }

    public Article getArticle(int articleId) {
        return articleDao.getArticle(articleId);
    }

    public List<Article> getArticleList(int offset, int category) {
        if (category == 0) {
            return articleDao.getArticleList(offset);
        } else {
            return articleDao.getArticleList(offset, category);
        }
    }

    public void setZeroScore(Stack<Integer> viewArticleList) {
        SetZeroScoreAsyncTask setZeroScoreAsyncTask = new SetZeroScoreAsyncTask();
        setZeroScoreAsyncTask.execute(viewArticleList);
    }

    class SetZeroScoreAsyncTask extends AsyncTask<Stack<Integer>, Void, Void> {
        @Override
        protected Void doInBackground(Stack<Integer>... params) {
            Stack<Integer> viewArticleList = params[0];

            while(!viewArticleList.empty()) {
                Article article = getArticle(viewArticleList.pop());
                article.setScore(0);
                article.save();
            }
            return null;
        }
    }


    public void refreshArticleScore(JSONObject articleJSONObject) {
        try {
            Article article = getArticle(articleJSONObject.getInt("id"));
            if(article != null) {
                article.setScore(articleJSONObject.getDouble("score"));
                articleDao.saveArticle(article);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeyArticle(){
        int TWO_DAY_SECOND = 172800;
        int twoDayAgo = (int)(System.currentTimeMillis() / 1000L) - TWO_DAY_SECOND;
        articleDao.deleteArticle(String.valueOf(twoDayAgo));
        LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
        if(lockScreenActivity != null) {
            lockScreenActivity.reFresh();
        }
    }
}
