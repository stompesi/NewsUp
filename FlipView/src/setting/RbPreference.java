package setting;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/***
 * 
 * @author stompesi
 *
 * 안드로이드 App에 설정같은 내용을 저장하는 Class
 */

public class RbPreference {

	private final String PREF_NAME = "newsup";

	public final static String PREF_IS_INTRO = "PREF_IS_INTRO";
	public final static String USER_ID = "USER_ID";
	public final static String PREF_IS_FIRST_NETWORK_REQUEST = "PREF_IS_FIRST_NETWORK_REQUEST";
	
	private Context context;

	public RbPreference(Context context) {
		this.context = context;
	}

	public void put(String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putString(key, value);
		editor.commit();
	}

	public void put(String key, boolean value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(key, value);
		editor.commit();
	}

	public void put(String key, int value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putInt(key, value);
		editor.commit();
	}

	public String getValue(String key, String dftValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		try {
			return pref.getString(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public int getValue(String key, int dftValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		try {
			return pref.getInt(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public boolean getValue(String key, boolean dftValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		try {
			return pref.getBoolean(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}
	}
}