package com.example.newsup.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.newsup.R;
import com.example.newsup.activity.transmission.structure.Image;
import com.example.newsup.activity.transmission.structure.TransmissionArticle;
import com.example.newsup.data.TimeCalculator;
import com.example.newsup.database.Article;
import com.example.newsup.network.NewsUpImageLoader;
import com.example.newsup.network.NewsUpNetwork;

public class ArticleListManager extends ArticleFlipViewManager {

	private static final int MINIMUM_ARTICLE_LIST_ATTACH_INDEX = 5;
	public static final String[] providers = { "", "중앙일보", "중앙선데이", "조선일보",
			"동아일보", "한겨례", "경향신문", "스포츠경향", "허핑턴포스트", "블로터닷", "인벤", "테크홀릭",
			"고함20", "뉴스페퍼민트", "슬로우뉴스", "오마이뉴스", "아주경제", "", "아시아투데이" };
	private int category;

	private int detailArticleprevChildIndex;

	private boolean isFailInsertArticleList;

	private int itemId;

	private boolean isRequestArticle;

	private Handler mHandler;

	private int errorXMLId;
	
	Stack<Integer> viewArticleList;

	public ArticleListManager(Context context, ViewFlipper flipper, int itemId,
			int offset, int errorXMLId) {
		super(context, flipper, offset);
		this.category = 0;
		this.itemId = itemId;
		this.isFailInsertArticleList = false;
		this.isRequestArticle = false;
		mHandler = new Handler();
		this.errorXMLId = errorXMLId;
		viewArticleList = new Stack<Integer>();
	}

	public void setCategory(int category) {
		this.category = category;
		this.isFailInsertArticleList = false;
	}

	private void addArticleListItem(Article article) {
		View view = inflater.inflate(itemId, null);

		// TODO : Idx를 Id로 변경해야 한다
		Log.e("article.getArticleId()",
				"id : " + article.getArticleId() + "  title : " + article.getTitle() + " idx : "
						+ article.getIdx() + " category : " + article.getCategory() + " timestamp : " + article.getTimestamp());
		view.setId(article.getArticleId());
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
		ImageView image = (ImageView) view.findViewById(R.id.image);

		// TODO : title 변경
		title.setText("" + article.getTitle());
		content.setText(article.getDescription());
		TimeCalculator timeCalculator = new TimeCalculator(
				Integer.parseInt(article.getTimestamp()));
		time.setText(timeCalculator.calculatorTimeDifference());
		provider.setText(providers[Integer.parseInt(article.getProvider())]);

		if (article.getIsExistFirstImage()) {
			NewsUpImageLoader.loadImage(image, article.getFirstImageURL(),
					article.getFirstImageColor());
			Image imageInfo = new Image(article.getFirstImageURL(),
					article.getFirstImageColor());
			image.setTag(imageInfo);
		} else {
			image.setVisibility(View.GONE);
			;
		}
		addView(view);
	}

	private void addArticleListItem(final TransmissionArticle article) {
		View view = inflater.inflate(itemId, null);
		// TODO : Idx를 Id로 변경해야 한다
		view.setId(article.getIdx());
		TextView titleView = (TextView) view.findViewById(R.id.title);
		TextView contentView = (TextView) view.findViewById(R.id.content);
		TextView timeView = (TextView) view.findViewById(R.id.time);
		TextView providerView = (TextView) view.findViewById(R.id.provider);
		ImageView image = (ImageView) view.findViewById(R.id.image);

		titleView.setText(article.getTitle());
		contentView.setText(article.getContent());
		timeView.setText(article.getTime());
		providerView.setText(article.getProvider());

		if (article.getIsExistFirstImage()) {
			NewsUpImageLoader.loadImage(image, article.getImageURL(),
					article.getImageColor());
		} else {
			image.setVisibility(View.GONE);
			;
		}

		addView(view);
	}

	public void insertArticleList(
			ArrayList<TransmissionArticle> transferredArticleList) {
		for (int i = 0; i < transferredArticleList.size(); i++) {
			addArticleListItem(transferredArticleList.get(i));
		}
	}

	public void insertArticleList() {
		int articleOffset = getArticleOffset();
		List<Article> articleList = null;

		if (category == 0) {
			articleList = Article.selectMainArticleList(articleOffset);
		} else {
			articleList = Article.selectOtherArticleList(category,
					articleOffset);
		}
		int articleListSize = articleList.size();
		Log.d("NewsUp", "articleSize : " + articleListSize + " category : " + category);
		if (articleListSize == 0) {
			if (!isFailInsertArticleList) {
				View view = inflater.inflate(errorXMLId, null);
				addView(view);
				isFailInsertArticleList = true;
				if (NewsUpNetwork.isNetworkState(context)) {
					Log.d("NewsUp", "새로운 뉴스 기사 요청");
					NewsUpNetwork.getInstance().requestArticleList(category,true);
				}
				return ;
			}
			if (NewsUpNetwork.isNetworkState(context)) {
				NewsUpNetwork.getInstance().requestArticleList(category, true);
			}
		} else {
			if (isFailInsertArticleList) {
				removeFlipperItem();
				isFailInsertArticleList = false;
			}
		}

		for (int i = 0; i < articleListSize; i++) {
			addArticleListItem(articleList.get(i));
		}
		successSaveArticle();
	}
	
	public void insertArticleList(int startCategory) {
		int articleOffset = getArticleOffset();
		List<Article> articleList = null;

		articleList = Article.selectOtherArticleList(startCategory, articleOffset);
		int articleListSize = articleList.size();
		Log.d("NewsUp", "articleSize : " + articleListSize + " startCategory : " + startCategory);
		if (articleListSize == 0) {
			if (!isFailInsertArticleList) {
				LinearLayout layout = (LinearLayout)inflater.inflate(errorXMLId, null);
				
				TextView errorMessage = (TextView) layout.findViewById(R.id.text);

				errorMessage.setText("당신의 맞춤형 기사를 추천 중입니다.");
				
				addView(layout);
				isFailInsertArticleList = true;
				if (NewsUpNetwork.isNetworkState(context)) {
					Log.d("NewsUp", "새로운 뉴스 기사 요청");
					NewsUpNetwork.getInstance().requestArticleList(startCategory,
							true);
				}
				return ;
			}
		} else {
			if (isFailInsertArticleList) {
				removeFlipperItem();
				isFailInsertArticleList = false;
			}
		}

		for (int i = 0; i < articleListSize; i++) {
			addArticleListItem(articleList.get(i));
		}
	}

	private int getArticleOffset() {
		int articleOffset = getChildChount() == 1 ? 0 : getChildChount();
		Log.e("articleOffset", "" + articleOffset);

		if (isFailInsertArticleList) {
			articleOffset--;
		}

		return articleOffset;
	}

	class InsertArticleTask extends AsyncTask<Integer, Void, List<Article>> {
		@Override
		protected List<Article> doInBackground(Integer... params) {
			int articleOffset = getArticleOffset();
			int category = params[0];
			List<Article> articleList = null;

			if (category == 0) {
				articleList = Article.selectMainArticleList(articleOffset);
			} else {
				articleList = Article.selectOtherArticleList(category,
						articleOffset);
			}
			return articleList;
		}

		@Override
		protected void onPostExecute(final List<Article> articleList) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					int articleListSize = articleList.size();
					if (articleListSize == 0) {
						if (!isFailInsertArticleList) {
							View view = inflater.inflate(errorXMLId, null);
							addView(view);
							isFailInsertArticleList = true;
							if (NewsUpNetwork.isNetworkState(context)) {
								NewsUpNetwork.getInstance().requestArticleList(
										category, true);
								return ;
							}
							successSaveArticle();
							return;
						}
						if (NewsUpNetwork.isNetworkState(context)) {
							NewsUpNetwork.getInstance().requestArticleList(
									category, true);
						} else {
							successSaveArticle();
						}
					} else {

						if (isFailInsertArticleList ) {
							removeFlipperItem();
						}
						for (int i = 0; i < articleListSize; i++) {
							addArticleListItem(articleList.get(i));
						}
						Log.e("currentChildIndex", "currentChildIndex : " + currentChildIndex);
						display(currentChildIndex);
						if (isFailInsertArticleList) {
							isFailInsertArticleList = false;
						}
						//
						successSaveArticle();
					}
				}
			}, 0);
		}
	}

	public void successSaveArticle() {
		isRequestArticle = false;
	}
	
	public void failNetworkArticleRequest() {
		isRequestArticle = false;
	}

	@Override
	public void inArticleDetail(int articleId) {
		detailArticleprevChildIndex = currentChildIndex;
		display(getChildChount());
	}

	public void runOnUiThread(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outArticleDetail() {
		display(detailArticleprevChildIndex);
	}

	@Override
	public boolean upDownSwipe(int increase) {
		int checkIndex = currentChildIndex + increase;

		Log.d("NewsUp", "checkIndex : " + checkIndex + " currentChildIndex : " + currentChildIndex + " getChildChount() : " + getChildChount());
		if (currentChildIndex <= MINIMUM_ARTICLE_LIST_ATTACH_INDEX && !isRequestArticle) {
			// TODO : insertArticleListBackGround(); 로변
			isRequestArticle = true;
			InsertArticleTask insertArticleTask = new InsertArticleTask();
			insertArticleTask.execute(category);
		}
		if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
			return false;
		}
		
		if(increase == -1) {
			viewArticleList.push(flipper.getCurrentView().getId());
		} else {
			viewArticleList.pop();
		}
		display(checkIndex);
		return true;
	}

	public int getCurrentViewId() {
		return flipper.getCurrentView().getId();
	}

	public void setCurrentChildIndex(int currentChildIndex) {
		this.currentChildIndex = currentChildIndex;

	}

	public void setDetailArticleprevChildIndex(int detailArticleprevChildIndex) {
		this.detailArticleprevChildIndex = detailArticleprevChildIndex;
	}

	public int getCategory() {
		return category;
	}

	public void removeAllFlipperItem() {
		while (flipper.getChildCount() > offset) {
			flipper.removeViewAt(minChildIndex);
		}
	}
	
	public void setZeroScore() {
		Article.setZeroScore(viewArticleList);
	}

	public boolean isNetworkError() {
		return isFailInsertArticleList;
	}

	public void successNetworkArticleRequest(int category) {
		isRequestArticle = true;
		InsertArticleTask insertArticleTask = new InsertArticleTask();
		insertArticleTask.execute(category);
	}

	public void runOutArticle() {
		// 기사 다사용했을때 아이디 변경
		// TODO : 여기 처리해야한다 
		LinearLayout layout;
		if(getChildChount() == 0) {
			layout = (LinearLayout) inflater.inflate(errorXMLId, null);
			TextView errorMessage = (TextView) layout.findViewById(R.id.text);
			ProgressBar progressBar = (ProgressBar) layout
					.findViewById(R.id.progressBar);

			progressBar.setVisibility(View.GONE);
			errorMessage.setText("기사를 모두 읽으셨습니다.!");
			isFailInsertArticleList = true;
			
			addView(layout);
			display(getChildChount() - 1);
		} else {
			layout = (LinearLayout) getChildAt(0);
			TextView errorMessage = (TextView) layout.findViewById(R.id.text);
			ProgressBar progressBar = (ProgressBar) layout
					.findViewById(R.id.progressBar);

			progressBar.setVisibility(View.GONE);
			errorMessage.setText("기사를 모두 읽으셨습니다.!");
			isFailInsertArticleList = true;
		}
	}
}
