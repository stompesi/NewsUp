package org.tworoom.android.newsup.background.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.tworoom.android.newsup.background.receiver.ScreenOnOffReceiver;
import org.tworoom.android.newsup.database.Article;
import org.tworoom.android.newsup.database.ArticleService;
import org.tworoom.android.newsup.network.NewsUpNetwork;
import org.tworoom.android.newsup.setting.RbPreference;

public class ArticleManageService extends Service {

	// 시간 상수 
//	private final static int TIME_HOURE = 25000;
//	private final static int TIME_TWO_HOURE = 50000;
//	private final static int ONE_DAY = 100000;
	
	
	private final static int TIME_HOURE = 3600000;
	private final static int TIME_TWO_HOURE = 7200000;
	private final static int ONE_DAY = 86400000;
			
	// 스크린 on / off 리시버 
	private ScreenOnOffReceiver mReceiver;
	
	// Category 시작, 끝 Index 설정 
	private static final int CATEGORY_MAX_INDEX = 10;
	private static final int CATEGORY_START_INDEX = 1;

	// 일정 시간 후에 작업할 Task 설정
	private static Timer articleMainRequestManageTimer;
	private static Timer articleAllCategoryRequestManageTimer;
	private static Timer sleepCheckTimer;
	
	// 뉴스기사 요청 기능 on / off 인지 확인하는 Flag
	private static boolean isStopArticleManageTimer;
	
	@Override
	public void onCreate() {
		Log.d("NewsUp", "Oncreate ArticleManage service");
		
		AsyncTask.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Log.d("NewsUp", "처음 기사 요청");
				RbPreference pref = new RbPreference(getApplicationContext());
				if(pref.getValue(RbPreference.FIRST_ARTICLE_REQUEST, true)
						&& NewsUpNetwork.isNetworkState(getApplicationContext())) {
					for (int i = 1 ; i <= 10 ; i++) {
						NewsUpNetwork.getInstance().requestArticleList(i, false);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					pref.setValue(RbPreference.FIRST_ARTICLE_REQUEST, false);
				}
			}
		});
		
		startRequestAllArticleManage();
		
		super.onCreate();
		registerScreenOnOffReceiver();
		startRequestArticleManage();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		stopRequestArticleManage();
		super.onDestroy();
	}
	
	
	// 스크린 꺼졌을 때 이벤트 
	public static void screenOff() {
		Log.d("NewsUp", "스크린 off 이벤트");
		sleepCheckTimer = new Timer();
		// 한번만 실행
		sleepCheckTimer.schedule(new CheckSleepTask(), TIME_TWO_HOURE);
	}

	// 스크린 켜졌을 때 이벤
	public static void screenOn() {
		Log.d("NewsUp", "스크린 on 이벤트");
		if(isStopArticleManageTimer){
			startRequestArticleManage();
		}
	}

	// 48시간 이상 지난 Article 제거  
	private static void removeArticleTimeOverItem() {
		ArticleService.getInstance().removeyArticle();
	}
	
	// 서버에 Article 요청 하루에 한번  
	private static void requestArticles() {
		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			NewsUpNetwork.getInstance().requestArticleList(i, false);
		}
	}
	
	// 서버에 Article 한시간에 1번씩 요청
	private static void requestMainArticle() {
		NewsUpNetwork.getInstance().requestArticleList(0, false);
	}
	
	// 서버에 Article score 요청  
	private static void refreshArticleScore() {
		NewsUpNetwork.getInstance().refreshArticleScore();
	}

	// 서버에 기사 요청하는 기능 실행 
	private static void startRequestArticleManage() {
		Log.d("NewsUp", "서버에 기사 요청하는 기능 실행 한시간에 한번씩");
		isStopArticleManageTimer = false;
		articleMainRequestManageTimer = new Timer();
		articleMainRequestManageTimer.schedule(new RequestMainArticleTask(), TIME_HOURE, TIME_HOURE);
	}
	
	private static void startRequestAllArticleManage() {
		Log.d("NewsUp", "서버에 카테고리별 기사 요청 기능 실행 하루에");
		articleAllCategoryRequestManageTimer = new Timer();
		articleAllCategoryRequestManageTimer.schedule(new RequestAllArticleTask(), ONE_DAY, ONE_DAY);
	}
	
	
	
	// 서버에 기사 요청하는 기능 중지 
	private static void stopRequestArticleManage() {
		Log.d("NewsUp", "서버에 기사 요청하는 기능 중지");
		isStopArticleManageTimer = true;
		articleMainRequestManageTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
		articleMainRequestManageTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
		articleMainRequestManageTimer = null;
	}
	
	
	// 뉴스기사 요청하는 Task class 
	private static class RequestMainArticleTask extends TimerTask {
		@Override
		public void run() {
			Log.d("NewsUp", "메인 뉴스기사 요청");
			requestMainArticle();
			refreshArticleScore();
		}
	}
	
	private static class RequestAllArticleTask extends TimerTask {
		@Override
		public void run() {
			Log.d("NewsUp", "모든 카테고리의 뉴스기사 요청");
			requestArticles();
		}
	}
	
	// 슬립모드 인지 아닌지 확인하는 Task class 
	private static class CheckSleepTask extends TimerTask {
		@Override
		public void run() {
			Log.d("NewsUp", "슬립모드 채크");
			if(!isStopArticleManageTimer) {
				Log.d("NewsUp", "자고있지 않았다면 뉴스기사 요청 중지");
				removeArticleTimeOverItem();
				stopRequestArticleManage();
				if(sleepCheckTimer != null) {
					sleepCheckTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
					sleepCheckTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
					sleepCheckTimer = null;
				}
			}
		}
	}
		
	// 스크린 on / off 리시버 등록 
	private void registerScreenOnOffReceiver() {
		mReceiver = new ScreenOnOffReceiver();
		IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, screenOff);
		registerReceiver(mReceiver, screenOn);
	}
}

