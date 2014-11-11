package article.view.list;

import image.handler.Image;

import java.util.ArrayList;
import java.util.List;

import manager.ImageViewManager;
import network.NewsUpNetwork;
import transmission.TransmissionArticle;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import article.view.ArticleFlipViewManager;

import com.example.flipview.R;

import database.Article;

public class ArticleListManager extends ArticleFlipViewManager {
	
	private static final int MINIMUM_ARTICLE_LIST_ATTACH_INDEX = 5;
	
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
		view.setId(article.getArticleId());
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
		ImageView image = (ImageView) view.findViewById(R.id.image);

		// TODO : title 변경 
		title.setText("" + article.getTitle());
		content.setText(article.getBody());
		time.setText(article.getTimestamp());
		provider.setText(article.getProvider());
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		
		ShapeDrawable drawable = new ShapeDrawable(new RectShape());
		drawable.setIntrinsicWidth(width);
		drawable.setIntrinsicHeight((int)(height * 0.3));
		drawable.getPaint().setColor(Color.parseColor(article.getFirstImageColor()));
		image.setImageDrawable(drawable);

		ImageViewManager.loadImage(image, article.getFirstImageURL());
		Image imageInfo = new Image(article.getFirstImageURL());
		image.setTag(imageInfo);
		
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
		ImageViewManager.loadImage(image, article.getImageURL());
		addView(view);
	}
	
	public void insertArticleList(ArrayList<TransmissionArticle> transferredArticleList) {
		for(int i = 0 ; i < transferredArticleList.size() ; i++) {
			addArticleListItem(transferredArticleList.get(i));
		}
	}

	public int insertArticleList() {
		int offset = getChildChount() - getChildChount() % 10; 
		List<Article> articleList = Article.selectArticleList(category, offset);
		
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
	
	
	class InsertArticleTask extends AsyncTask<Void, Void, List<Article>> {

		@Override
		protected List<Article> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int offset = getChildChount() - getChildChount() % 10;
			List<Article> articleList = Article.selectArticleList(category, offset);
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
		Log.e("index", "count : " + getChildChount() + "  currentChildIndex : " + currentChildIndex + " checkIndex : " + checkIndex);
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
}
