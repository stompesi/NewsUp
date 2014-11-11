package com.example.flipview.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.flipview.R;
import com.example.flipview.application.NewsUpApp;
import com.example.flipview.database.Keyword;
import com.example.flipview.service.LockScreenService;
import com.example.flipview.setting.RbPreference;
import com.orm.query.Condition;
import com.orm.query.Select;

public class SettingActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	// 좌표
	private float xAtDown, xAtUp;
	
	// 크기
	private static final int MAX_KEYWORD_SIZE = 5;
	public static final int SMALL_WORD = 10;
	public static final int MEDIUM_WORD = 15;
	public static final int LARGE_WORD = 20;
	
	// 관심키워드 등록 Text 입력창 
	private EditText edtWord;
	
	// 레이아웃 
	private LinearLayout textSizeLayout;
	
	private ArrayList<String> keywordList; 
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Context context;
	
	Toast toast;
	
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
				if(group.getId() == R.id.radioWordSize)
				{
					switch(checkedId)
					{
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
					Log.e("aaa","aaa");
					ArticleActivity articleDetailActivity = (ArticleActivity) ArticleActivity.getInstance();
					articleDetailActivity.changeTextSize();
				}
			}
		};
		
		// 리스너 설정 
		SettingOnCheckedChangeListener settingOnCheckedChangeListener = new SettingOnCheckedChangeListener();
		SettingOnClickListener settingOnClickListener = new SettingOnClickListener();
		RbPreference pref = new RbPreference(this);
		List<Keyword> keywordORMList = Keyword.listAll(Keyword.class);
		

		keywordList = new ArrayList<String>();
		
		for(Keyword keywordORM : keywordORMList) {
			keywordList.add(keywordORM.getKeyword());
		}
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keywordList);

		// keyword 리스트 설정 
		listView = (ListView)findViewById(R.id.listKeyWord);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		textSizeLayout =(LinearLayout)findViewById(R.id.textSizeLayout);
		
		// editText 설정 
		edtWord = ((EditText)findViewById(R.id.edtWord));
		edtWord.setOnClickListener(settingOnClickListener);
		edtWord.setInputType(EditorInfo.TYPE_NULL);

		// button 리스너 등록 
		((Button)findViewById(R.id.btnWordRegister)).setOnClickListener(settingOnClickListener);
		((Button)findViewById(R.id.btnWordDelete)).setOnClickListener(settingOnClickListener);
		
		// switch 리스너 등록 
		((Switch)findViewById(R.id.swLockScreen)).setChecked(pref.getValue(RbPreference.IS_LOCK_SCREEN, false));
		((Switch)findViewById(R.id.swLockScreen)).setOnCheckedChangeListener(settingOnCheckedChangeListener);
		((Switch)findViewById(R.id.swPushNotify)).setChecked(pref.getValue(RbPreference.NOTI_ALARM, false));
		((Switch)findViewById(R.id.swPushNotify)).setOnCheckedChangeListener(settingOnCheckedChangeListener);
		((Switch)findViewById(R.id.swWordSize)).setOnCheckedChangeListener(settingOnCheckedChangeListener);
		
		// radioGroup 리스너 등록
		int index = pref.getValue(RbPreference.WORD_SIZE, MEDIUM_WORD) / 5 - 2;
		((RadioGroup)findViewById(R.id.radioWordSize)).check(((RadioGroup)findViewById(R.id.radioWordSize)).getChildAt(index).getId());
		((RadioGroup)findViewById(R.id.radioWordSize)).setOnCheckedChangeListener(mRCheckedChangeListener);
		
		View view = (View) findViewById(R.id.listKeyWord);
		view.setOnTouchListener(this);
	}


	// Switch change 리스너 
	private class SettingOnCheckedChangeListener implements OnCheckedChangeListener
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			RbPreference pref = new RbPreference(context);
			
			if(isChecked) {
				switch(buttonView.getId())
				{
				case R.id.swLockScreen:
					Intent intent = new Intent(context, LockScreenService.class);
					startService(intent);
					pref.setValue(RbPreference.IS_LOCK_SCREEN, true);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.VISIBLE);
					break;
				case R.id.swPushNotify:
					pref.setValue(RbPreference.NOTI_ALARM, true);
					break;
				}
			} else {
				switch(buttonView.getId())
				{
				case R.id.swLockScreen:
					Intent intent = new Intent(context, LockScreenService.class);
					stopService(intent);
					pref.setValue(RbPreference.IS_LOCK_SCREEN, false);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.GONE);
					break;
				case R.id.swPushNotify:
					pref.setValue(RbPreference.NOTI_ALARM, false);
					break;
				}
			}
		}
	}

	// button click 리스너 
	private class SettingOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnWordRegister:
				String keyword = edtWord.getText().toString();
				if(keywordList.size() < MAX_KEYWORD_SIZE) {
					Keyword keywordORM = Select.from(Keyword.class).where(Condition.prop("keyword").eq(keyword)).first();
					if (keywordORM != null) {
						showToast("동일한 키워드는 등록하실 수 없습니다.");
						return ;
					}
					if(keyword.length() != 0) {
						keywordList.add(keyword);
						
						keywordORM = new Keyword(keyword);
						keywordORM.save();
						
						edtWord.setText("");
						showToast("\"" + keyword + "\" 관심키워드로 등록되었습니다.");
						adapter.notifyDataSetChanged();
					}
				} else if(keyword.length() != 0) {
					showToast("관심키워드는 최대 5개까지 등록 됩니다.");
				}
				break;
			case R.id.btnWordDelete:
				int positoin;
				
				positoin = listView.getCheckedItemPosition();
				
				if(positoin != ListView.INVALID_POSITION) {
					keyword = keywordList.get(positoin);
					keywordList.remove(positoin);
					Keyword keywordORM = Select.from(Keyword.class).where(Condition.prop("keyword").eq(keyword)).first();
					keywordORM.delete();
					
					showToast("\"" + keyword + "\" 관심키워드를 삭제하였습니다.");
					listView.clearChoices();
					adapter.notifyDataSetChanged();
				}
				break;
			case R.id.edtWord:
				 edtWord.setInputType(EditorInfo.TYPE_CLASS_TEXT);
				break;
			default:
				break;
			}
		}
	}
	
	private void showToast(String string) {
		if(toast == null) {
			toast = Toast.makeText(getApplicationContext(),
					string, Toast.LENGTH_SHORT);
		}else{
			toast.setText(string);
		}
		toast.show();
		
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
			} else if (xAtUp - xAtDown > SWIPE_MIN_DISTANCE) {
				finish();
			}
		}
		return false;
	}
}



