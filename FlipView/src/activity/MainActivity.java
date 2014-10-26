package activity;

import java.util.ArrayList;

import lockscreen.service.LockScreenService;
import manager.ArticleDetailManager;
import manager.ArticleFlipViewManager;
import manager.ArticleListManager;
import setting.RbPreference;
import transmission.TransmissionArticle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

import com.example.flipview.R;

public class MainActivity extends Activity implements OnTouchListener {
	
	private static final int NONE_TAB = 0;
	private static final int DOUBLE_TAB = 2;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int CLICK_MAX_DURATION = 600;
	
	// LockScreen Activity에서 MainActivity를 kill 하기위함
	private static Activity mainActivity;
	
	// 좌표
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	// Double Tab 확인을 위한 변수
	int clickCount;
	long clickStartTime;
	
	ArticleFlipViewManager flipperManager;
	ArticleListManager articleListManager;
	ArticleDetailManager articleDetailManager;

	public static MainActivity getInstance() {
		return (MainActivity) mainActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().hide();
		
		init();
		
		/***
		 * 1. LockScreen Activity에서 MainActivity intent 했을 때 : article detail
		 * show 2. App을 바로 실행 했을 때 : article list show
		 */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int articleId = extras.getInt("articleId");
			ArrayList<TransmissionArticle> transferredArticleList = (ArrayList<TransmissionArticle>)getIntent().getSerializableExtra("articleList");
			articleListManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
			articleListManager.insertArticleList(transferredArticleList);
			articleListManager.setCurrentChildIndex(extras.getInt("setCurrentChildIndex"));
			moveArticleDetail(articleId);
		} else {
			articleListManager.insertArticleList();
			articleListManager.display(articleListManager.getChildChount());
		}
	}

	private void init() {
		mainActivity = MainActivity.this;
		
		ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.articleListFlipper);
		ViewFlipper articleDetailFlipper = (ViewFlipper) findViewById(R.id.articleDetailFlipper);
		
		articleListFlipper.setOnTouchListener(this);
		articleDetailFlipper.setOnTouchListener(this);
		
		articleListManager = new ArticleListManager(this, articleListFlipper, 2, R.layout.article_list_item);
		articleDetailManager = new ArticleDetailManager(this, articleDetailFlipper, 1);
		
		flipperManager = articleListManager;
		clickCount = 0;
		
		View view = getLayoutInflater().inflate(R.layout.menu, null);
		MenuClickListener menuClickListener = new MenuClickListener();
		view.findViewById(R.id.main).setOnClickListener(menuClickListener);
		view.findViewById(R.id.politicalSociety).setOnClickListener(menuClickListener);
		view.findViewById(R.id.economy).setOnClickListener(menuClickListener);
		view.findViewById(R.id.culture).setOnClickListener(menuClickListener);
		view.findViewById(R.id.sports).setOnClickListener(menuClickListener);
		view.findViewById(R.id.it).setOnClickListener(menuClickListener);
		view.findViewById(R.id.health).setOnClickListener(menuClickListener);
		view.findViewById(R.id.entertainment).setOnClickListener(menuClickListener);
		view.findViewById(R.id.world).setOnClickListener(menuClickListener);
		view.findViewById(R.id.sympathy).setOnClickListener(menuClickListener);
		view.findViewById(R.id.game).setOnClickListener(menuClickListener);
		view.findViewById(R.id.setting).setOnClickListener(menuClickListener);
		articleListManager.getFlipper().addView(view, 0);
		
		view = getLayoutInflater().inflate(R.layout.menu, null);
		view.findViewById(R.id.main).setOnClickListener(menuClickListener);
		view.findViewById(R.id.politicalSociety).setOnClickListener(menuClickListener);
		view.findViewById(R.id.economy).setOnClickListener(menuClickListener);
		view.findViewById(R.id.culture).setOnClickListener(menuClickListener);
		view.findViewById(R.id.sports).setOnClickListener(menuClickListener);
		view.findViewById(R.id.it).setOnClickListener(menuClickListener);
		view.findViewById(R.id.health).setOnClickListener(menuClickListener);
		view.findViewById(R.id.entertainment).setOnClickListener(menuClickListener);
		view.findViewById(R.id.world).setOnClickListener(menuClickListener);
		view.findViewById(R.id.sympathy).setOnClickListener(menuClickListener);
		view.findViewById(R.id.game).setOnClickListener(menuClickListener);
		view.findViewById(R.id.setting).setOnClickListener(menuClickListener);
		articleDetailManager.getFlipper().addView(view, 0);
		
		RbPreference pref = new RbPreference(MainActivity.this);
		if(pref.getValue(RbPreference.IS_LOCK_SCREEN, false)) {
			Intent intent = new Intent(MainActivity.this, LockScreenService.class);
			startService(intent);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(flipperManager == articleListManager && !(flipperManager.isMenuState())) {
				return super.onKeyDown(keyCode, event);
			} else {
				flipperManager.setAnimation(R.anim.second_left_right_in,
						R.anim.first_left_right_out);
				backEvent();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// detail -> List
	// menu -> detail, list
	private boolean backEvent() {
		if (flipperManager.isMenuState()) {
			flipperManager.outMenuPage();
			return true;
		} else if (flipperManager != articleDetailManager) {
			return false;
		}
		flipperManager = articleListManager;
		articleDetailManager.outArticleDetail();
		articleListManager.outArticleDetail();
		
		return true;
	}
	
	// list -> detail
	private boolean moveArticleDetail(int articleId) {
		if (flipperManager == articleDetailManager || flipperManager.isMenuState()
				|| articleListManager.getCurrentChildIndex() == 1) {
			return false;
		} 
		flipperManager = articleDetailManager;
		articleListManager.inArticleDetail(articleId);
		articleDetailManager.inArticleDetail(articleId);
		return true;
	}

	// detail, list -> menu
	private boolean moveMenuPage() {
		if (!flipperManager.isMenuState()) {
			return flipperManager.inMenuPage();
		}
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
				flipperManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
				return backEvent();
				// right
			} else if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
				initClickCount();
				flipperManager.setAnimation(R.anim.first_left_right_in, R.anim.second_up_down_out);
				return moveArticleDetail(articleListManager.getCurrentViewId());
			}
			if (clickCount == DOUBLE_TAB && 
					((flipperManager == articleListManager && flipperManager.getCurrentChildIndex() != 1)
					|| (flipperManager == articleDetailManager && flipperManager.getCurrentChildIndex() != 0))) {
				long time = System.currentTimeMillis() - clickStartTime;
				if (time <= CLICK_MAX_DURATION) {
					initClickCount();
					flipperManager.setAnimation(R.anim.fade_in, R.anim.fade_out);
					return moveMenuPage();
				}
				initClickCount();
			}
		}
		return true;
	}

	
	
	private class MenuClickListener implements OnClickListener {

		@Override
		synchronized public void onClick(View v) {
			int category = 0;
			switch(v.getId()) {
			case R.id.main:
				category = 0;
				break;
			case R.id.politicalSociety:
				category = 1;
				break;
			case R.id.economy:
				category = 2;
				break;
			case R.id.culture:
				category = 3;
				break;
			case R.id.sports:
				category = 4;
				break;
			case R.id.game:
				category = 5;
				break;
			case R.id.it:
				category = 6;
				break;
			case R.id.health:
				category = 7;
				break;
			case R.id.entertainment:
				category = 8;
				break;
			case R.id.world:
				Log.d("Main", "111111");
				category = 9;
				break;
			case R.id.sympathy:
				category = 10;
				break;
			case R.id.setting:

				Intent i = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(i);

				return; 
			}
			articleListManager.changeCategory(category);
			backEvent();
			if(flipperManager == articleDetailManager) {
				flipperManager.setAnimation(R.anim.second_left_right_in, R.anim.first_left_right_out);
				backEvent();
			}
		}
	}
}
