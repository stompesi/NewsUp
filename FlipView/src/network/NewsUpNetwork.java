package network;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ArticleReadInfo.ArticleReadInfo;
import activity.ArticleActivity;
import activity.LockScreenActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import application.NewsUpApp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import database.Article;

public class NewsUpNetwork {

	private final static String TAG_OBJECT_JSON = "JSON_OBJECT";
	
	// 서버 요청 주소 
	private final static String ARTICLE_REQUEST_SERVER_ADDRESS = "http://14.63.173.158:5000";
	private final static String LOG_SEND_SERVER_ADDRESS = "http://14.63.161.26:5000";

	private static NewsUpNetwork instance;

	private String deviceId;
	
	private NewsUpNetwork() {}

	public static NewsUpNetwork getInstance() {
		if (instance == null) {
			instance = new NewsUpNetwork();
		}
		return instance;
	}
	
	// 디바이스 Id 저장 
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	// article list 요청 
	public void requestArticleList(final int category) {
		Log.d("NewsUp", "뉴스기사 요청");
		String requestURL = ARTICLE_REQUEST_SERVER_ADDRESS + "/news/category/" + category;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "Network : 뉴스기사 요청 성공.");
						JSONArray articles;
						try {
							articles = response.getJSONArray("articles");
							for (int i = 0; i < articles.length(); i++) {
								// TODO : 카테고리 임시변경함 - 서버에서 제대로 데이터가 날라온다면 제거해야함 
								articles.getJSONObject(i).put("category", category);
								Article.saveArticle(articles.getJSONObject(i));
							}
							if(ArticleActivity.getInstance() != null) {
								ArticleActivity.getInstance().successSaveArticle();
							} 
							if(LockScreenActivity.getInstance() != null) {
								LockScreenActivity.getInstance().successSaveArticle();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : 뉴스기사 요청 실패.");
						if(ArticleActivity.getInstance() != null) {
							ArticleActivity.getInstance().successSaveArticle();
						} 
						if(LockScreenActivity.getInstance() != null) {
							LockScreenActivity.getInstance().successSaveArticle();
						}
					}
				}) {
			 @Override
		       public Map<String, String> getHeaders() throws AuthFailureError {
		           HashMap<String, String> headers = new HashMap<String, String>();
		           headers.put("user_token", deviceId);
		           return headers;
		       }
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	// 사용자 device 등록
	public void requestRegistUser(final Context context) {
		Log.d("NewsUp", "User 등록");
		String requestURL = ARTICLE_REQUEST_SERVER_ADDRESS + "/users";
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, requestURL, null, 
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "Network : User 등록 성공.");
					}
				}, 
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : User 등록 실패.");
					}
				}) {
			 @Override
		     public Map<String, String> getHeaders() throws AuthFailureError {
	           HashMap<String, String> headers = new HashMap<String, String>();
	           headers.put("user_token", deviceId);
	           return headers;
		     }
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	// User Log 서버로 전달 
	public void updateUserLog(final ArticleReadInfo articleReadInfo) {
		Log.d("NewsUp", "User Log 서버로 전달");
		String requestURL = LOG_SEND_SERVER_ADDRESS + "/users/log";

		JSONObject params = new JSONObject();
		try {
            params.put("article_id", articleReadInfo.getArticleId());  
            params.put("start_time", articleReadInfo.getStartTime());  
            params.put("page", articleReadInfo.getPagesReadTime());
            //TODO : 좋아요 실어요 넣기 
            params.put("like", 0);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		Log.e("updateUserLog", ""+ articleReadInfo.getArticleId());  
		Log.e("updateUserLog", ""+ articleReadInfo.getStartTime());
		Log.e("updateUserLog", ""+ (articleReadInfo.getPagesReadTime()));
		Log.e("updateUserLog", ""+ (articleReadInfo.getPagesReadTime()).size());
        //TODO : 좋아요 실어요 넣기 
		Log.e("updateUserLog", ""+ 0);
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, requestURL, params, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						int errorCode;
						try {
							errorCode = response.getInt("error_code");
							switch(errorCode) {
							case 1:
								Log.e("NewsUp", "Network : updateUserLog 실패.");
								updateUserLog(articleReadInfo);
								break;
							case 0:
								Log.d("NewsUp", "Network : updateUserLog 성공.");
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : updateUserLog 실패.");
					}
				}) {
			 		@Override
			 		public Map<String, String> getHeaders() throws AuthFailureError {
			           HashMap<String, String> headers = new HashMap<String, String>();
			           headers.put("user_token", deviceId);
			           return headers;
			       }
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	// Network 상태 확인 함수 
	public static boolean isNetworkState(Context context) {
		Log.d("NewsUp", "네트워크 상태 확인");
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        if(lte_4g != null)                             
            blte_4g = lte_4g.isConnected();
        if( mobile != null ) {
            if (mobile.isConnected() || wifi.isConnected() || blte_4g)
                  return true;
        } else {
            if ( wifi.isConnected() || blte_4g )
                  return true;
        }
        return false; 
	}
}
