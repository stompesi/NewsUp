package com.example.newsup.background.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.newsup.background.receiver.ScreenOnOffReceiver;
import com.example.newsup.database.Article;
import com.example.newsup.network.NewsUpNetwork;
import com.example.newsup.setting.RbPreference;

public class ArticleManageService extends Service {

	// 시간 상수 
	private final static int TIME_HOURE = 3600000;
	private final static int TIME_TWO_HOURE = 7200000;
	
	// 스크린 on / off 리시버 
	private ScreenOnOffReceiver mReceiver;
	
	// Category 시작, 끝 Index 설정 
	private static final int CATEGORY_MAX_INDEX = 10;
	private static final int CATEGORY_START_INDEX = 0;

	// 일정 시간 후에 작업할 Task 설정
	private static Timer articleRequestManageTimer;
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
		sleepCheckTimer = new Timer();
		sleepCheckTimer.schedule(new CheckSleepTask(), TIME_TWO_HOURE);
	}

	// 스크린 켜졌을 때 이벤
	public static void screenOn() {
		if(isStopArticleManageTimer){
			startRequestArticleManage();
		}
	}

	// 48시간 이상 지난 Article 제거  
	private static void removeArticleTimeOverItem() {
		Article.removeyArticle();
	}
	
	// 서버에 Article 요청 
	private static void requestArticles() {
		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			NewsUpNetwork.getInstance().requestArticleList(i, false);
		}
	}
	
	// 서버에 Article score 요청  
		private static void refreshArticleScore() {
			NewsUpNetwork.getInstance().refreshArticleScore();
		}

	// 서버에 기사 요청하는 기능 실행 
	private static void startRequestArticleManage() {
		isStopArticleManageTimer = false;
		articleRequestManageTimer = new Timer();
		articleRequestManageTimer.schedule(new RequestArticleTask(), TIME_HOURE, TIME_HOURE);
	}
	
	// 서버에 기사 요청하는 기능 중지 
	private static void stopRequestArticleManage() {
		isStopArticleManageTimer = true;
		articleRequestManageTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
		articleRequestManageTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
		articleRequestManageTimer = null;
	}
	
	
	// 뉴스기사 요청하는 Task class 
	private static class RequestArticleTask extends TimerTask {
		@Override
		public void run() {
			requestArticles();
			refreshArticleScore();
		}
	}
	
	// 슬립모드 인지 아닌지 확인하는 Task class 
	private static class CheckSleepTask extends TimerTask {
		@Override
		public void run() {
			if(!isStopArticleManageTimer) {
				removeArticleTimeOverItem();
				stopRequestArticleManage();
				sleepCheckTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
				sleepCheckTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
				sleepCheckTimer = null;
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

