package application;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import setting.RbPreference;
import image.handler.BitmapCache;
import network.Network;
import activity.MainActivity;
import activity.StartActivity;
import android.app.Application;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import database.ArticleDatabaseHandler;

public class NewsUpApp extends Application {
	public static final String TAG = NewsUpApp.class.getSimpleName();
	
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private ArticleDatabaseHandler articleDBManager;
	private static NewsUpApp mInstance;
	
	private String deviceId; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		articleDBManager = ArticleDatabaseHandler.getInstance(getApplicationContext());
		Network.getInstance().setDBHandler(articleDBManager);
		
		RbPreference pref = new RbPreference(this);
		deviceId = pref.getValue(RbPreference.USER_ID, null);
		// 앱 처음 실행 
		if(deviceId == null) {
			deviceId = encodeId();
			pref.put(RbPreference.USER_ID, deviceId);
		}
		
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
	
	public ArticleDatabaseHandler getDB() {
		return articleDBManager;
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

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapCache());
		}
		
		return this.mImageLoader;
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
