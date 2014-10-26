package activity;

import java.util.ArrayList;

import lockscreen.service.LockScreenService;
import manager.ArticleListManager;
import setting.RbPreference;
import transmission.TransmissionArticle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

import com.example.flipview.R;

public class ArticleListActivity extends Activity implements OnTouchListener {
	
	private static final int NONE_TAB = 0;
	private static final int DOUBLE_TAB = 2;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int CLICK_MAX_DURATION = 600;
	
	// MainActivity를 kill 하기위함
	private static Activity articleListActivity;
	
	// 좌표
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	// Double Tab 확인을 위한 변수
	int clickCount;
	long clickStartTime;
	
	ArticleListManager articleListManager;

	public static Activity getInstance() {
		return articleListActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_list_activity);
		
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
				ArrayList<TransmissionArticle> transferredArticleList = (ArrayList<TransmissionArticle>)getIntent().getSerializableExtra("articleList");
				articleListManager.insertArticleList(transferredArticleList);
				articleListManager.display(extras.getInt("setCurrentChildIndex") - 1);
				moveArticleDetail(extras.getInt("articleId"));
				return ;
			}
		}
		articleListManager.insertArticleList();
		articleListManager.display(articleListManager.getChildChount());
	}

	private void init() {
		articleListActivity = ArticleListActivity.this;
		
		ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.articleListFlipper);
		articleListFlipper.setOnTouchListener(this);
		
		articleListManager = new ArticleListManager(this, articleListFlipper, R.layout.article_list_item);
		
		clickCount = 0;
		
		RbPreference pref = new RbPreference(ArticleListActivity.this);
		if(pref.getValue(RbPreference.IS_LOCK_SCREEN, false)) {
			Intent intent = new Intent(ArticleListActivity.this, LockScreenService.class);
			startService(intent);
		}
	}
	
	// list -> detail
	private boolean moveArticleDetail(int articleId) {
		Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
		
		Bundle bun = new Bundle();
		bun.putInt("articleId", articleId);
		intent.putExtras(bun);
		
		startActivity(intent);
		this.overridePendingTransition(R.anim.first_left_right_in, R.anim.second_up_down_out);
		return true;
	}

	// list -> menu
	private boolean moveMenuPage() {
		Intent i = new Intent(ArticleListActivity.this, MenuActivity.class);
		startActivity(i);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		return false;
	}

	private void initClickCount() {
		clickCount = 0;
	}
	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
		if (v != articleListManager.getFlipper() ) {
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
			// up
			if (yAtDown - yAtUp > SWIPE_MIN_DISTANCE) {
				initClickCount();
				articleListManager.setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
				return articleListManager.upDownSwipe(-1);
				// down
			} else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
				initClickCount();
				articleListManager.setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
				return articleListManager.upDownSwipe(1);
				// right
			} else if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
				initClickCount();
				 moveArticleDetail(articleListManager.getCurrentViewId());
				 return false;
			}
			if (clickCount == DOUBLE_TAB) {
				long time = System.currentTimeMillis() - clickStartTime;
				if (time <= CLICK_MAX_DURATION) {
					initClickCount();
					return moveMenuPage();
				}
				initClickCount();
			}
		}
		return true;
	}
}
