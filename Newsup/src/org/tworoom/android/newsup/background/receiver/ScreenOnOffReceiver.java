package org.tworoom.android.newsup.background.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.tworoom.android.newsup.background.service.ArticleManageService;

public class ScreenOnOffReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent articleManageIntent = new Intent(context, ArticleManageService.class);
			context.startService(articleManageIntent);
		}
		
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			ArticleManageService.screenOff();
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			ArticleManageService.screenOn();
		}
	}
}
