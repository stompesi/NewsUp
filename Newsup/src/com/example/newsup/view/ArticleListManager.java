package com.example.newsup.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
	public static final String[] providers = {"", "중앙일보", "중앙선데이", "조선일보", "동아일보", "한겨례", "경향신문", "스포츠경향"
		, "허핑턴포스트", "블로터닷", "인벤", "테크홀릭", "고함20", "뉴스페퍼민트", "슬로우뉴스", "오마이뉴스", "아주경제", "", "아시아투데이"};
	private int category;
	
	private int detailArticleprevChildIndex;
	
	private boolean isFailInsertArticleList;
	
	private int itemId;
	
	private boolean isRequestArticle;
	
	private Handler mHandler;
	
	public ArticleListManager(Context context, ViewFlipper flipper, int itemId, int offset) {
		super(context, flipper, offset);
		this.category = 0;
		this.itemId = itemId;
		this.isFailInsertArticleList = false;
		this.isRequestArticle = false;
		mHandler = new Handler();
	}
	
	public void setCategory(int category) {
		this.category = category;
		this.isFailInsertArticleList = false;
	}
	
	private void addArticleListItem(Article article) {
		View view = inflater.inflate(itemId, null);

		// TODO : Idx를 Id로 변경해야 한다
		Log.e("article.getArticleId()", "article.getArticleId() : " + article.getTitle() + " idx : " + article.getIdx() );
		view.setId(article.getArticleId());
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
		ImageView image = (ImageView) view.findViewById(R.id.image);

		// TODO : title 변경 
		title.setText("" + article.getTitle());
		content.setText(article.getDescription());
		TimeCalculator timeCalculator = new TimeCalculator(Integer.parseInt(article.getTimestamp())); 
		time.setText(timeCalculator.calculatorTimeDifference());
		provider.setText(providers[Integer.parseInt(article.getProvider())]);
		
		if(article.getIsExistFirstImage()) {
			NewsUpImageLoader.loadImage(image, article.getFirstImageURL(), article.getFirstImageColor());
			Image imageInfo = new Image(article.getFirstImageURL(), article.getFirstImageColor());
			image.setTag(imageInfo);
		} else {
			image.setVisibility(View.GONE);;
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
		
		if(article.getIsExistFirstImage()) {
			NewsUpImageLoader.loadImage(image, article.getImageURL(), article.getImageColor());
		} else {
			image.setVisibility(View.GONE);;
		}
		
		addView(view);
	}
	
	public void insertArticleList(ArrayList<TransmissionArticle> transferredArticleList) {
		for(int i = 0 ; i < transferredArticleList.size() ; i++) {
			addArticleListItem(transferredArticleList.get(i));
		}
	}

	public int insertArticleList() {
		int articleOffset = getArticleOffset(); 
		List<Article> articleList = null;
		
		if(category == 0) {
			articleList = Article.selectMainArticleList(articleOffset);
		} else {
			articleList = Article.selectOtherArticleList(category, articleOffset);
		}
		
		int articleListSize = articleList.size();

		 if(articleListSize == 0) {
			 if(!isFailInsertArticleList) {
				 View view = inflater.inflate(R.layout.view_network_error, null);
				 addView(view);
				 isFailInsertArticleList = true;
				 return 1;
			 }
			 if(NewsUpNetwork.isNetworkState(context)) {
				NewsUpNetwork.getInstance().requestArticleList(category);
			 } 
		 } else {
			 if(isFailInsertArticleList) {
				 removeFlipperItem();
				 isFailInsertArticleList = false;
			 }
		 }
		 
		for (int i = 0; i < articleListSize; i++) {
			addArticleListItem(articleList.get(i));
		}
		return articleListSize;	
	}
	
	private int getArticleOffset() {
		int articleOffset = getChildChount() == 1 ? 0 : getChildChount();
		Log.e("articleOffset", "" + articleOffset);
		
		if(isFailInsertArticleList) {
			articleOffset--;
		}
		
		return articleOffset;
	}
	class InsertArticleTask extends AsyncTask<Void, Void, List<Article>> {

		@Override
		protected List<Article> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int articleOffset = getArticleOffset();
			List<Article> articleList = null;
			
			
			if(category == 0) {
				articleList = Article.selectMainArticleList(articleOffset);
			} else {
				articleList = Article.selectOtherArticleList(category, articleOffset);
			}
			return  articleList;
		}
		
		
		 @Override
		 protected void onPostExecute(final List<Article> articleList) {
			 mHandler.postDelayed(new Runnable() {
		            @Override
		            public void run() {
		            	int articleListSize = articleList.size();
			   			 currentChildIndex += articleListSize;
			   			 if(articleListSize == 0) {
			   				 if(!isFailInsertArticleList) {
			   					currentChildIndex++;
			   					 View view = inflater.inflate(R.layout.view_network_error, null);
			   					 addView(view);
			   					 isFailInsertArticleList = true;
			   					successSaveArticle();
			   					 return ;
			   				 }
			   				 if(NewsUpNetwork.isNetworkState(context)) {
			   					NewsUpNetwork.getInstance().requestArticleList(category);
			   				 } else {
			   					successSaveArticle();
			   				 }
			   			 } else {
			   				 if(isFailInsertArticleList) {
			   					removeFlipperItem();
			   					currentChildIndex--;
			   					isFailInsertArticleList = false;
			   				 }
			   				for (int i = 0; i < articleListSize; i++) {
				   				addArticleListItem(articleList.get(i));
				   			}
			   				display(currentChildIndex);
			   				successSaveArticle();
			   			 }
		            }
		        }, 0); 
		 }
	}
	public void successSaveArticle() {
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
	public boolean upDownSwipe(int increase){
		int checkIndex = currentChildIndex + increase;
		
		if (currentChildIndex <= MINIMUM_ARTICLE_LIST_ATTACH_INDEX && !isRequestArticle) {
			// TODO : insertArticleListBackGround(); 로변
			isRequestArticle = true;
			InsertArticleTask insertArticleTask = new InsertArticleTask();
			insertArticleTask.execute();
		}
		if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
			return false;
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
		Article.setZeroScore(currentChildIndex + 1);
	}
}
