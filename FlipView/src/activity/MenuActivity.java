package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.flipview.R;

public class MenuActivity extends Activity  {
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
		// 리스너 설정 
		MenuClickListener menuClickListener = new MenuClickListener();
		findViewById(R.id.main).setOnClickListener(menuClickListener);
		findViewById(R.id.politicalSociety).setOnClickListener(menuClickListener);
		findViewById(R.id.economy).setOnClickListener(menuClickListener);
		findViewById(R.id.culture).setOnClickListener(menuClickListener);
		findViewById(R.id.sports).setOnClickListener(menuClickListener);
		findViewById(R.id.it).setOnClickListener(menuClickListener);
		findViewById(R.id.health).setOnClickListener(menuClickListener);
		findViewById(R.id.entertainment).setOnClickListener(menuClickListener);
		findViewById(R.id.world).setOnClickListener(menuClickListener);
		findViewById(R.id.sympathy).setOnClickListener(menuClickListener);
		findViewById(R.id.game).setOnClickListener(menuClickListener);
		findViewById(R.id.setting).setOnClickListener(menuClickListener);
	}

	private class MenuClickListener implements OnClickListener {

		@Override
		synchronized public void onClick(View v) {
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
				return; 
			}
			
//			 Article List 로돌아가야하는데 카테고리를 변경해야한다.
			ArticleActivity articleListActivity = (ArticleActivity) ArticleActivity.getInstance();
			articleListActivity.changeCategory(category);
			finish();
		}
	}
}



