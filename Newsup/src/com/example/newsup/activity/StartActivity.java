package com.example.newsup.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.example.newsup.R;
import com.example.newsup.application.NewsUpApp;
import com.example.newsup.background.service.ArticleManageService;
import com.example.newsup.background.service.LockScreenService;
import com.example.newsup.network.NewsUpNetwork;
import com.example.newsup.setting.RbPreference;


@SuppressLint("ClickableViewAccessibility")
public class StartActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	// 좌표
	private float xAtDown, xAtUp;
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		
		init();
		touchEventInit();
	}
	
	private void init() {
		Intent lockScreenIntent = new Intent(StartActivity.this, LockScreenService.class);
		Intent articleManageIntent = new Intent(StartActivity.this, ArticleManageService.class);
		startService(articleManageIntent);
		startService(lockScreenIntent);
		
		NewsUpNetwork.getInstance().requestArticleList(0);
		
		//처음 시작 할때 셋팅 값 저장.
		RbPreference pref = new RbPreference(this);
		pref.setValue(RbPreference.IS_LOCK_SCREEN, true);//락스크린 on
		pref.setValue(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);//글자 크기 기본 15로 지정.
	}
	
	private void touchEventInit() {
		findViewById(R.id.politicalSociety).setOnTouchListener(this);
		findViewById(R.id.economy).setOnTouchListener(this);
		findViewById(R.id.culture).setOnTouchListener(this);
		findViewById(R.id.sports).setOnTouchListener(this);
		findViewById(R.id.it).setOnTouchListener(this);
		findViewById(R.id.health).setOnTouchListener(this);
		findViewById(R.id.entertainment).setOnTouchListener(this);
		findViewById(R.id.world).setOnTouchListener(this);
		findViewById(R.id.sympathy).setOnTouchListener(this);
		findViewById(R.id.game).setOnTouchListener(this);
		findViewById(R.id.start).setOnTouchListener(this);
	}
	
	public void settingStart() {
		StartActivity startActivity;
		RbPreference pref;
		Intent intent;
		
		startActivity = StartActivity.this;
		pref = new RbPreference(startActivity);
		pref.setValue(RbPreference.PREF_IS_INTRO, false);
		
		NewsUpNetwork.getInstance().setDeviceId(((NewsUpApp)getApplication()).getDeviceId());
		NewsUpNetwork.getInstance().requestRegistUser(getApplication());
		
		intent = new Intent(startActivity, ArticleActivity.class);
		startActivity(intent);
		startActivity.finish();
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xAtDown = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			xAtUp = event.getX();
			if (Math.abs(xAtDown - xAtUp) > SWIPE_MIN_DISTANCE) {
			} else if (Math.abs(xAtUp - xAtDown) > SWIPE_MIN_DISTANCE) {
				return true;
			} else {
				int category = 0;
				switch(v.getId()) {
				
				case R.id.politicalSociety: category = 1; break;
				case R.id.economy: category = 2; break;
				case R.id.culture: category = 3; break;
				case R.id.sports: category = 4; break;
				case R.id.game: category = 5; break;
				case R.id.it: category = 6; break;
				case R.id.health: category = 7; break;
				case R.id.entertainment: category = 8; break;
				case R.id.world: category = 9; break;
				case R.id.sympathy: category = 10; break;
				case R.id.start:
					Intent i = new Intent(StartActivity.this,SettingActivity.class);
					startActivity(i);
					StartActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					return true;
				default:
					return false;
				}
//			 	Article List 로돌아가야하는데 카테고리를 변경해야한다.
				ArticleActivity articleListActivity = (ArticleActivity) ArticleActivity.getInstance();
				articleListActivity.changeCategory(category);
				finish();
			}
		}
		return true;
	}
	
	private void setImage(int imageId, int src){
		ImageView image = (ImageView) findViewById(imageId);
		image.setImageResource(src);
	}
}




