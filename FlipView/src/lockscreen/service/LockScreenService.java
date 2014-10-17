package lockscreen.service;

import java.util.Timer;
import java.util.TimerTask;

import lockscreen.receiver.LockScreenReceiver;
import network.Network;
import setting.RbPreference;
import activity.LockScreenActivity;
import activity.MainActivity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import application.NewsUpApp;

import com.example.flipview.R;

import database.ArticleDatabaseHandler;

public class LockScreenService extends Service {

	// Category 시작, 끝 Index 설정 
	private static final int CATEGORY_MAX_INDEX = 10;
	private static final int CATEGORY_START_INDEX = 0;
	private static final int CACHE_ARTICLE_NUMBER = 30;

	// 일정 시간 후에 작업할 Task 설정
	private Timer newsUpTimer;
	private TimerTask lockScreenTask;
	private TimerTask removeOverItemTask;
	private TimerTask removeAllOverItemTask;
	
	// LockScreen Off 리시버 
	private LockScreenReceiver mReceiver;

	@Override
	public void onCreate() {
		Log.d("NewsUp", "Oncreate LockScreen service");
		super.onCreate();
		setTask();
		articleFirstRequest();
		registerNewsUpReceiver();
		Notification notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("NewsUp").setContentText("첫화면에 뉴스 기스 혜택 제공 중").setSmallIcon(R.drawable.ic_launcher).build();

		// TODO : schedule에대한 정보 습득, 시간초 바꿀것

		startForeground(1, notification);
	}

	// 캐쉬해 있는 Article 갯수보다 숫자가 많으면 해당 카테고리 Item 한개 제거 
	private void removeArticleOverItem() {
		ArticleDatabaseHandler db = ((NewsUpApp) getApplicationContext()).getDB();

		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			int categoryArticleSize = db.selectArticleCount(i);
			if (categoryArticleSize > CACHE_ARTICLE_NUMBER) {
				db.remveOldArticle(i);
			}
		}
	}
	
	private void removeArticleAllOverItem() {
		ArticleDatabaseHandler db = ((NewsUpApp) getApplicationContext()).getDB();

		for (int i = CATEGORY_START_INDEX; i <= CATEGORY_MAX_INDEX; i++) {
			int categoryArticleSize = db.selectArticleCount(i);
			for(int j = categoryArticleSize ; j < CACHE_ARTICLE_NUMBER ; j++) {
				db.remveOldArticle(i);
			}
		}
	}
	
	// 서버에 Article 요청 
	private void requestArticles(int startIndex) {
		for (int i = startIndex; i <= CATEGORY_MAX_INDEX; i++) {
			Network.getInstance().requestArticleList(i);
		}
	}
	
	// 처음 캐쉬 Article 요청 30개
	private void articleFirstRequest() {
		RbPreference pref = new RbPreference(this);
		if (pref.getValue(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, true)) {
			if (Network.isNetworkState(getApplicationContext())) {
				requestArticles(CATEGORY_START_INDEX + 1);
				requestArticles(CATEGORY_START_INDEX);
				requestArticles(CATEGORY_START_INDEX);
				pref.put(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, false);
			}
		}
	}

	// Task 설정 (일정시간마다 작동하는 일)
	private void setTask() {
		newsUpTimer = new Timer();
		lockScreenTask = new TimerTask() {
			@Override
			public void run() {
				requestArticles(CATEGORY_START_INDEX);
			}
		};

		removeOverItemTask = new TimerTask() {
			@Override
			public void run() {
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				MainActivity mainActivity = (MainActivity) MainActivity.getInstance();
				LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
				
				if ((mainActivity != null  && mainActivity.hasWindowFocus())
					|| (lockScreenActivity != null && lockScreenActivity.hasWindowFocus())) {
					return ;
				} 
				removeArticleOverItem();
			}
		};
		
		removeAllOverItemTask = new TimerTask() {
			@Override
			public void run() {
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				MainActivity mainActivity = (MainActivity) MainActivity.getInstance();
				if (!pm.isScreenOn()) {
					removeArticleAllOverItem();

					LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
					lockScreenActivity.finish();

					Intent intent = new Intent(LockScreenService.this, LockScreenActivity.class);
					startActivity(intent);
				} else if (mainActivity != null && !mainActivity.hasWindowFocus()) {
					removeArticleOverItem();
				} 
			}
		};
		
		// 10초마다 over article 하나씩 제거
		newsUpTimer.schedule(removeOverItemTask, 1000, 1000); 
		// 한시간마다 Article 요청 
		newsUpTimer.schedule(lockScreenTask, 3600000, 3600000);
		// 하루마다 모든 over Article 제거 
		newsUpTimer.schedule(removeAllOverItemTask, 86400000, 86400000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		articleFirstRequest();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null) {
			if (mReceiver.isOnreceive) {
				mReceiver.reenableKeyguard();
			}
			unregisterReceiver(mReceiver);
			unRegisterTask();
		}
	}
	
	// Task 해제 
	private void unRegisterTask() {
		newsUpTimer.cancel(); // 해당 타이머가 수행할 모든 행위들을 정지
		newsUpTimer.purge(); // 대기중이던 취소된 행위가 있는 경우 모두 제거
		newsUpTimer = null;
	}

	// Receiver 해제 
	private void registerNewsUpReceiver() {
		mReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}
}
