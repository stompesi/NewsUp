package application;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import setting.RbPreference;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		
		RbPreference pref = new RbPreference(this);
		deviceId = pref.getValue(RbPreference.USER_ID, null);
		// 앱 처음 실행 
		if(deviceId == null) {
			deviceId = encodeId();
			pref.put(RbPreference.USER_ID, deviceId);
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
	
	private String encodeId(){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			String deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID) + System.currentTimeMillis();
			
			sh.update(deviceId.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	public void refreshDeviceID(){
		RbPreference pref = new RbPreference(this);
		deviceId = encodeId();
		pref.put(RbPreference.USER_ID, deviceId);
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
