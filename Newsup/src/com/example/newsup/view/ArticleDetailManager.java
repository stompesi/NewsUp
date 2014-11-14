package com.example.newsup.view;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.newsup.R;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class ArticleDetailManager extends ArticleFlipViewManager implements YouTubePlayer.OnInitializedListener {
	public static final String VIDEO_ID = "V7dmCpyCtA4";
	private int textSize;

	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;

	LayoutInfo layoutInfo;

	boolean pageOut;
	boolean isAnimationning;
	
	ArticleDetailInfomation articleDetailInfomation;
	
	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		this.context = context;
	}



	Handler handler = new Handler();
	ArrayList<Object> list;
	PageSplitter splitter;

	public void getArticleDetail(int articleId) {
		String str;
		Article article;

		TextPaint textPaint;

		article = Article.getArticle(articleId);
		list = new ArrayList<Object>();
		str = article.getBody();

		layoutInfo = LayoutInfo.getInstance();

		TextView textView = setTextView(context,
				layoutInfo.getAvailableTotalHeight(), NewsUpApp.getInstance().getTextSize());
		textPaint = textView.getPaint();

		splitter = new PageSplitter(textPaint);

		// TODO: 시작페이지를 만들어야한다 (상세기사 첫화면)
		splitter.split(str);
		ArticleDetailPage articleDetailPage = splitter.makePageList();

		// 첫페이지 title, author

		LinearLayout view, layout;
		layout = (LinearLayout)inflater.inflate(R.layout.view_article_detail_first_page,null);
		view = (LinearLayout)(layout).findViewById(R.id.viewArticleDetail);
		
		
		TextView titleText = (TextView)layout.findViewById(R.id.title);
		titleText.setText(article.getTitle());

		TextView auhtorText = (TextView)layout.findViewById(R.id.author);
		auhtorText.setText(article.getAuthor());

		TextView proviewrText = (TextView)layout.findViewById(R.id.provider);
		proviewrText.setText(ArticleListManager.providers[Integer.parseInt(article.getProvider())]);

		TextView facebookText = (TextView)layout.findViewById(R.id.cnt_facebook);
		facebookText.setText("1,100");

		TextView twitterText = (TextView)layout.findViewById(R.id.cnt_twitter);
		twitterText.setText("1,200");
		
		viewMaker(view, articleDetailPage);
		addView(layout);

//
//				LinearLayout view, layout;
//				layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail,
//						null);
//				view = (LinearLayout) (layout).findViewById(R.id.viewArticleDetail);
//		
//				textSize = R.dimen.text_title;
//				textView = setTextView(context, 300, textSize);
//				textView.setPadding(layoutInfo.getTextViewPadding(), 100, 0, 0);
//				textView.setMaxLines(2);
//				textView.setEllipsize(TextUtils.TruncateAt.END);
//		
//				textView.setText(article.getAuthor());
//				view.addView(textView);
//		
//				textSize = R.dimen.text_author;
//				textView = setTextView(context, 100, textSize);
//				textView.setPadding(layoutInfo.getTextViewPadding(), 10, 0, 0);
//		
//				textView.setText(article.getAuthor());
//				view.addView(textView);
//				viewMaker(view, articleDetailPage);
//				addView(layout);



		articleReadInfo.addPage();
		//여기	
		display(getChildChount() - 1);

		InsertArticleTask insertArticleTask = new InsertArticleTask(articleId);
		insertArticleTask.execute();
	}

	class InsertArticleTask extends
	AsyncTask<Void, ArticleDetailPage, Void> {

		private ArticleDetailPage articleDetailPage;


		private final Semaphore resource;

		private boolean isFinish;
		public InsertArticleTask(int articleId) {
			resource = new Semaphore(1);
			this.isFinish = false;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {
			ArticleDetailPage next = splitter.makePageList();

			if(next == null) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						addView(new ArticleLastPageMaker(context, inflater).getLastPage());
						currentChildIndex++;
						articleReadInfo.addPage();

						// TODO Auto-generated method stub
						for(int i = 0 ; i < getChildChount() ; i++) {
							LinearLayout view = (LinearLayout) flipper.getChildAt(i);
							setPageNumber(view, getChildChount() - i, getChildChount());
						}

					}
				});
				isFinish = true;
				return null;
			} 
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(pageOut) {
					removeAllFlipperItem();
					return null;
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
				currentChildIndex++;
				articleReadInfo.addPage();

				addView(new ArticleLastPageMaker(context, inflater).getLastPage());
				currentChildIndex++;
				articleReadInfo.addPage();

				for(int i = 0 ; i < getChildChount() ; i++) {
					LinearLayout view = (LinearLayout) flipper.getChildAt(i);
					setPageNumber(view, getChildChount() - i, getChildChount());
				}
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

				view.addView(imageView);

				imageView.getLayoutParams().height = imageInfo.getHeight() - 50;
				imageView.getLayoutParams().width = imageInfo.getWidth();
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

				NewsUpImageLoader.loadImage(imageView, imageInfo.getURL(), imageInfo.getColor());
			}
			// 텍스트 처리
			else {
				String text, save;
				TextView textView;
				Paint mPaint;
				int end;
				String[] textArr;

				text = (String) object;
				save = "";

				textView = new TextView(context);
				textView.setWidth(layoutInfo.getAvailableTotalWidth());
				textView.setLineSpacing((float) 1.1, (float) 1.1);

				int textSize = NewsUpApp.getInstance().getTextSize();
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context
						.getResources().getDimension(textSize));
				ApplyFont(context, textView);

				mPaint = textView.getPaint();

				end = 0;
				textArr = text.split("\n");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() == 0)
						textArr[j] = " ";
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = mPaint.breakText(textArr[j], true,
								layoutInfo.getAvailableTotalWidth(), null);
						if (end > 0) {
							// 자른 문자열을 문자열 배열에 담아 놓는다.
							save += textArr[j].substring(0, end) + "\n";
							// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
							textArr[j] = textArr[j].substring(end);
						}
					} while (end > 0);
				}
				Log.e("save", save);
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

	private TextView setTextView(Context context, int height, int textSize) {

		TextView textView = new TextView(context);
		textView.setWidth(layoutInfo.getAvailableTotalWidth());
		textView.setHeight(height);
		textView.setLineSpacing((float) 1.1, (float) 1.1);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
				.getDimension(textSize));
		ApplyFont(context, textView);

		return textView;
	}

	private void ApplyFont(Context context, TextView tv) {
		//		Typeface face = Typeface.createFromAsset(context.getAssets(), "NanumGothic.ttf.mp3");
		//		tv.setTypeface(face);
	}

	private void ApplyFont(Context context, TextPaint tv) {
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"NanumGothic.ttf.mp3");
		tv.setTypeface(face);
	}

	@Override
	public void outArticleDetail() {
		pageOut = true;
		setReadTime();
		if (NewsUpNetwork.isNetworkState(context)) {
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
		
		NewsUpNetwork.getInstance().requestFacebook(article.getArticleURL(), articleDetailInfomation);
		NewsUpNetwork.getInstance().requestTwitter(article.getArticleURL(), articleDetailInfomation);
//		NewsUpNetwork.getInstance().requestArticleDetail(articleId);
		
		
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
		flipper.removeAllViews();
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player,
			boolean wasRestored) {

		if (!wasRestored) {
			player.cueVideo(VIDEO_ID);
		}

	}
}
