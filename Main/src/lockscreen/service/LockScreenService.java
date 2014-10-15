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


public class LockScreenService extends Service {
	
	private static final int CATEGORY_NUMBER = 10;
	private static final int CATEGORY_START_INDEX = 0;
	
	private LockScreenReceiver mReceiver;
	private Timer newsUpTimer;
	private TimerTask lockScreenTask;
	private TimerTask databaseTask;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		registerNewsUpReceiver();
		newsUpTimer = new Timer();
		RbPreference pref = new RbPreference(this);
		Notification notification = new NotificationCompat.Builder(getApplicationContext())
							.setContentTitle("NewsUp")
							.setContentText("첫화면에 뉴스 기스 혜택 제공 중")
							.setSmallIcon(R.drawable.ic_launcher)
							.build();
		lockScreenTask = new TimerTask() {
							@Override
							public void run() {
								// TODO : 어떤것을 지울지 결정 해야 한다.
								requestArticles(CATEGORY_START_INDEX);
							}
						};
		
		databaseTask = new TimerTask() {
			@Override
			public void run() {
				// TODO : 어떤것을 지울지 결정 해야 한다.
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				MainActivity mainActivity = (MainActivity) MainActivity.getInstance();
				if(!mainActivity.hasWindowFocus()) {
					removeArticles();
				}else if(!pm.isScreenOn()) {
					removeArticles();
					
					LockScreenActivity lockScreenActivity = (LockScreenActivity) LockScreenActivity.getInstance();
					lockScreenActivity.finish();
					
					Intent i = new Intent(getApplicationContext(), LockScreenActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(i);
				}
				
			}
		};
						
		// TODO : 이거 지우면 멘처음 무조껀 3번은 요청하게 된다. 
		if(pref.getValue(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, true)) {
			if(Network.isNetworkStat(getApplicationContext())){
				requestArticles(CATEGORY_START_INDEX + 1);
				requestArticles(CATEGORY_START_INDEX);
				requestArticles(CATEGORY_START_INDEX);
				pref.put(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, false);
			}
		}
		
		// TODO : schedule에대한 정보 습득, 시간초 바꿀것
//		newsUpTimer.schedule(databaseTask, 360000, 360000); // 6분마다 article 하나씩 제거
		newsUpTimer.schedule(databaseTask, 10000, 10000); // 6분마다 article 하나씩 제거
		newsUpTimer.schedule(lockScreenTask, 3600000, 3600000); // 한시간마다 article 하나씩 제거
		startForeground(1, notification);
	}

	private void removeArticles() {
		NewsUpApp app = (NewsUpApp) getApplicationContext();
		
		for(int i = CATEGORY_START_INDEX ; i <= CATEGORY_NUMBER ; i++) {
			int dbCount = app.getDB().selectArticleCount(i);
			if(dbCount > 30) {
				app.getDB().remveOldArticle(i);
			}
		}
	}
	
	private void requestArticles(int startIndex) {
		// TODO : break를 빼야한다. 
		for(int i = startIndex ; i <= CATEGORY_NUMBER ; i++) {
			Network.getInstance().requestArticleList(i);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		RbPreference pref = new RbPreference(this);
		if(pref.getValue(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, true)) {
			if(Network.isNetworkStat(getApplicationContext())){
				requestArticles(CATEGORY_START_INDEX + 1);
				requestArticles(CATEGORY_START_INDEX);
				requestArticles(CATEGORY_START_INDEX);
				pref.put(RbPreference.PREF_IS_FIRST_NETWORK_REQUEST, false);
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null) {
			if (mReceiver.isOnreceive) {
				mReceiver.reenableKeyguard();
			}
			unregisterReceiver(mReceiver);
		}

	}

	private void registerNewsUpReceiver() {
		mReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}
}
