package com.example.newsup.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

import com.example.newsup.R;
import com.example.newsup.activity.transmission.structure.TransmissionArticle;
import com.example.newsup.background.service.LockScreenService;
import com.example.newsup.setting.RbPreference;
import com.example.newsup.view.ArticleDetailManager;
import com.example.newsup.view.ArticleFlipViewManager;
import com.example.newsup.view.ArticleListManager;
import com.example.newsup.view.structure.LayoutInfo;
import com.google.android.youtube.player.YouTubeBaseActivity;

public class ArticleActivity extends YouTubeBaseActivity implements OnTouchListener {
	
	private static final int NONE_TAB = 0;
	private static final int DOUBLE_TAB = 2;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int CLICK_MAX_DURATION = 600;
	
	// MainActivity를 kill 하기위함
	private static Activity mainActivity;
	
	// 좌표
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	// Double Tab 확인을 위한 변수
	int clickCount;
	long clickStartTime;
	
	ArticleListManager articleListManager;
	ArticleDetailManager articleDetailManager;
	ArticleFlipViewManager flipperManager;
	
	private int currentArticleId;

	
	private boolean isAnimationning;
	public static ArticleActivity getInstance() {
		return (ArticleActivity) mainActivity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		init();
		
		/***
		 * 1. LockScreen Activity에서 MainActivity intent 했을 때 : article detail
		 * show 2. App을 바로 실행 했을 때 : article list show
		 */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if(extras.containsKey("category")) {
				articleListManager.setCategory(extras.getInt("category"));
			} else {
				int articleId = extras.getInt("articleId");
				ArrayList<TransmissionArticle> transferredArticleList = (ArrayList<TransmissionArticle>)getIntent().getSerializableExtra("articleList");
				articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
				articleListManager.insertArticleList(transferredArticleList);
				articleListManager.setCurrentChildIndex(extras.getInt("setCurrentChildIndex"));
				moveArticleDetail(articleId);
				return ;
			}
		}
		articleListManager.insertArticleList();
		articleListManager.display(articleListManager.getChildChount() - 1);
	}

	private void init() {
		mainActivity = ArticleActivity.this;
		
		RbPreference pref = new RbPreference(ArticleActivity.this);
		if(pref.getValue(RbPreference.IS_LOCK_SCREEN, false)) {
			Intent intent = new Intent(ArticleActivity.this, LockScreenService.class);
			startService(intent);
		}
		
		ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.articleListFlipper);
		ViewFlipper articleDetailFlipper = (ViewFlipper) findViewById(R.id.articleDetailFlipper);
		
		articleListFlipper.setOnTouchListener(this);
		articleDetailFlipper.setOnTouchListener(this);
		
		articleListManager = new ArticleListManager(this, articleListFlipper, R.layout.view_article_list, 1);
		articleDetailManager = new ArticleDetailManager(this, articleDetailFlipper, 0);
		
		flipperManager = articleListManager;
		
		articleListManager.getFlipper().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 @Override
			  public void onGlobalLayout() {
			   //now we can retrieve the width and height
			   int width = articleListManager.getFlipper().getWidth();
			   int height = articleListManager.getFlipper().getHeight();
			   
//			   articleDetailManager.setLayoutWidth(width);
//			   articleDetailManager.setLayoutHeight(height);
			   
			   LayoutInfo layoutInfo = LayoutInfo.getInstance();
			   
			   layoutInfo.calLayoutInfo(width, height);
			   articleListManager.getFlipper().getViewTreeObserver().removeOnGlobalLayoutListener(this);
			 }
		});
		
	}
	
	public void changeCategory(int category) {
		if(flipperManager == articleDetailManager) {
			articleDetailManager.outArticleDetail();
			articleDetailManager.removeAllFlipperItem();
			flipperManager = articleListManager;
		}
		articleListManager.setCategory(category);
		articleListManager.removeAllFlipperItem();
		articleListManager.insertArticleList();
		flipperManager.setAnimation(R.anim.in, R.anim.out);
		articleListManager.display(articleListManager.getChildChount() - 1);
	}
	
	public void successSaveArticle(){
		articleListManager.successSaveArticle();
	}
	
	public void changeTextSize() {
		if(flipperManager == articleDetailManager) {
			articleDetailManager.changeTextSize(currentArticleId);;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(flipperManager == articleDetailManager) {
				articleDetailManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
				articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
				backEvent();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// detail -> List
	// menu -> detail, list
	private boolean backEvent() {
		articleListManager.getFlipper().getInAnimation().setAnimationListener(new Animation.AnimationListener() {
		      public void onAnimationStart(Animation animation) {
		    	  Log.e("start", "backEvent start");
		    	  isAnimationning = true;
		      }
		      public void onAnimationRepeat(Animation animation) {}
		      public void onAnimationEnd(Animation animation) {
		    	  Log.e("end", "backEvent end");
		    	  articleDetailManager.removeAllFlipperItem();
		    	  isAnimationning = false;
		    	  articleListManager.getFlipper().getInAnimation().setAnimationListener(null);
		      }
		   });
		
		flipperManager = articleListManager;
		articleDetailManager.outArticleDetail();
		articleListManager.outArticleDetail();
		return true;
	}
	
	// list -> detail
	private boolean moveArticleDetail(int articleId) {
		articleListManager.getFlipper().getInAnimation().setAnimationListener(new Animation.AnimationListener() {
		      public void onAnimationStart(Animation animation) {
		    	  Log.e("start", "moveArticleDetail start");
		    	  isAnimationning = true;
		      }
		      public void onAnimationRepeat(Animation animation) {}
		      public void onAnimationEnd(Animation animation) {
		    	  Log.e("end", "moveArticleDetail end");
		    	  isAnimationning = false;
		    	  articleListManager.getFlipper().getInAnimation().setAnimationListener(null);
		      }
		   });
		this.currentArticleId = articleId;
		flipperManager = articleDetailManager;
		articleListManager.inArticleDetail(articleId);
		articleDetailManager.inArticleDetail(articleId);
		return true;
	}

	// detail, list -> menu
	private boolean moveMenuPage() {
		Intent intent = new Intent(ArticleActivity.this, MenuActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		return false;
	}

	private void initClickCount() {
		clickCount = 0;
	}
	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
		if (v != flipperManager.getFlipper()) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			yAtDown = event.getY();
			xAtDown = event.getX();
			if(clickCount == NONE_TAB) {
				clickStartTime = System.currentTimeMillis();
			}
			clickCount++;
			break;
		case MotionEvent.ACTION_MOVE:
			/***
			 * TODO : 땡겨지는 에니메이션 주기
			 */
			break;
		case MotionEvent.ACTION_UP:
			yAtUp = event.getY();
			xAtUp = event.getX();
			
			if(!isAnimationning){
				// up
				if (yAtDown - yAtUp > SWIPE_MIN_DISTANCE) {
					initClickCount();
					flipperManager.setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
					return flipperManager.upDownSwipe(-1);
					// down
				} else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
					initClickCount();
					flipperManager.setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
					return flipperManager.upDownSwipe(1);
					// left
				} else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
					initClickCount();
					if (flipperManager != articleDetailManager) {
						return false;
					}
					articleDetailManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
					articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
					return backEvent();
					// right
				} else if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
					initClickCount();
					if (flipperManager == articleDetailManager || flipperManager.isErrorView()) {
						return false;
					}
					articleListManager.setAnimation(R.anim.first_left_right_in, R.anim.second_up_down_out);
					return moveArticleDetail(articleListManager.getCurrentViewId());
				}
				if (clickCount == DOUBLE_TAB) {
					long time = System.currentTimeMillis() - clickStartTime;
					if (time <= CLICK_MAX_DURATION) {
						initClickCount();
						flipperManager.setAnimation(R.anim.fade_in, R.anim.fade_out);
						return moveMenuPage();
					}
					initClickCount();
				}
			}
		}
		
		return true;
	}
	
	
	public void changeIsAnimationningFlag() {
		isAnimationning = false;
	}
}
