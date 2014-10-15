package activity;


import lockscreen.service.LockScreenService;
import network.Network;
import setting.RbPreference;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;
import application.NewsUpApp;

import com.example.flipview.R;
import com.urqa.clientinterface.URQAController;


@SuppressLint("ClickableViewAccessibility")
public class StartActivity extends Activity implements OnTouchListener {
	
	private static final int ARTICLE_END_ITEM_INDEX = 1;
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int ARTICLE_OFFSET = 1;
	
	private ViewFlipper startFlipper;

	private float yAtDown;
	private float yAtUp;
	
	private int currentFlipperChildSize, currentChildIndex;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		getActionBar().hide();
		URQAController.InitializeAndStartSession(getApplicationContext(), "3049748C");
		
		RbPreference pref = new RbPreference(this);
		// 앱 처음 실행 
		if(pref.getValue(RbPreference.PREF_IS_INTRO, true)) {
			init();
			display(startFlipper.getChildCount() - ARTICLE_OFFSET);
		} else {
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void init() {
		startFlipper = (ViewFlipper) findViewById(R.id.startFlipper);
		startFlipper.setOnTouchListener(this);
		currentFlipperChildSize = startFlipper.getChildCount() - ARTICLE_OFFSET;
		
		View view = startFlipper.getChildAt(ARTICLE_END_ITEM_INDEX);
		
		Button startButton = (Button) view.findViewById(R.id.btn);
		startButton.setOnClickListener(new StartClickLitener());
		startButton.setText("시작");
		for (int i = currentFlipperChildSize; i > ARTICLE_END_ITEM_INDEX ; i--){
			view = startFlipper.getChildAt(i);
			startButton = (Button) view.findViewById(R.id.btn);
			startButton.setOnClickListener(new NextClickLitener());
			startButton.setText("다음");
		}
		
		Network.getInstance().requestArticleList(0);
	}
	

	// 페이지 Up, Down
	private boolean upDownSwipe(int movingCheckIndex) {
		if (movingCheckIndex > currentFlipperChildSize
				|| movingCheckIndex < ARTICLE_END_ITEM_INDEX) {
			return false;
		}
		display(movingCheckIndex);
		return true;
	}
	
	private void display(int checkWhichChild) {
		currentChildIndex = checkWhichChild; 
		startFlipper.setDisplayedChild(currentChildIndex);
	}
	
	private void setAnimation(int in, int out) {
		Animation inAnimation = AnimationUtils.loadAnimation(this, in);
		Animation outAnimation = AnimationUtils.loadAnimation(this, out);
		startFlipper.setInAnimation(inAnimation);
		startFlipper.setOutAnimation(outAnimation);
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
		
		if (v != startFlipper) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			yAtDown = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			yAtUp = event.getY(); 
            
			if (yAtDown - yAtUp > SWIPE_MIN_DISTANCE) {
				setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
				return upDownSwipe(currentChildIndex - 1);
			// down
			} else if (yAtUp - yAtDown > SWIPE_MIN_DISTANCE) {
				setAnimation(R.anim.first_up_down_in, R.anim.second_up_down_out);
				return upDownSwipe(currentChildIndex + 1);
			}  
		}
		return true;
	}
	
	private class StartClickLitener implements OnClickListener{

		@Override
		public void onClick(View v) {
			StartActivity startActivity;
			Intent intent;
			RbPreference pref;
			
			startActivity = StartActivity.this;
			pref = new RbPreference(startActivity);
			pref.put(RbPreference.PREF_IS_INTRO, false);
			
//			 서비스(background 실행) 실행 용도
			intent = new Intent(startActivity, LockScreenService.class);
			startActivity.startService(intent);
			
			Network.getInstance().setDeviceId(((NewsUpApp)getApplication()).getDeviceId());
			Network.getInstance().requestRegistUser(getApplication());
			
			intent = new Intent(startActivity, MainActivity.class);
			startActivity(intent);
			startActivity.finish();
			
		}
	}
	
	private class NextClickLitener implements OnClickListener{

		@Override
		public void onClick(View v) {
			setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
			upDownSwipe(currentChildIndex - 1);
		}
	}
}




