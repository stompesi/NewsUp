package manager;

import image.handler.Image;

import java.util.ArrayList;

import network.Network;
import transmission.TransmissionArticle;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;
import application.NewsUpApp;

import com.android.volley.toolbox.NetworkImageView;
import com.example.flipview.R;

import database.Article;

public class ArticleListManager extends ArticleFlipViewManager {
	
	private static final int MINIMUM_ARTICLE_LIST_ATTACH_INDEX = 5;
	private static final int CACHE_ARTICLE_COUNT = 20;
	
	private int category;
	
	private int detailArticleprevChildIndex;
	
	private boolean isFailInsertArticleList;
	
	private int itemId;
	
	public ArticleListManager(Context context, ViewFlipper flipper, int offset, int itemId) {
		super(context, flipper, offset);
		this.category = 0;
		this.itemId = itemId;
		this.isFailInsertArticleList = false;
	}
	
	public void changeCategory(int category) {
		this.category = category;
		isFailInsertArticleList = false;
		removeAllFlipperItem();
		insertArticleList();
		menuPrevChildIndex = detailArticleprevChildIndex = getChildChount();
	}

	private void addArticleListItem(Article article) {
		View view = inflater.inflate(itemId, null);

		// TODO : Idx를 Id로 변경해야 한다
		view.setId(article.getIdx());
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
		NetworkImageView image = (NetworkImageView) view.findViewById(R.id.image);

		title.setText("" + article.getIdx());
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

		image.setImageUrl(article.getFirstImageURL(), NewsUpApp.getInstance().getImageLoader());
		Image imageInfo = new Image(article.getFirstImageURL());
		image.setTag(imageInfo);
		
		addView(view);
	}
	
	private void addArticleListItem(final TransmissionArticle article) {
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				View view = inflater.inflate(itemId, null);
				// TODO : Idx를 Id로 변경해야 한다
				view.setId(article.getIdx());
				TextView titleView = (TextView) view.findViewById(R.id.title);
				TextView contentView = (TextView) view.findViewById(R.id.content);
				TextView timeView = (TextView) view.findViewById(R.id.time);
				TextView providerView = (TextView) view.findViewById(R.id.provider);
				NetworkImageView image = (NetworkImageView) view.findViewById(R.id.image);

				titleView.setText(article.getTitle());
				contentView.setText(article.getContent());
				timeView.setText(article.getTime());
				providerView.setText(article.getProvider());
				image.setImageUrl(article.getImageURL(), NewsUpApp.getInstance().getImageLoader());
				addView(view);
			}
		});
	}
	
	public void insertArticleList(ArrayList<TransmissionArticle> transferredArticleList) {
		for(int i = 0 ; i < transferredArticleList.size() ; i++) {
			addArticleListItem(transferredArticleList.get(i));
		}
	}

	public int insertArticleList() {
		int offset;
		offset = (getChildChount() < CACHE_ARTICLE_COUNT) ?  CACHE_ARTICLE_COUNT - getChildChount() : 0;
		ArrayList<Article> articleList = articleDBManager.selectArticleList(category, offset);
		
		int articleListSize = articleList.size();

		 if(articleListSize == 0) {
			 if(!isFailInsertArticleList) {
				 View view = inflater.inflate(R.layout.network_error, null);
				 addView(view);
				 isFailInsertArticleList = true;
				 return 1;
			 }
			 if(Network.isNetworkState(context)) {
				Network.getInstance().requestArticleList(category);
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
	
	@Override
	public void inArticleDetail(int articleId) {
		detailArticleprevChildIndex = currentChildIndex;
		display(getChildChount() + 1);
	}
	
	@Override
	public void outArticleDetail() {
		setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
		display(detailArticleprevChildIndex);
	}
	
	@Override
	public boolean upDownSwipe(int increase){
		int checkIndex = currentChildIndex + increase;
		
		if (currentChildIndex <= MINIMUM_ARTICLE_LIST_ATTACH_INDEX) {
			checkIndex += insertArticleList();
		}
		
		if (checkIndex > flipper.getChildCount() - offset
				|| isMenuState()
				|| checkIndex < minChildIndex) {
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
}
