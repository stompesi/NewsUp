package activity;

import manager.ArticleDetailManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

import com.example.flipview.R;

public class ArticleDetailActivity extends Activity implements OnTouchListener {
	
	private static final int NONE_TAB = 0;
	private static final int DOUBLE_TAB = 2;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int CLICK_MAX_DURATION = 600;
	
	// 좌표
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	// Double Tab 확인을 위한 변수
	int clickCount;
	long clickStartTime;
	
	private static Activity articleDetailActivity;
	
	private ArticleDetailManager flipperManager;
	
	public static Activity getInstance() {
		return articleDetailActivity;
	}
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.second_left_right_in, R.anim.first_left_right_out);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_detail_activity);
		init();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int articleId = extras.getInt("articleId");
			flipperManager.startArticleRead(articleId);
		}
	}
	
	public void settingTextSize() {
		flipperManager.removeAllFlipperItem();
		flipperManager.startArticleRead();
	}

	private void init() {
		articleDetailActivity = ArticleDetailActivity.this;
		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.articleDetailFlipper);
		flipperManager = new ArticleDetailManager(this, viewFlipper);
		viewFlipper.setOnTouchListener(this);
		clickCount = 0;
	}
	
	// MenuActivity로 이동 
	private boolean moveMenuPage() {
		Intent intent = new Intent(ArticleDetailActivity.this, MenuActivity.class);
		Bundle bun = new Bundle();
		bun.putBoolean("articleDetail", true);
		intent.putExtras(bun);
		
		startActivity(intent);
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
			} else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
				initClickCount();
				flipperManager.setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
				return flipperManager.upDownSwipe(1);
				// left
			} else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
				initClickCount();
				finish();
				return false;
			} 
			if (clickCount == DOUBLE_TAB) {
				long time = System.currentTimeMillis() - clickStartTime;
				if (time <= CLICK_MAX_DURATION) {
					initClickCount();
					moveMenuPage();
					return false;
				}
				initClickCount();
			}
		}
		return true;
	}
}
