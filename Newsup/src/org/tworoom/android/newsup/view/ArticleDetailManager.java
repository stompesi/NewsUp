package org.tworoom.android.newsup.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.tworoom.android.newsup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tworoom.android.newsup.activity.ArticleActivity;
import org.tworoom.android.newsup.activity.transmission.structure.Image;
import org.tworoom.android.newsup.application.NewsUpApp;
import org.tworoom.android.newsup.data.ArticleReadInfo;
import org.tworoom.android.newsup.database.Article;
import org.tworoom.android.newsup.database.ArticleService;
import org.tworoom.android.newsup.network.NewsUpImageLoader;
import org.tworoom.android.newsup.network.NewsUpNetwork;
import org.tworoom.android.newsup.splitter.PageSplitter;
import org.tworoom.android.newsup.view.structure.ArticleDetailInfomation;
import org.tworoom.android.newsup.view.structure.ArticleDetailPage;
import org.tworoom.android.newsup.view.structure.ImageInfo;
import org.tworoom.android.newsup.view.structure.LayoutInfo;
import org.tworoom.android.newsup.view.structure.RelatedArticle;
import org.tworoom.android.newsup.view.structure.RelatedVideo;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ArticleDetailManager extends ArticleFlipViewManager {
    public static final String VIDEO_ID = "V7dmCpyCtA4";

    private ArticleReadInfo articleReadInfo;
    private int pageReadStartTime;

    LayoutInfo layoutInfo;

    boolean isAnimationning;

    ArticleDetailInfomation articleDetailInfomation;

    private static ArticleDetailManager articleDetailManager;

    private OnClickListener likeClickListener;

    private boolean isOutArticleDetailPage;
    private int likeFlag;
    ImageView like;
    ImageView unlike;

    Semaphore resource;

    private ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
        super(context, flipper, offset);
        this.context = context;
        resource = new Semaphore(1);
        likeFlag = 0;
        likeClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (v.getId()) {
                    case R.id.like:
                        switch (likeFlag) {
                            case 0:
                                like.setBackgroundResource(R.drawable.ic_like_on);
                                likeFlag = 1;
                                break;
                            case 1:
                                like.setBackgroundResource(R.drawable.ic_like_off);
                                likeFlag = 0;
                                break;
                            case -1:
                                like.setBackgroundResource(R.drawable.ic_like_on);
                                unlike.setBackgroundResource(R.drawable.ic_unlike_off);
                                likeFlag = 1;
                                break;
                        }
                        break;
                    case R.id.unlike:
                        switch (likeFlag) {
                            case 0:
                                unlike.setBackgroundResource(R.drawable.ic_unlike_on);
                                likeFlag = -1;
                                break;
                            case 1:
                                unlike.setBackgroundResource(R.drawable.ic_unlike_on);
                                like.setBackgroundResource(R.drawable.ic_like_off);
                                likeFlag = -1;
                                break;
                            case -1:
                                unlike.setBackgroundResource(R.drawable.ic_unlike_off);
                                likeFlag = 0;
                                break;
                        }
                        break;
                }
            }
        };
    }

    public static void setArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
        articleDetailManager = new ArticleDetailManager(context, flipper, offset);
    }

    public static ArticleDetailManager getInstance() {
        return articleDetailManager;
    }


    private LinearLayout firstLayout;
    Handler handler = new Handler();
    ArrayList<Object> list;
    PageSplitter splitter;

    private int totalCount;


    public void getArticleDetail(int articleId) {
        String str;
        Article article;

        TextPaint textPaint;

        article = ArticleService.getInstance().getArticle(articleId);
        list = new ArrayList<Object>();
        str = article.getBody();

        layoutInfo = LayoutInfo.getInstance();

        TextView textView;

        textView = new TextView(context);
        textView.setWidth(layoutInfo.getAvailableTotalWidth());
        textView.setLineSpacing((float) 1.5, (float) 1.5);

        int textSize = NewsUpApp.getInstance().getTextSize();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context
                .getResources().getDimension(textSize));
        ApplyFont(context, textView);

        textPaint = textView.getPaint();

        splitter = new PageSplitter(textPaint);

        splitter.split(str);
        ArticleDetailPage articleDetailPage = splitter.makePageList();

        // 첫페이지 title, author

        LinearLayout view;
        firstLayout = (LinearLayout) inflater.inflate(R.layout.view_article_detail_first_page, null);
        view = (LinearLayout) (firstLayout).findViewById(R.id.viewArticleDetail);


        TextView titleText = (TextView) firstLayout.findViewById(R.id.title);
        titleText.setText(article.getTitle());

        TextView auhtorText = (TextView) firstLayout.findViewById(R.id.author);
        auhtorText.setText(article.getAuthor());

        TextView proviewrText = (TextView) firstLayout.findViewById(R.id.provider);
        proviewrText.setText(ArticleListManager.providers[Integer.parseInt(article.getProvider())]);

        viewMaker(view, articleDetailPage);
        addView(firstLayout);
        articleReadInfo.addPage();
        display(getChildChount() - 1);

        InsertArticleTask insertArticleTask = new InsertArticleTask(articleId);
        insertArticleTask.execute();
    }

    class InsertArticleTask extends
            AsyncTask<Void, ArticleDetailPage, Void> {

        private ArticleDetailPage articleDetailPage;

        private int articleId;
        private boolean isFinish;

        public InsertArticleTask(int articleId) {
            this.isFinish = false;
            this.articleId = articleId;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Void... params) {
            ArticleDetailPage next = splitter.makePageList();
            try {
                resource.acquire();
                if (next == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            like = (ImageView) flipper.getCurrentView().findViewById(R.id.like);
                            unlike = (ImageView) flipper.getCurrentView().findViewById(R.id.unlike);

                            like.setBackgroundResource(R.drawable.ic_like_off);
                            unlike.setBackgroundResource(R.drawable.ic_unlike_off);

                            like.setOnClickListener(likeClickListener);
                            unlike.setOnClickListener(likeClickListener);

                        }
                    });
                    NewsUpNetwork.getInstance().requestArticleDetail(articleId);
                    resource.release();
                    return null;
                }
                resource.release();
                Thread.sleep(300);
            } catch (InterruptedException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            while (next != null) {

                try {
                    resource.acquire();
                    if (isOutArticleDetailPage) {
                        resource.release();
                        return null;
                    }

                    articleDetailPage = next;
                    next = splitter.makePageList();
                    if (next == null) {
                        publishProgress(articleDetailPage);
                        isFinish = true;
                        return null;
                    } else {
                        publishProgress(articleDetailPage);
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final ArticleDetailPage... articleDetailPage) {
            LinearLayout layoutView;
            final LinearLayout articleContentLayout;
            articleContentLayout = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
            layoutView = (LinearLayout) (articleContentLayout).findViewById(R.id.viewArticleDetail);
            viewMaker(layoutView, articleDetailPage[0]);

            if (isFinish) {
                Log.e("last", "last");
                addView(articleContentLayout);

                like = (ImageView) articleContentLayout.findViewById(R.id.like);
                unlike = (ImageView) articleContentLayout.findViewById(R.id.unlike);

                like.setBackgroundResource(R.drawable.ic_like_off);
                unlike.setBackgroundResource(R.drawable.ic_unlike_off);

                like.setOnClickListener(likeClickListener);
                unlike.setOnClickListener(likeClickListener);
                articleReadInfo.addPage();

                NewsUpNetwork.getInstance().requestArticleDetail(articleId);
                resource.release();
            } else {
                addView(articleContentLayout);
                articleReadInfo.addPage();
                resource.release();
            }
        }

        @Override
        protected void onPostExecute(Void params) {
        }
    }

    private void viewMaker(LinearLayout view,
                           ArticleDetailPage articleDetailPage) {
        ArrayList<Object> articleContent;

        Object object;

        articleContent = (ArrayList<Object>) articleDetailPage.getContent();

        for (int i = 0; i < articleContent.size(); i++) {
            object = articleContent.get(i);

            // 이미지 처리
            if (object instanceof ImageInfo) {
                ImageInfo imageInfo;
                ImageView imageView;

                imageInfo = (ImageInfo) object;
                imageView = new ImageView(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = imageInfo.getWidth();
                params.height = imageInfo.getHeight();

                imageView.setLayoutParams(params);


                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                view.addView(imageView);


                NewsUpImageLoader.loadImage(imageView, imageInfo.getURL(), imageInfo.getColor());
            }
            // 텍스트 처리
            else {
                String text, save;
                TextView textView;
                Paint mPaint;
                int end;

                text = (String) object;
                save = "";

                textView = new TextView(context);
                textView.setWidth(layoutInfo.getAvailableTotalWidth());
                textView.setLineSpacing((float) 1.5, (float) 1.5);

                int textSize = NewsUpApp.getInstance().getTextSize();
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context
                        .getResources().getDimension(textSize));
                ApplyFont(context, textView);

                mPaint = textView.getPaint();

                String[] textArr;
                textArr = text.split("\n");
                for (int j = 0; j < textArr.length; j++) {
                    if (textArr[j].length() == 0)
                        textArr[j] = " ";
                    do {
                        // 글자가 width 보다 넘어가는지 체크
                        end = mPaint.breakText(textArr[j], true, layoutInfo.getAvailableTotalWidth(), null);
                        if (end > 0) {
                            // 자른 문자열을 문자열 배열에 담아 놓는다.
                            save += textArr[j].substring(0, end) + "\n";
                            // 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
                            textArr[j] = textArr[j].substring(end);
                        }
                    } while (end > 0);
                }
                textView.setText(save);
                view.addView(textView);
            }
        }
    }

    private void setPageNumber(LinearLayout layout, int pageNumber,
                               int maxPageNumber) {
        TextView textView = (TextView) (layout)
                .findViewById(R.id.viewArticleBottom);
        textView.setText(pageNumber + "/" + maxPageNumber);

    }


    private void ApplyFont(Context context, TextView tv) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "NotoSansKR-Regular.ttf.mp3");
        tv.setTypeface(face);
    }

    @Override
    public void outArticleDetail() {
        setReadTime();
        isOutArticleDetailPage = true;
        if (NewsUpNetwork.isNetworkState(context)) {
            articleReadInfo.setLike(likeFlag);
            NewsUpNetwork.getInstance().updateUserLog(articleReadInfo);
        }
    }

    @Override
    public boolean upDownSwipe(int increase) {

        int checkIndex = currentChildIndex + increase;
        Log.d("NewsUp", "ArticleDetail upDownSwipe : " + checkIndex + " getChildChount() : " + getChildChount());
        if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
            return false;
        }

        setReadTime();
        display(checkIndex);

        return true;
    }

    private void setReadTime() {
        int index = getChildChount() - (currentChildIndex + 1);
        articleReadInfo.setReadTime(index, getTimestamp() - pageReadStartTime);
        pageReadStartTime = getTimestamp();
    }

    @Override
    public void inArticleDetail(int articleId) {
        removeAllFlipperItem();
        isOutArticleDetailPage = false;
        articleDetailInfomation = new ArticleDetailInfomation();
        requestInfomation(articleId);
        pageReadStartTime = getTimestamp();
        articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime);
        getArticleDetail(articleId);

    }

    public void requestInfomation(int articleId) {
        Article article = ArticleService.getInstance().getArticle(articleId);
        if (article != null) {
            NewsUpNetwork.getInstance().requestFacebook(article.getArticleURL());
            NewsUpNetwork.getInstance().requestTwitter(article.getArticleURL());
        }
    }

    public void setFacebookLikeCount(int shareCount) {
        articleDetailInfomation.setFacebookLikeCount(shareCount);
        totalCount++;

        if (totalCount == 2) {
            setFacebookTwitterCount();
        }
    }

    public void setTwitterCount(int shareCount) {
        articleDetailInfomation.setTwitterCount(shareCount);
        totalCount++;

        if (totalCount == 2) {
            setFacebookTwitterCount();
        }
    }

    private void setFacebookTwitterCount() {
        TextView facebookText = (TextView) firstLayout.findViewById(R.id.cnt_facebook);
        facebookText.setText("" + articleDetailInfomation.getFacebookLikeCount());

        TextView twitterText = (TextView) firstLayout.findViewById(R.id.cnt_twitter);
        twitterText.setText("" + articleDetailInfomation.getTwitterCount());
        totalCount = 0;
    }

    public void setRelatedInfomation(JSONArray relatedArticles, JSONArray relatedVedios) {
        if (relatedArticles == null && relatedVedios == null) {
            for (int i = 0; i < getChildChount(); i++) {
                LinearLayout view = (LinearLayout) flipper.getChildAt(i);
                setPageNumber(view, getChildChount() - i, getChildChount());
            }
            return;
        }

        try {
            if (relatedArticles != null) {
                for (int i = 0; i < relatedArticles.length(); i++) {
                    JSONObject item = relatedArticles.getJSONObject(i);
                    RelatedArticle relatedArticle = new RelatedArticle();

                    relatedArticle.setTitle(item.getString("title"));
                    relatedArticle.setDescription(item.getString("description"));
                    relatedArticle.setURL(item.getString("url"));


                    if (item.has("image") && !item.isNull("image")) {
                        JSONObject imageObject = item.getJSONObject("image");
                        Image imageInfo = new Image(imageObject.getString("url"), imageObject.getString("color"));
                        relatedArticle.setImageInfo(imageInfo);
                    }

                    articleDetailInfomation.addRelatedArticle(relatedArticle);
                }
            }


            if (relatedVedios != null) {
                for (int i = 0; i < relatedVedios.length(); i++) {
                    JSONObject item = relatedVedios.getJSONObject(i);
                    RelatedVideo relatedVideo = new RelatedVideo();

                    relatedVideo.setTitle(item.getString("title"));
                    relatedVideo.setId(item.getString("id"));
                    relatedVideo.setImageURL(item.getString("image_url"));
                    articleDetailInfomation.addRelatedVideo(relatedVideo);
                }
            }


            if (articleDetailInfomation.getRelatedVideoList().size() != 0 || articleDetailInfomation.getRelatedArticleList().size() != 0) {
                LinearLayout layout = new ArticleLastPageMaker(context, inflater, articleDetailInfomation).getLastPage();
                ListView itemList = (ListView) (layout).findViewById(R.id.itemList);
                FrameLayout youtube_1 = (FrameLayout) (layout).findViewById(R.id.youtube_1);
                FrameLayout youtube_2 = (FrameLayout) (layout).findViewById(R.id.youtube_2);

                youtube_1.setOnTouchListener(ArticleActivity.getInstance());
                youtube_2.setOnTouchListener(ArticleActivity.getInstance());
                itemList.setOnTouchListener(ArticleActivity.getInstance());

                addView(layout);
                articleReadInfo.addPage();
            }
            for (int i = 0; i < getChildChount(); i++) {
                LinearLayout view = (LinearLayout) flipper.getChildAt(i);
                setPageNumber(view, getChildChount() - i, getChildChount());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showYoutube(int index) {
        RelatedVideo relatedVideo = articleDetailInfomation.getRelatedVideoList().get(index);
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + relatedVideo.getId())));
    }

    public void changeTextSize(int articleId) {
        removeAllFlipperItem();
        isOutArticleDetailPage = false;
        pageReadStartTime = getTimestamp();
        articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime);
        getArticleDetail(articleId);

    }

    private int getTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    @Override
    public void removeAllFlipperItem() {
        isOutArticleDetailPage = true;
        currentChildIndex = -1;
        flipper.removeAllViews();
    }


}
