package network;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ArticleReadInfo.ArticleReadInfo;
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

public class Network {

	private final static String TAG_OBJECT_JSON = "JSON_OBJECT";
	private final static String SERVER_ADDRESS = "http://14.63.161.26:5000";

	private static Network singletone;

	private String deviceId;
	
	private Network() {}

	public static Network getInstance() {
		if (singletone == null) {
			singletone = new Network();
		}
		return singletone;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	// article list 요청 
	public void requestArticleList(final int category) {
		Log.i("NewsUp", "Request Article List");
		
		String requestURL = SERVER_ADDRESS + "/news/category/" + category;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.i("NewsUp", "Network : ArticleList success.");
						JSONArray articles;
						try {
							articles = response.getJSONArray("articles");
							for (int i = 0; i < articles.length(); i++) {
								// TODO : 카테고리 임시변경함 - 서버에서 제대로 데이터가 날라온다면 제거해야함 
								articles.getJSONObject(i).put("category", category);
								Article.saveArticle(articles.getJSONObject(i));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : ArticleList Request Fail.");
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
		Log.i("NewsUp", "User 등록");
		
		String requestURL = SERVER_ADDRESS + "/users";

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						int errorCode;
						try {
							errorCode = response.getInt("error_code");
							switch(errorCode) {
							case 1:
								Log.e("NewsUp", "Network : Add user fail.");
								((NewsUpApp) context.getApplicationContext()).refreshDeviceID();
								setDeviceId(((NewsUpApp) context.getApplicationContext()).getDeviceId());
								requestRegistUser(context);
								break;
							case 0:
								Log.i("NewsUp", "Network : Add user success.");
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : ArticleList Request Fail.");
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
	public void updateUserLog(ArticleReadInfo articleReadInfo) {

		String requestURL = SERVER_ADDRESS + "/users/log";

		JSONObject params = new JSONObject();
		try {
            params.put("article_id", articleReadInfo.getArticleId());  
            params.put("start_time", articleReadInfo.getStartTime());  
            params.put("page", articleReadInfo.getPagesReadTime());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		Log.d("NewsUp", "User Log 서버로 전달");
		Log.d("NewsUp", "article_id : " + articleReadInfo.getArticleId());  
		Log.d("NewsUp", "start_time : " + articleReadInfo.getStartTime());  
		Log.d("NewsUp", "page : " + articleReadInfo.getPagesReadTime());
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, requestURL, params, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						int errorCode;
						try {
							errorCode = response.getInt("error_code");
							switch(errorCode) {
							case 1:
								Log.d("NewsUp", "Network : updateUserLog fail.");
								break;
							case 0:
								Log.d("NewsUp", "Network : updateUserLog success.");
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("NewsUp", "Network : ArticleList Request Fail.");
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
