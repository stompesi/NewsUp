package service;

import receiver.LockScreenReceiver;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.flipview.R;

public class LockScreenService extends Service {

	// LockScreen ON / Off 리시버 
	private LockScreenReceiver mReceiver;

	@Override
	public void onCreate() {
		Log.d("NewsUp", "Oncreate LockScreen service");
		super.onCreate();
		registerNewsUpReceiver();
		// TODO : 아이콘 넣어야 함
		Notification notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("NewsUp").setContentText("첫화면에 뉴스 기스 혜택 제공 중").setSmallIcon(R.drawable.ic_launcher).build();
		startForeground(1, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
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
		IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, screenOff);
		registerReceiver(mReceiver, screenOn);
	}
}
