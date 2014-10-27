package activity;


import network.Network;
import service.ArticleManageService;
import service.LockScreenService;
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
public class StartActivity extends Activity {
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		URQAController.InitializeAndStartSession(getApplicationContext(), "184637B8");
		
		RbPreference pref = new RbPreference(this);
//		 앱 처음 실행 
		if(pref.getValue(RbPreference.PREF_IS_INTRO, true)) {
			init();
			onClick();
		} else {
			Intent intent = new Intent(StartActivity.this, ArticleActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void init() {
		Intent lockScreenIntent = new Intent(StartActivity.this, LockScreenService.class);
		Intent articleManageIntent = new Intent(StartActivity.this, ArticleManageService.class);
		startService(articleManageIntent);
		startService(lockScreenIntent);
		
		
		Network.getInstance().requestArticleList(0);
		
		//처음 시작 할때 셋팅 값 저장.
		RbPreference pref = new RbPreference(this);
		pref.put(RbPreference.IS_LOCK_SCREEN, true);//락스크린 on
		pref.put(RbPreference.NOTI_ALARM, true);//락스크린 off
		pref.put(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);//글자 크기 기본 15로 지정.
	}
	
	public void onClick() {
		StartActivity startActivity;
		Intent intent;
		RbPreference pref;
		
		startActivity = StartActivity.this;
		pref = new RbPreference(startActivity);
		pref.put(RbPreference.PREF_IS_INTRO, false);
		
//		 서비스(background 실행) 실행 용도
		
		Network.getInstance().setDeviceId(((NewsUpApp)getApplication()).getDeviceId());
		Network.getInstance().requestRegistUser(getApplication());
		
		intent = new Intent(startActivity, ArticleActivity.class);
		startActivity(intent);
		startActivity.finish();
		
	}
}




