package org.tworoom.android.newsup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.tworoom.android.newsup.R;


public class MenuActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	// 좌표
	private float xAtDown, xAtUp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		init();
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	// 초기 설정 
	private void init() {
		
		findViewById(R.id.main).setOnTouchListener(this);
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
		findViewById(R.id.setting).setOnTouchListener(this);
		findViewById(R.id.menuLayout).setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xAtDown = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			xAtUp = event.getX();
			if (xAtDown - xAtUp > SWIPE_MIN_DISTANCE) {
				return false;
			} else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
				finish();
				return true;
			} else {
				int category = 0;
				switch(v.getId()) {
				case R.id.main:  category = 0; break;
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
				case R.id.setting:
					Intent i = new Intent(MenuActivity.this,SettingActivity.class);
					startActivity(i);
					MenuActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
}



