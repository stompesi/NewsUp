package com.example.newsup.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.newsup.R;
import com.example.newsup.application.NewsUpApp;
import com.example.newsup.background.service.LockScreenService;
import com.example.newsup.setting.RbPreference;

public class SettingActivity extends Activity implements OnTouchListener{

	private static final int SWIPE_MIN_DISTANCE = 100;
	// 좌표
	private float xAtDown, xAtUp;

	// 크기
	public static final int SMALL_WORD = 10;
	public static final int MEDIUM_WORD = 15;
	public static final int LARGE_WORD = 20;

	// 레이아웃
	private LinearLayout textSizeLayout;

	// private ListView listView;
	private Context context;

	// 글자크기 라디오 버튼 클릭 이벤트 리스너
	RadioGroup.OnCheckedChangeListener mRCheckedChangeListener;

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

		mRCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
			RbPreference pref = new RbPreference(SettingActivity.this);

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group.getId() == R.id.radioWordSize) {
					switch (checkedId) {
					case R.id.btnTextSizeSmall:
						pref.setValue(RbPreference.WORD_SIZE, SMALL_WORD);
						NewsUpApp.getInstance().setTextSize(SMALL_WORD);
						break;

					case R.id.btnTextSizeMedium:
						pref.setValue(RbPreference.WORD_SIZE, MEDIUM_WORD);
						NewsUpApp.getInstance().setTextSize(MEDIUM_WORD);
						break;

					case R.id.btnTextSizeLarge:
						pref.setValue(RbPreference.WORD_SIZE, LARGE_WORD);
						NewsUpApp.getInstance().setTextSize(LARGE_WORD);
						break;

					default:
						break;
					}
					Log.e("aaa", "aaa");
					ArticleActivity articleDetailActivity = (ArticleActivity) ArticleActivity
							.getInstance();
					articleDetailActivity.changeTextSize();
				}
			}
		};

		// 리스너 설정
		SettingOnCheckedChangeListener settingOnCheckedChangeListener = new SettingOnCheckedChangeListener();
		RbPreference pref = new RbPreference(this);

		textSizeLayout = (LinearLayout) findViewById(R.id.textSizeLayout);

		// switch 리스너 등록
		((Switch) findViewById(R.id.swLockScreen)).setChecked(pref.getValue(
				RbPreference.IS_LOCK_SCREEN, false));
		((Switch) findViewById(R.id.swLockScreen))
				.setOnCheckedChangeListener(settingOnCheckedChangeListener);
		((Switch) findViewById(R.id.swWordSize))
				.setOnCheckedChangeListener(settingOnCheckedChangeListener);

		// radioGroup 리스너 등록
		int index = pref.getValue(RbPreference.WORD_SIZE, MEDIUM_WORD) / 5 - 2;
		((RadioGroup) findViewById(R.id.radioWordSize))
				.check(((RadioGroup) findViewById(R.id.radioWordSize))
						.getChildAt(index).getId());
		((RadioGroup) findViewById(R.id.radioWordSize))
				.setOnCheckedChangeListener(mRCheckedChangeListener);

		LinearLayout view = (LinearLayout) findViewById(R.id.swipeArea);
		view.setOnTouchListener(this);
	}

	// Switch change 리스너
	private class SettingOnCheckedChangeListener implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			RbPreference pref = new RbPreference(context);

			if (isChecked) {
				switch (buttonView.getId()) {
				case R.id.swLockScreen:
					Intent intent = new Intent(context, LockScreenService.class);
					startService(intent);
					pref.setValue(RbPreference.IS_LOCK_SCREEN, true);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.VISIBLE);
					break;
				}
			} else {
				switch (buttonView.getId()) {
				case R.id.swLockScreen:
					Intent intent = new Intent(context, LockScreenService.class);
					stopService(intent);
					pref.setValue(RbPreference.IS_LOCK_SCREEN, false);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.GONE);
					break;
				}
			}
		}
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
