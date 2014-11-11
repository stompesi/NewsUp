package com.example.newsup.background.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.newsup.background.service.ArticleManageService;

public class ScreenOnOffReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			ArticleManageService.screenOff();
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			ArticleManageService.screenOn();
		}
	}
}
