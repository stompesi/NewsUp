package org.tworoom.android.newsup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.urqa.clientinterface.URQAController;

import org.tworoom.android.newsup.R;
import org.tworoom.android.newsup.setting.RbPreference;

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