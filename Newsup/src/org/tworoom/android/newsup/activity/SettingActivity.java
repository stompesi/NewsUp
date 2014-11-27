package org.tworoom.android.newsup.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

import org.tworoom.android.newsup.R;

import org.tworoom.android.newsup.application.NewsUpApp;
import org.tworoom.android.newsup.background.service.LockScreenService;
import org.tworoom.android.newsup.setting.RbPreference;

public class SettingActivity extends Activity implements OnTouchListener,OnClickListener{

	Button btnSmallWorld, btnLargeWorld, btnMediumWorld,btnlockScreen;

	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int WORLD_SMALL = 0;
	private static final int WORLD_MEDIUM = 1;
	private static final int WORLD_LARGE = 2;
	// 좌표
	private float xAtDown, xAtUp;

	// 크기
	public static final int SMALL_WORD = 10;
	public static final int MEDIUM_WORD = 15;
	public static final int LARGE_WORD = 20;
	private int worldCount = 0; 
	private boolean isLockScreen = false;

	// private ListView listView;
	private Context context;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		init();
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	// 초기 설정
	private void init() {
		context = this;
		RbPreference pref = new RbPreference(this);
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.settingLayout);
		btnlockScreen = (Button) findViewById(R.id.swLockScreen);
		btnSmallWorld = (Button) findViewById(R.id.btnTextSizeSmall);
		btnMediumWorld= (Button) findViewById(R.id.btnTextSizeMedium);
		btnLargeWorld = (Button) findViewById(R.id.btnTextSizeLarge);


		linearLayout.setOnTouchListener(this);
		btnlockScreen.setOnClickListener(this);
		btnSmallWorld.setOnClickListener(this);
		btnMediumWorld.setOnClickListener(this);
		btnLargeWorld.setOnClickListener(this);


		if(pref.getValue(RbPreference.IS_LOCK_SCREEN, false))
		{
			btnlockScreen.setBackgroundResource(R.drawable.switch_on);
		}else
		{
			btnlockScreen.setBackgroundResource(R.drawable.switch_off);
		}

		int index = pref.getValue(RbPreference.WORD_SIZE, MEDIUM_WORD) / 5 - 2;
		Log.d("TAG", index+"");

		switch(index)
		{
		case WORLD_SMALL:
			btnSmallWorld.setBackgroundResource(R.drawable.btn_fs_36_on);
			break;
		case WORLD_MEDIUM:
			btnMediumWorld.setBackgroundResource(R.drawable.btn_fs_42_on);
			break;

		case WORLD_LARGE:
			btnLargeWorld.setBackgroundResource(R.drawable.btn_fs_48_on);
			break;

		default:
			break;

		}

	}

	@Override
	public void onClick(View v) {
		RbPreference pref = new RbPreference(this);

		switch(v.getId()){
		case R.id.btnTextSizeSmall:
			worldCount = WORLD_SMALL;
			pref.setValue(RbPreference.WORD_SIZE, SMALL_WORD);
			NewsUpApp.getInstance().setTextSize(SMALL_WORD);

			worldToggleButton(worldCount);
			break;

		case R.id.btnTextSizeMedium:
			worldCount = WORLD_MEDIUM;
			pref.setValue(RbPreference.WORD_SIZE, MEDIUM_WORD);
			NewsUpApp.getInstance().setTextSize(MEDIUM_WORD);

			worldToggleButton(worldCount);
			break;

		case R.id.btnTextSizeLarge:
			worldCount = WORLD_LARGE;
			pref.setValue(RbPreference.WORD_SIZE, LARGE_WORD);
			NewsUpApp.getInstance().setTextSize(LARGE_WORD);

			worldToggleButton(worldCount);
			break;

		case R.id.swLockScreen:

			isLockScreen = toggleButton(isLockScreen); 
			pref.setValue(RbPreference.IS_LOCK_SCREEN, isLockScreen);
			break;
		default:
			break;

		}
		ArticleActivity articleDetailActivity = (ArticleActivity) ArticleActivity
				.getInstance();
		articleDetailActivity.changeTextSize();


	}

	public void worldToggleButton(int worldCount){
		switch(worldCount){
		case WORLD_SMALL:
			btnSmallWorld.setBackgroundResource(R.drawable.btn_fs_36_on);
			btnMediumWorld.setBackgroundResource(R.drawable.btn_fs_42_off);
			btnLargeWorld.setBackgroundResource(R.drawable.btn_fs_48_off);

			break;

		case WORLD_MEDIUM:
			btnSmallWorld.setBackgroundResource(R.drawable.btn_fs_36_off);
			btnMediumWorld.setBackgroundResource(R.drawable.btn_fs_42_on);
			btnLargeWorld.setBackgroundResource(R.drawable.btn_fs_48_off);

			break;

		case WORLD_LARGE:
			btnSmallWorld.setBackgroundResource(R.drawable.btn_fs_36_off);
			btnMediumWorld.setBackgroundResource(R.drawable.btn_fs_42_off);
			btnLargeWorld.setBackgroundResource(R.drawable.btn_fs_48_on);

			break;
		default:
			break;

		}
	}


	public boolean toggleButton(boolean isLockScreen){
		if(isLockScreen){

			btnlockScreen.setBackgroundResource(R.drawable.switch_off);
			isLockScreen = false;
			Intent intent = new Intent(context, LockScreenService.class);
			stopService(intent);


		}else{

			btnlockScreen.setBackgroundResource(R.drawable.switch_on);
			isLockScreen = true;
			Intent intent = new Intent(context, LockScreenService.class);
			startService(intent);
		}
		return isLockScreen;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xAtDown = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			xAtUp = event.getX();
			if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
				finish();
				return false;
			}
		}
		return true;
	}

}


