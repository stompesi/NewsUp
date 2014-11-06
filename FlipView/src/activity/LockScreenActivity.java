package activity;

import java.util.ArrayList;

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
import article.view.list.ArticleListManager;
import clock.Clock;

import com.example.flipview.R;


@SuppressLint("ClickableViewAccessibility")
public class LockScreenActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int ARTICLE_END_ITEM_INDEX = 0;
	
	private float xAtDown, xAtUp, yAtDown, yAtUp;

	private ArticleListManager articleListManager;
	
	private static LockScreenActivity lockScreenActivity;
	
	private TextView timeView;
	private TextView dateView;
	private TextView ampmView;
	
	private Clock clock;

	private Handler timerHandler;
	private Runnable timerRunnable;
	
	
	public void successSaveArticle(){
		articleListManager.successSaveArticle();
	}
	
	public static LockScreenActivity getInstance() {
		return lockScreenActivity;
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("Create", "aaa");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lockscreen);
		
		init();
		
		/***
		 * FLAG_SHOW_WHEN_LOCKED : 기본장금화면 보다 위에 Activity를 띄우는 것
		 * FLAG_DISMISS_KEYGUARD : 안드로이드 기본 잠금화면을 없애는 것 
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		articleListManager.insertArticleList();
		articleListManager.display(articleListManager.getChildChount() - 1);
	}
	@Override
	
	protected void onUserLeaveHint() {
		finish();
	}
	private void init() {
		lockScreenActivity = LockScreenActivity.this;
		
		ViewFlipper articleListFlipper = (ViewFlipper) findViewById(R.id.lockScreenViewFlipper);
		articleListFlipper.setOnTouchListener(this);
		
		articleListManager = new ArticleListManager(this, articleListFlipper, R.layout.view_lockscreen_article_list, 0);
		
		timeView = (TextView) findViewById(R.id.lockScreenTime);
		dateView = (TextView) findViewById(R.id.lockScreenDate);
		ampmView = (TextView) findViewById(R.id.lockScreenAM);
		
		clock = new Clock();
		
		timerRunnable = new Runnable() {
            @Override
            public void run() {
            	String time = clock.getTime(); 
            	String ampm = clock.getAMPM();
				String date = clock.getDate() + clock.getWeek();
				
				timeView.setText(time);
				ampmView.setText(ampm);
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
		ArticleActivity mainActivity = (ArticleActivity) ArticleActivity.getInstance();
		mainActivity.finish();
		
		Intent intent = new Intent(LockScreenActivity.this, ArticleActivity.class);
		ArrayList<TransmissionArticle> articleList = new ArrayList<TransmissionArticle>();
		
		for(int i = articleListManager.getChildChount() - 1 ; i >= ARTICLE_END_ITEM_INDEX  ; i--) {
			TransmissionArticle transmissionArticle = new TransmissionArticle(articleListManager.getChildAt(i));
			articleList.add(transmissionArticle);
		}
		bun.putInt("articleId", articleListManager.getCurrentViewId());
		bun.putInt("setCurrentChildIndex", articleListManager.getCurrentChildIndex()); // add two parameters: a string and a boolean
		bun.putSerializable("articleList", articleList);
		intent.putExtras(bun);
		startActivity(intent);
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
	
	public void reFresh() {
		articleListManager.removeAllFlipperItem();
		articleListManager.insertArticleList();
		articleListManager.display(articleListManager.getChildChount() - 1);
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
			// up
			}  
		}
		return true;
	}
}




