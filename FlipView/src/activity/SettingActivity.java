package activity;

import java.util.ArrayList;
import java.util.List;

import lockscreen.service.LockScreenService;
import setting.RbPreference;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.example.flipview.R;
import com.orm.query.Condition;
import com.orm.query.Select;

import database.KeywordORM;

public class SettingActivity extends Activity  {
	// 크기
	private static final int MAX_KEYWORD_SIZE = 5;
	public static final int SMALL_WORD = 13;
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
	
	// 글자크기 라디오 버튼 클릭 이벤트 리스너 
	RadioGroup.OnCheckedChangeListener mRCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		RbPreference pref = new RbPreference(SettingActivity.this);
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(group.getId() == R.id.radioWordSize)
			{
				switch(checkedId)
				{
				case R.id.btnTextSizeSmall:
					pref.put(RbPreference.WORD_SIZE, SMALL_WORD);
					break;

				case R.id.btnTextSizeMedium:
					pref.put(RbPreference.WORD_SIZE, MEDIUM_WORD);
					break;

				case R.id.btnTextSizeLarge:
					pref.put(RbPreference.WORD_SIZE, LARGE_WORD);
					break;

				default:
					break;
				}
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	// 초기 설정 
	private void init() {
		// 리스너 설정 
		SettingOnCheckedChangeListener settingOnCheckedChangeListener = new SettingOnCheckedChangeListener();
		SettingOnClickListener settingOnClickListener = new SettingOnClickListener();
		RbPreference pref = new RbPreference(this);
		
		context = this;
		
		// TODO : 디비에서 가저온것 키워드 리스트에 넣어야 한다.
//		Realm realm = Realm.getInstance(context);
		keywordList = new ArrayList<String>();
		
		List<KeywordORM> keywordORMList = KeywordORM.listAll(KeywordORM.class);
		
		for(KeywordORM keywordORM : keywordORMList) {
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
		((RadioGroup)findViewById(R.id.radioWordSize)).setOnCheckedChangeListener(mRCheckedChangeListener);
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
					pref.put(RbPreference.IS_LOCK_SCREEN, true);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.VISIBLE);
					break;
				case R.id.swPushNotify:
					pref.put(RbPreference.NOTI_ALARM, true);
					break;
				}
			} else {
				switch(buttonView.getId())
				{
				case R.id.swLockScreen:
					Intent intent = new Intent(context, LockScreenService.class);
					stopService(intent);
					pref.put(RbPreference.IS_LOCK_SCREEN, false);
					break;
				case R.id.swWordSize:
					textSizeLayout.setVisibility(View.GONE);
					break;
				case R.id.swPushNotify:
					pref.put(RbPreference.NOTI_ALARM, false);
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
				if(keywordList.size() < MAX_KEYWORD_SIZE) {
					String keyword = edtWord.getText().toString();
					if(keyword.length() != 0) {
						keywordList.add(keyword);
//						Realm realm = Realm.getInstance(context);
//						KeywordRealmObject.insertKeyword(realm, keyword);
						
						KeywordORM keywordORM = new KeywordORM(keyword);
						keywordORM.save();
						
						edtWord.setText("");
						adapter.notifyDataSetChanged();
					}
				}
				break;
			case R.id.btnWordDelete:
				int positoin;
				
				positoin = listView.getCheckedItemPosition();
				
				if(positoin != listView.INVALID_POSITION) {
					String keyword = keywordList.get(positoin);
					keywordList.remove(positoin);

					KeywordORM keywordORM = Select.from(KeywordORM.class).where(Condition.prop("keyword").eq(keyword)).first();
					keywordORM.delete();
					
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
}



