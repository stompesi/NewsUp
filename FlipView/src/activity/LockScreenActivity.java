package activity;

import java.util.ArrayList;

import manager.ArticleListManager;
import transmission.TransmissionArticle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ViewFlipper;
import clock.Clock;

import com.example.flipview.R;


@SuppressLint("ClickableViewAccessibility")
public class LockScreenActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int ARTICLE_END_ITEM_INDEX = 1;
	
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	private ArticleListManager articleListManager;
	
	private static LockScreenActivity lockScreenActivity;
	
	private TextView timeView;
	private TextView dateView;
	
	private Clock clock;

	private Handler timerHandler;
	private Runnable timerRunnable;
	
	
	public static LockScreenActivity getInstance() {
		return lockScreenActivity;
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("Create", "aaa");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lockscreen_activity);
		
		init();
		
		/***
		 * FLAG_SHOW_WHEN_LOCKED : 기본장금화면 보다 위에 Activity를 띄우는 것
		 * FLAG_DISMISS_KEYGUARD : 안드로이드 기본 잠금화면을 없애는 것 
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		articleListManager.insertArticleList();
		articleListManager.display(articleListManager.getChildChount());
	}
	@Override
	protected void onUserLeaveHint() {
		finish();
	}
	private void init() {
		lockScreenActivity = LockScreenActivity.this;
		
		ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.lockScreenViewFlipper);
		articleListFlipper.setOnTouchListener(this);
		
		articleListManager = new ArticleListManager(this, articleListFlipper, R.layout.lockscreen_article_list_item);
		
		timeView = (TextView) findViewById(R.id.lockScreenTime);
		dateView = (TextView) findViewById(R.id.lockScreenDate);
		
		clock = new Clock();
		
		timerRunnable = new Runnable() {
            @Override
            public void run() {
            	String time = clock.getTime() + clock.getAMPM();
				String date = clock.getDate() + clock.getWeek();
				
				timeView.setText(time);
				dateView.setText(date);
				timerHandler.postDelayed(timerRunnable, 1000);
            }
        };
         
        timerHandler = new Handler();
        timerHandler.postDelayed(timerRunnable, 0);
		
	}
	@Override
    protected void onDestroy() {
        Log.i("test", "onDstory()");
        timerHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }
	
	private boolean moveNewsUpApp() {
		if(articleListManager.getCurrentChildIndex() == 1) {
			return false;
		}
		Bundle bun = new Bundle();
		ArticleListActivity mainActivity = (ArticleListActivity) ArticleListActivity.getInstance();
		mainActivity.finish();
		
		Intent intent = new Intent(LockScreenActivity.this, ArticleListActivity.class);
		ArrayList<TransmissionArticle> articleList = new ArrayList<TransmissionArticle>();
		
		for(int i = articleListManager.getChildChount() ; i >= ARTICLE_END_ITEM_INDEX  ; i--) {
			TransmissionArticle transmissionArticle = new TransmissionArticle(articleListManager.getChildAt(i));
			articleList.add(transmissionArticle);
		}
		bun.putInt("articleId", articleListManager.getCurrentViewId());
		bun.putInt("setCurrentChildIndex", articleListManager.getCurrentChildIndex()); // add two parameters: a string and a boolean
		bun.putSerializable("articleList", articleList);
		intent.putExtras(bun);
		startActivity(intent);
		this.overridePendingTransition(R.anim.first_left_right_in, R.anim.second_up_down_out);
		finish();
		return false;
	}

	
	// finish 
	private boolean offLockScreen() {
			finish();
			return false;
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK:
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		
		if (v != articleListManager.getFlipper()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			yAtDown = event.getY();
			xAtDown = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			yAtUp = event.getY(); 
			xAtUp = event.getX();
			if (yAtDown - yAtUp > SWIPE_MIN_DISTANCE) {
				articleListManager.setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
				return articleListManager.upDownSwipe(-1);
			// down
			} else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
				articleListManager.setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
				return articleListManager.upDownSwipe(1);
			// left
			} else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
				return offLockScreen();
			// right 
			} else if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
				return moveNewsUpApp();
			}  
		}
		return true;
	}
}




