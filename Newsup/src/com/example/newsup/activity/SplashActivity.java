package com.example.newsup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.newsup.R;
import com.example.newsup.application.NewsUpApp;
import com.example.newsup.network.NewsUpNetwork;
import com.example.newsup.setting.RbPreference;
import com.urqa.clientinterface.URQAController;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		URQAController.InitializeAndStartSession(getApplicationContext(), "184637B8");
		
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {
			@Override
			public void run() {
				RbPreference pref = new RbPreference(SplashActivity.this);
//				 앱 처음 실행 
				if(pref.getValue(RbPreference.PREF_IS_INTRO, true)) {
					Intent intent = new Intent(SplashActivity.this, StartActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(SplashActivity.this, ArticleActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, 1500);
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}