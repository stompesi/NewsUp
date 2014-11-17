package com.example.newsup.view;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.newsup.R;
import com.example.newsup.activity.transmission.structure.Image;
import com.example.newsup.application.NewsUpApp;
import com.example.newsup.data.ArticleReadInfo;
import com.example.newsup.database.Article;
import com.example.newsup.network.NewsUpImageLoader;
import com.example.newsup.network.NewsUpNetwork;
import com.example.newsup.splitter.PageSplitter;
import com.example.newsup.view.structure.ArticleDetailInfomation;
import com.example.newsup.view.structure.ArticleDetailPage;
import com.example.newsup.view.structure.ImageInfo;
import com.example.newsup.view.structure.LayoutInfo;
import com.example.newsup.view.structure.RelatedArticle;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class ArticleDetailManager extends ArticleFlipViewManager implements YouTubePlayer.OnInitializedListener {
	public static final String VIDEO_ID = "V7dmCpyCtA4";

	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;

	LayoutInfo layoutInfo;

	boolean pageOut;
	boolean isAnimationning;
	
	ArticleDetailInfomation articleDetailInfomation;
	
	private static ArticleDetailManager articleDetailManager;
	
	private OnClickListener likeClickListener;
	
	
	private int likeFlag; 
	ImageView like;
	ImageView unlike;
	
	private ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		this.context = context;
		likeFlag = 0;
		likeClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				switch(v.getId()) {
				case R.id.like:
					switch(likeFlag){
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
					switch(likeFlag){
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

		article = Article.getArticle(articleId);
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
		firstLayout = (LinearLayout)inflater.inflate(R.layout.view_article_detail_first_page,null);
		view = (LinearLayout)(firstLayout).findViewById(R.id.viewArticleDetail);
		
		
		TextView titleText = (TextView)firstLayout.findViewById(R.id.title);
		titleText.setText(article.getTitle());

		TextView auhtorText = (TextView)firstLayout.findViewById(R.id.author);
		auhtorText.setText(article.getAuthor());

		TextView proviewrText = (TextView)firstLayout.findViewById(R.id.provider);
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


		private final Semaphore resource;

		private int articleId;
		private boolean isFinish;
		public InsertArticleTask(int articleId) {
			resource = new Semaphore(1);
			this.isFinish = false;
			this.articleId = articleId;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			ArticleDetailPage next = splitter.makePageList();

			if(next == null) {
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
				return null;
			} 
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(next != null) {
				try {
					resource.acquire();
					articleDetailPage = next;
					next = splitter.makePageList();
					if(next == null) {
						publishProgress(articleDetailPage);
						isFinish = true;
						return null;
					} 
					else {
						publishProgress(articleDetailPage);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(pageOut) {
					removeAllFlipperItem();
					return null;
				}
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
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

			if(isFinish){
				Log.e("last", "last");
				addView(articleContentLayout);
				
				like = (ImageView) articleContentLayout.findViewById(R.id.like);
				unlike = (ImageView) articleContentLayout.findViewById(R.id.unlike);
				
				like.setBackgroundResource(R.drawable.ic_like_off);
				unlike.setBackgroundResource(R.drawable.ic_unlike_off);
				
				like.setOnClickListener(likeClickListener);
				unlike.setOnClickListener(likeClickListener);
				
				currentChildIndex++;
				articleReadInfo.addPage();
				
				NewsUpNetwork.getInstance().requestArticleDetail(articleId);
				resource.release();
			} else {
				addView(articleContentLayout);
				currentChildIndex++;
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
		pageOut = true;
		setReadTime();
		if (NewsUpNetwork.isNetworkState(context)) {
			articleReadInfo.setLike(likeFlag);
			NewsUpNetwork.getInstance().updateUserLog(articleReadInfo);
		}
	}

	@Override
	public boolean upDownSwipe(int increase) {
		int checkIndex = currentChildIndex + increase;

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
		articleDetailInfomation = new ArticleDetailInfomation();
		requestInfomation(articleId);
		pageOut = false;
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime);
		getArticleDetail(articleId);

	}
	
	public void requestInfomation(int articleId) {
		Article article = Article.getArticle(articleId);
		if(article != null) {
			NewsUpNetwork.getInstance().requestFacebook(article.getArticleURL());
			NewsUpNetwork.getInstance().requestTwitter(article.getArticleURL());
		}
	}
	
	public void setFacebookLikeCount(int shareCount) {
		articleDetailInfomation.setFacebookLikeCount(shareCount);
		totalCount++;
		
		if(totalCount == 2) {
			setFacebookTwitterCount();
		}
	}
	
	public void setTwitterCount(int shareCount) {
		articleDetailInfomation.setTwitterCount(shareCount);
		totalCount++;
		
		if(totalCount == 2) {
			setFacebookTwitterCount();
		}
	}
	
	private void setFacebookTwitterCount() {
		TextView facebookText = (TextView)firstLayout.findViewById(R.id.cnt_facebook);
		facebookText.setText("" + articleDetailInfomation.getFacebookLikeCount());

		TextView twitterText = (TextView)firstLayout.findViewById(R.id.cnt_twitter);
		twitterText.setText("" + articleDetailInfomation.getTwitterCount());
		totalCount = 0;
	}
	
	public void setRelatedArticle(String videoId) {
		articleDetailInfomation.setVideoId(videoId);
		addView(new ArticleLastPageMaker(context, inflater, true, articleDetailInfomation).getLastPage());
		currentChildIndex++;
		articleReadInfo.addPage();
		for(int i = 0 ; i < getChildChount() ; i++) {
			LinearLayout view = (LinearLayout) flipper.getChildAt(i);
			setPageNumber(view, getChildChount() - i, getChildChount());
		}
	}

	public void setRelatedArticle(JSONArray relatedArticles, boolean isExistVideo) {
		
		try {
			for (int i = 0; i < relatedArticles.length(); i++) {
				JSONObject item = relatedArticles.getJSONObject(i);
				RelatedArticle relatedArticle = new RelatedArticle();
				
				relatedArticle.setTitle(item.getString("title"));
				relatedArticle.setDescription(item.getString("description"));
				relatedArticle.setURL(item.getString("url"));
				
				
				if(item.has("image")) {
					JSONObject imageObject = item.getJSONObject("image");
					Image imageInfo = new Image(imageObject.getString("url"), imageObject.getString("color"));
					relatedArticle.setImageInfo(imageInfo);
				}
				
				articleDetailInfomation.addRelatedArticle(relatedArticle);
			}
			if(!isExistVideo && articleDetailInfomation.getRelatedArticleList().size() != 0) {
				addView(new ArticleLastPageMaker(context, inflater, isExistVideo, articleDetailInfomation).getLastPage());
				currentChildIndex++;
				articleReadInfo.addPage();
				
				for(int i = 0 ; i < getChildChount() ; i++) {
					LinearLayout view = (LinearLayout) flipper.getChildAt(i);
					setPageNumber(view, getChildChount() - i, getChildChount());
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void changeTextSize(int articleId) {
		removeAllFlipperItem();
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime);
		getArticleDetail(articleId);

	}

	private int getTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public void removeAllFlipperItem() {
		Handler handler = new Handler();
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				flipper.removeAllViews();
			}
		});
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player,
			boolean wasRestored) {

		if (!wasRestored) {
			player.cueVideo(VIDEO_ID);
		}

	}
	
	
}
