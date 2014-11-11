package com.example.newsup.background.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.newsup.R;
import com.example.newsup.background.receiver.LockScreenReceiver;

public class LockScreenService extends Service {

	// LockScreen ON / Off 리시버 
	private LockScreenReceiver screenOnOffReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		registerScreenOnOffReceiver();
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
		if (screenOnOffReceiver != null) {
			if (screenOnOffReceiver.isOnreceive) {
				screenOnOffReceiver.reenableKeyguard();
			}
			unregisterReceiver(screenOnOffReceiver);
		}
	}
	
	// 스크린 on / off 리시버 등록 
	private void registerScreenOnOffReceiver() {
		screenOnOffReceiver = new LockScreenReceiver();
		IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenOnOffReceiver, screenOff);
		registerReceiver(screenOnOffReceiver, screenOn);
	}
}
