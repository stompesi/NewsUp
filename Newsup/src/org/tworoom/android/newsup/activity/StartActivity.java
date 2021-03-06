package org.tworoom.android.newsup.activity;


import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.tworoom.android.newsup.R;

import org.tworoom.android.newsup.background.service.ArticleManageService;
import org.tworoom.android.newsup.background.service.LockScreenService;
import org.tworoom.android.newsup.network.NewsUpNetwork;
import org.tworoom.android.newsup.setting.RbPreference;


@SuppressLint("ClickableViewAccessibility")
public class StartActivity extends Activity implements OnTouchListener {
	
	private static final int SWIPE_MIN_DISTANCE = 100;
	// 좌표
	private float xAtDown, xAtUp;
	
	private Category[] categoryList;
	
	private ArrayList<Integer> likeCategoryList;
	
	private int likeCount;
	
	private int currentChildIndex;
	
	private ViewFlipper startFlipper;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		init();
		likeCount = 0;
		currentChildIndex = 0;
		display(0);
	}
	
	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	private void init() {
		
		Intent articleManageIntent = new Intent(StartActivity.this, ArticleManageService.class);
		startService(articleManageIntent);
		
		NewsUpNetwork.getInstance().requestRegistUser(getApplication());
		
		likeCategoryList = new ArrayList<Integer>();
		
		categoryList = new Category[10];
		
		categoryList[0] = new Category(R.id.politicalSociety, R.drawable.politics_on, R.drawable.politics);
		categoryList[1] = new Category(R.id.economy, R.drawable.economic_on, R.drawable.economic);
		categoryList[2] = new Category(R.id.culture, R.drawable.culture_on, R.drawable.culture);
		categoryList[3] = new Category(R.id.sports, R.drawable.sports_on, R.drawable.sports);
		categoryList[4] = new Category(R.id.game, R.drawable.game_on, R.drawable.game);
		categoryList[5] = new Category(R.id.it, R.drawable.it_on, R.drawable.it);
		categoryList[6] = new Category(R.id.health, R.drawable.health_on, R.drawable.health);
		categoryList[7] = new Category(R.id.entertainment, R.drawable.entertainment_on, R.drawable.entertainment);
		categoryList[8] = new Category(R.id.world, R.drawable.international_on, R.drawable.international);
		categoryList[9] = new Category(R.id.sympathy, R.drawable.sympathy_on, R.drawable.sympathy);
		
		startFlipper = (ViewFlipper) findViewById(R.id.flipper);
		
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
		
		findViewById(R.id.check1).setOnTouchListener(this);
		findViewById(R.id.check2).setOnTouchListener(this);
		findViewById(R.id.check3).setOnTouchListener(this);
		findViewById(R.id.check4).setOnTouchListener(this);
		findViewById(R.id.check5).setOnTouchListener(this);
		
		RbPreference pref = new RbPreference(this);
		pref.setValue(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);//글자 크기 기본 15로 지정.
		
	}
	
	// 페이지 Up, Down
		private boolean upDownSwipe(int movingCheckIndex) {
			display(movingCheckIndex);
			return true;
		}
		
		private void display(int checkWhichChild) {
			currentChildIndex = checkWhichChild; 
			startFlipper.setDisplayedChild(currentChildIndex);
		}
		
		private void setAnimation(int in, int out) {
			Animation inAnimation = AnimationUtils.loadAnimation(this, in);
			Animation outAnimation = AnimationUtils.loadAnimation(this, out);
			startFlipper.setInAnimation(inAnimation);
			startFlipper.setOutAnimation(outAnimation);
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
			} else {
				int category = 0;
				switch(v.getId()) {
				case R.id.check1:
				case R.id.check2:
				case R.id.check3:
				case R.id.check4:
				case R.id.check5:
					setAnimation(R.anim.second_up_down_in, R.anim.first_up_down_out);
					upDownSwipe(currentChildIndex - 1);
					return true;
					
				case R.id.politicalSociety: category = 0; break;
				case R.id.economy: category = 1; break;
				case R.id.culture: category = 2; break;
				case R.id.sports: category = 3; break;
				case R.id.game: category = 4; break;
				case R.id.it: category = 5; break;
				case R.id.health: category = 6; break;
				case R.id.entertainment: category = 7; break;
				case R.id.world: category = 8; break;
				case R.id.sympathy: category = 9; break;
				case R.id.start:
					
					Intent lockScreenIntent = new Intent(StartActivity.this, LockScreenService.class);
					startService(lockScreenIntent);
					
					for(int i = 0 ; i < 10 ; i++) {
						if(categoryList[i].getIsChecked()) {
							likeCategoryList.add(i + 1);
						}
					}
					
					//처음 시작 할때 셋팅 값 저장.
					RbPreference pref = new RbPreference(this);
					pref.setValue(RbPreference.IS_LOCK_SCREEN, true);//락스크린 on
					pref.setValue(RbPreference.PREF_IS_INTRO, false);
					NewsUpNetwork.getInstance().requestPreference(likeCategoryList);
					
					Intent i = new Intent(StartActivity.this, ArticleActivity.class);
					Bundle bun = new Bundle();
					if(likeCategoryList.size() == 0) {
						bun.putInt("startCategory", 1);
					} else {
						bun.putInt("startCategory", likeCategoryList.get(0).intValue());
					}
					
					i.putExtras(bun);
					startActivity(i);
					finish();
					return true;
				default:
					return false;
				}
				categoryList[category].switchImage();
			}
		}
		return true;
	}
	
	private void setImage(int imageId, int src) {
		Button image = (Button) findViewById(imageId);
		image.setBackgroundResource(src);
	}
	
	class Category {
		private int id;
		private int onImageId;
		private int offImageId;
		private boolean isChecked;
		
		public Category(int id, int onImageId, int offImageId) {
			this.id = id;
			this.isChecked = false;
			this.onImageId = onImageId;
			this.offImageId = offImageId;
		}
		
		
		public void switchImage() {
			if(isChecked){
				setImage(id, offImageId);
				isChecked = false;
				likeCount--;
			} else {
				setImage(id, onImageId);
				isChecked = true;
				likeCount++;
				Random random = new Random();
		        int ment = random.nextInt(5);
				switch(ment) {
				case 0:
					showMessage("어랏~ 이 카테고리 많이들 좋아하시더라고요~!");
					break;
				case 1:
					showMessage("좋아요! 추천을 기대해 보아요~");
					break;
				case 2:
					showMessage("아~ 이 카테고리를 좋아하시는군요!!");
					break;
				case 3:
					showMessage("좀 더 다양한 분야의 내용이 추천되요!!");
					break;
				case 5:
					showMessage("저도 이 카테고리 좋아하는데~");
					break;
				}
			}
		}
		
		private void showMessage(String message) {
			TextView responseText = (TextView) findViewById(R.id.responseText);
			responseText.setText(message);
		}
		
		public boolean getIsChecked() {
			return isChecked;
		}
	}
}




