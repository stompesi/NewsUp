package setting;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/***
 * 
 * @author stompesi
 *
 * 안드로이드 App에 설정 내용을 저장하는 Class
 */

public class RbPreference {

	private final String PREF_NAME = "newsup";

	public final static String USER_ID = "USER_ID";
	public final static String WORD_SIZE = "WORD_SIZE";
	public final static String NOTI_ALARM = "NOTI_ALARM";
	public final static String PREF_IS_INTRO = "PREF_IS_INTRO";
	public final static String IS_LOCK_SCREEN ="IS_LOCK_SCREEN";
	public final static String PREF_IS_FIRST_NETWORK_REQUEST = "PREF_IS_FIRST_NETWORK_REQUEST";
	
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	public RbPreference(Context context) {
		pref= context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		editor = pref.edit();
	}

	public void setValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void setValue(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void setValue(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public String getValue(String key, String dftValue) {
		try {
			return pref.getString(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public int getValue(String key, int dftValue) {
		try {
			return pref.getInt(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}

	}

	public boolean getValue(String key, boolean dftValue) {
		try {
			return pref.getBoolean(key, dftValue);
		} catch (Exception e) {
			return dftValue;
		}
	}
}