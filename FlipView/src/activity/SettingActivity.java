package activity;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;

import setting.RbPreference;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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

public class SettingActivity extends Activity  {

	private Switch sw_lockScreen;
	private Switch sw_wordSize;
	private Switch push_notify;
	private Button btn_wordRegister;
	private Button btn_wordDelete;
	private LinearLayout layout_size,layout_list;
	private EditText edt_wordEnter;
	private RadioGroup radioGroup;
	private ArrayList<String> item_list; 
	private ArrayAdapter<String> Adapter;
	private ListView listView;
	private InputMethodManager inputManager;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();

	}
	private void init()
	{

		MyOnCheckedChangeListener myOnCheckedChangeListener = new MyOnCheckedChangeListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();

		context = this;
		item_list = new ArrayList<String>();
		Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,item_list);
		inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

		listView = (ListView)findViewById(R.id.item_list);
		listView.setAdapter(Adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


		layout_size =(LinearLayout)findViewById(R.id.layout_size);
		layout_list =(LinearLayout)findViewById(R.id.layout_list);
		sw_lockScreen  = (Switch)findViewById(R.id.sw_lockScreen);
		push_notify  = (Switch)findViewById(R.id.push_notify);
		sw_wordSize = (Switch)findViewById(R.id.sw_wordSize);

		btn_wordRegister = (Button)findViewById(R.id.btn_wordRegister);
		btn_wordDelete = (Button)findViewById(R.id.btn_wordDelete);
		edt_wordEnter = (EditText)findViewById(R.id.edt_wordEnter);
		radioGroup =(RadioGroup)findViewById(R.id.radio);

		edt_wordEnter.setOnClickListener(myOnClickListener);
		btn_wordRegister.setOnClickListener(myOnClickListener);
		btn_wordDelete.setOnClickListener(myOnClickListener);
		sw_lockScreen.setOnCheckedChangeListener(myOnCheckedChangeListener);
		push_notify.setOnCheckedChangeListener(myOnCheckedChangeListener);
		sw_wordSize.setOnCheckedChangeListener(myOnCheckedChangeListener);
		radioGroup.setOnCheckedChangeListener(mRCheckedChangeListener);

	

	}


	RadioGroup.OnCheckedChangeListener mRCheckedChangeListener = 
			new	RadioGroup.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(group.getId()==R.id.radio)
			{
				switch(checkedId)
				{
				case R.id.btn_text_size_small:
					Log.d("TAG", "작은");

					break;

				case R.id.btn_text_size_medium:
					Log.d("TAG","중간");
					break;

				case R.id.btn_text_size_large:
					Log.d("TAT","큰");
					break;

				default:
					break;


				}
			}
		}
	};

	private class MyOnCheckedChangeListener implements OnCheckedChangeListener
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			RbPreference pref = new RbPreference(context);
			switch(buttonView.getId())
			{
			case R.id.sw_lockScreen:
				if(isChecked)
				{
					pref.put(RbPreference.IS_LOCK_SCREEN, true);
					//락스크릭 on
				}
				else
				{
					pref.put(RbPreference.IS_LOCK_SCREEN, false);
					//락스크린 off
				}
				break;
			case R.id.sw_wordSize:
				if(isChecked)
				{
					layout_size.setVisibility(View.VISIBLE);

				}
				else
				{
					layout_size.setVisibility(View.GONE);
				}
				break;
			case R.id.push_notify:
				if(isChecked)
				{
					pref.put(RbPreference.NOTI_ALARM, true);
					//푸시 알람 on
				}
				else
				{
					pref.put(RbPreference.NOTI_ALARM, false);
					//푸시 알람 off
				}
				break;
			}

		}

	}

	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_wordRegister:
				String txt = edt_wordEnter.getText().toString();
				if(item_list.size() < 5)
				{
					if(txt.length() != 0)
					{
						item_list.add(txt);
						edt_wordEnter.setText("");
						Adapter.notifyDataSetChanged();
						//inputManager.hideSoftInputFromWindow(edt_wordEnter.getWindowToken(), 0);
					}
				}

				break;
			case R.id.btn_wordDelete:
				int positoin;
				positoin = listView.getCheckedItemPosition();
				if(positoin != listView.INVALID_POSITION)
				{
					item_list.remove(positoin);
					listView.clearChoices();
					Adapter.notifyDataSetChanged();
				}

				break;
			case R.id.edt_wordEnter:
				edt_wordEnter.setText("");

				break;

			default:
				break;
			}

		}
	}

}



