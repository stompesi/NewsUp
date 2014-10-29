package service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import network.Network;
import receiver.ScreenOnOffReceiver;
import transmission.TransmissionArticle;
import activity.ArticleActivity;
import activity.LockScreenActivity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import database.Article;

public class ArticleManageService extends Service {

	private final static int TIME_HOURE = 3600000;
	private final static int TIME_TWO_HOURE = 7200000;
	private final static int TIME_NOW = 0;
	
	private ScreenOnOffReceiver mReceiver;
	
	// Category 시작, 끝 Index 설정 
	private static final int CATEGORY_MAX_INDEX = 10;
	private static final int CATEGORY_START_INDEX = 0;

	// 일정 시간 후에 작업할 Task 설정
	private static Timer articleRequestManageTimer;
	private static Timer sleepCheckTimer;
	
	private static boolean isStopArticleManageTimer;
	
	public static void screenOff() {
		sleepCheckTimer = new Timer();
		sleepCheckTimer.schedule(new CheckSleepTask(), TIME_TWO_HOURE);
	}
	
	public static void screenOn() {
		if(isStopArticleManageTimer){
			reStartRequestArticleManage();
		}
	}

	@Override
	public void onCreate() {
		Log.e("NewsUp", "Oncreate ArticleManage service");
		super.onCreate();
		registerScreenOnOffReceiver();
		startRequestArticleManage();
	}

	// 48시간 이상 지난 Article 제거  
	private static void removeArticleTimeOverItem() {
		Article.removeyArticle();
	}
	
	// 서버에 Article 요청 
	private static void requestArticles() {
		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			Network.getInstance().requestArticleList(i);
		}
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
	
	private static void reStartRequestArticleManage() {
		isStopArticleManageTimer = false;
		articleRequestManageTimer = new Timer();
		articleRequestManageTimer.schedule(new RequestArticleTask(), TIME_NOW, TIME_HOURE);
	}
	
	private static void startRequestArticleManage() {
		isStopArticleManageTimer = false;
		articleRequestManageTimer = new Timer();
		articleRequestManageTimer.schedule(new RequestArticleTask(), TIME_HOURE, TIME_HOURE);
	}
	
	private static void stopRequestArticleManage() {
		isStopArticleManageTimer = true;
		articleRequestManageTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
		articleRequestManageTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
		articleRequestManageTimer = null;
	}
	
	private static class RequestArticleTask extends TimerTask {

		@Override
		public void run() {
			Log.e("NewsUp", "기사 요청");
			requestArticles();
		}
	}
	
	private static class CheckSleepTask extends TimerTask {

		@Override
		public void run() {
			if(!isStopArticleManageTimer) {
				Log.e("NewsUp", "슬립모드");
				// DB아티클 제거
				removeArticleTimeOverItem();
				
				
				
				
				stopRequestArticleManage();
				sleepCheckTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
				sleepCheckTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
				sleepCheckTimer = null;
			}
		}
	}
		
	private void registerScreenOnOffReceiver() {
		mReceiver = new ScreenOnOffReceiver();
		IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, screenOff);
		registerReceiver(mReceiver, screenOn);
	}
}

