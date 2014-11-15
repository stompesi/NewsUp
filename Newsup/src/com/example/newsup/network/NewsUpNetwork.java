package com.example.newsup.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.newsup.activity.ArticleActivity;
import com.example.newsup.activity.LockScreenActivity;
import com.example.newsup.application.NewsUpApp;
import com.example.newsup.data.ArticleReadInfo;
import com.example.newsup.database.Article;
import com.example.newsup.view.structure.ArticleDetailInfomation;

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
	
	
	public void refreshArticleScore() {
		Log.d("NewsUp", "뉴스기사 점수 요청");
		Log.d("NewsUp", "deviceId : " + deviceId);
		String requestURL = ARTICLE_REQUEST_SERVER_ADDRESS + "/news/category/";

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "Network : 뉴스기사 점수 요청 성공.");
						JSONArray articles;
						try {
							articles = response.getJSONArray("articles");
							for (int i = 0; i < articles.length(); i++) {
								Article.refreshArticleScore(articles.getJSONObject(i));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : 뉴스기사 점수 요청 실패.");
					}
				}) {
			 @Override
		       public Map<String, String> getHeaders() throws AuthFailureError {
		           HashMap<String, String> headers = new HashMap<String, String>();
		           headers.put("User-Token", deviceId);
		           return headers;
		       }
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}

	// article list 요청 
	public void requestArticleList(final int category) {
		Log.d("NewsUp", "뉴스기사 요청");
		Log.d("NewsUp", "deviceId : " + deviceId);
		String requestURL = ARTICLE_REQUEST_SERVER_ADDRESS + "/news/category/" + category;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "Network : 뉴스기사 요청 성공.");
						JSONArray articles;
						try {
							articles = response.getJSONArray("articles");
							for (int i = 0; i < articles.length(); i++) {
								Log.e("Id", "" + articles.getJSONObject(i).getDouble("score"));
								articles.getJSONObject(i).put("category", category);
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
		           headers.put("User-Token", deviceId);
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
	           headers.put("User-Token", deviceId);
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
			           headers.put("User-Token", deviceId);
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
	
	
	
	public void requestVideo(String query) {
		Log.d("NewsUp", "동영상 요청");
		String requestURL = "https://www.googleapis.com/youtube/v3/search?"
				+ "part=snippet&"
				+ "key=AIzaSyBUlYE_3MYaqnTFaegtKhSy0BzvkdQTfqY&"
				+ "type=video&"
				+ "order=date&"
				+ "q=" + query;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "동영상 요청 성공");
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	public void requestFacebook(String query, final ArticleDetailInfomation articleDetailInfomation) {
		Log.d("NewsUp", "페이스북 좋아요 요청");
		String requestURL = "http://api.facebook.com/method/links.getStats?format=json&urls=";
//			query = URLEncoder.encode("format=json&urls=" + query,"UTF-8");
			requestURL += query;
		
		Log.d("NewsUp", "requestURL : " + requestURL);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							Log.d("NewsUp", "Network : 페이스북 좋아요 요청 성공. count : " + response.getInt("like_count"));
							articleDetailInfomation.setTwitterCount(response.getInt("like_count"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("NewsUp", "Network : 페이스북 좋아요 요청 실패");
					}
				}) {
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	public void requestTwitter(String query, final ArticleDetailInfomation articleDetailInfomation) {
		Log.d("NewsUp", "트위터 트윗수 요청");
		String requestURL = "http://urls.api.twitter.com/1/urls/count.json?url=";
		
//			query = URLEncoder.encode(query,"UTF-8");
		requestURL += query;
		Log.d("NewsUp", "requestURL : " + requestURL);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							Log.d("NewsUp", "트위터 트윗수 요청 성공. count : " + response.getInt("count"));
							articleDetailInfomation.setTwitterCount(response.getInt("count"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("NewsUp", "Network : 트위터 트윗수 요청 실패");
					}
				}) {
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
	
	
	public void requestArticleDetail(final int articleId) {
		Log.d("NewsUp", "뉴스 상세정보 요청");
		Log.d("NewsUp", "deviceId : " + deviceId);
		String requestURL = ARTICLE_REQUEST_SERVER_ADDRESS + "/news/articles/" + articleId;

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("NewsUp", "Network : 뉴스 상서 정보 요청 성공.");
						try {
							JSONArray relatedArticles = response.getJSONArray("related_article");
							for (int i = 0; i < relatedArticles.length(); i++) {
//								relatedArticles.getJSONObject(i)
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("NewsUp", "Network : 뉴스상세정보 요청 실패.");
					}
				}) {
			 @Override
		       public Map<String, String> getHeaders() throws AuthFailureError {
		           HashMap<String, String> headers = new HashMap<String, String>();
		           headers.put("User-Token", deviceId);
		           return headers;
		       }
		};
		NewsUpApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG_OBJECT_JSON);
	}
	
}
