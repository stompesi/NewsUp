package org.tworoom.android.newsup.database;

import java.util.List;

/**
 * Created by stompesi on 14. 12. 7..
 */
public class ArticleDao {



    public void saveArticle(Article article) {
        article.save();
    }
    public Article getArticle(int articleId) {
        Article article = null;
        article = Article.findById(Article.class, (long) articleId);
        return article;
    }


    public List<Article> getArticleList(int offset) {
        Article.executeQuery("VACUUM");
        List<Article> result = Article.findWithQuery(Article.class, "SELECT * FROM Article WHERE SCORE != 0 ORDER BY score DESC, idx asc LIMIT 10 OFFSET ?", "" + offset);
        return result;
    }

    public List<Article> getArticleList(int offset, int category) {
        Article.executeQuery("VACUUM");
        List<Article> result = Article.findWithQuery(Article.class, "SELECT * FROM Article where category = ? and SCORE != 0 ORDER BY idx asc LIMIT 10 OFFSET ?", "" + category , "" + offset);
        return result;
    }

    public void deleteArticle(String time) {
        Article.deleteAll(Article.class, "timestamp <= ?", time);
    }


}
