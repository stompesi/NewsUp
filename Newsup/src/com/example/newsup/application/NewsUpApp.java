package com.example.newsup.application;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.newsup.R;
import com.example.newsup.activity.SettingActivity;
import com.example.newsup.network.NewsUpNetwork;
import com.example.newsup.setting.RbPreference;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class NewsUpApp extends com.orm.SugarApp {
	public static final String TAG = NewsUpApp.class.getSimpleName();
	
	private RequestQueue mRequestQueue;
	private static NewsUpApp mInstance;
	
	private String deviceId; 
	private int textSize;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		
		RbPreference pref = new RbPreference(this);
		deviceId = pref.getValue(RbPreference.USER_ID, null);
		
		// 앱 처음 실행 
		if(deviceId == null) {
			deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
			pref.setValue(RbPreference.USER_ID, deviceId);
		}
		
		NewsUpNetwork.getInstance().setDeviceId(deviceId);
		
		int num = pref.getValue(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);
		switch(num) {
		case SettingActivity.SMALL_WORD:
			textSize = R.dimen.text_small;
			break;
		case SettingActivity.MEDIUM_WORD:
			textSize = R.dimen.text_medium;
			break;
		case SettingActivity.LARGE_WORD:
			textSize = R.dimen.text_large;
			break;
		}
		
		// UNIVERSAL IMAGE LOADER SETUP
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
		// END - UNIVERSAL IMAGE LOADER SETUP
		
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public int getTextSize() {
		return textSize;
	}
	
	public void setTextSize(int num) {
		switch(num) {
		case SettingActivity.SMALL_WORD:
			textSize = R.dimen.text_small;
			break;
		case SettingActivity.MEDIUM_WORD:
			textSize = R.dimen.text_medium;
			break;
		case SettingActivity.LARGE_WORD:
			textSize = R.dimen.text_large;
			break;
		}
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public static synchronized NewsUpApp getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
}
