package service;

import java.util.Timer;
import java.util.TimerTask;

import network.Network;
import receiver.ScreenOnOffReceiver;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import database.Article;

public class ArticleManageService extends Service {

	private final static int TIME_HOURE = 3600000;
	private final static int TIME_FOUR_HOURE = 14400000;
	private final static int TIME_NOW = 0;
	
	private ScreenOnOffReceiver mReceiver;
	
	// Category 시작, 끝 Index 설정 
	private static final int CATEGORY_MAX_INDEX = 10;
	private static final int CATEGORY_START_INDEX = 0;

	// 일정 시간 후에 작업할 Task 설정
	private static Timer articleManageTimer;
	private static Timer sleepCheckTimer;
	
	private static boolean isStopArticleManageTimer;
	
	public static void screenOff() {
		sleepCheckTimer = new Timer();
		sleepCheckTimer.schedule(new CheckSleepTask(), TIME_HOURE);
	}
	
	public static void screenOn() {
		if(isStopArticleManageTimer){
			reStartArticleManage();
		}
	}

	@Override
	public void onCreate() {
		Log.e("NewsUp", "Oncreate ArticleManage service");
		super.onCreate();
		registerScreenOnOffReceiver();
		startArticleManage();
	}

	// 48시간 이상 지난 Article 제거  
	private static void removeArticleTimeOverItem() {
		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			Article.removeCategoryArticle(i);
		}
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
		stopArticleManage();
		super.onDestroy();
	}
	
	private static void reStartArticleManage() {
		isStopArticleManageTimer = false;
		articleManageTimer = new Timer();
		articleManageTimer.schedule(new RequestArticleTask(), TIME_NOW, TIME_HOURE);
		articleManageTimer.schedule(new RemoveTimeOverArticleTask(), TIME_NOW, TIME_FOUR_HOURE);
	}
	
	private static void startArticleManage() {
		isStopArticleManageTimer = false;
		articleManageTimer = new Timer();
		articleManageTimer.schedule(new RequestArticleTask(), TIME_HOURE, TIME_HOURE);
		articleManageTimer.schedule(new RemoveTimeOverArticleTask(), TIME_FOUR_HOURE, TIME_FOUR_HOURE);
	}
	
	private static void stopArticleManage() {
		isStopArticleManageTimer = true;
		articleManageTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
		articleManageTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
		articleManageTimer = null;
	}
	private static class RemoveTimeOverArticleTask extends TimerTask {

		@Override
		public void run() {
			Log.e("NewsUp", "기사 제거");
			removeArticleTimeOverItem();
		}
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
				stopArticleManage();
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

