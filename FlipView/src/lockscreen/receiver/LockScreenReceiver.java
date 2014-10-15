package lockscreen.receiver;

import activity.LockScreenActivity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/***
 *  - BroadcastReceiver
 * 화면이 꺼졌을때 : ACTION_SCREEN_OFF
 * 화면이 켜졌을때 : ACTION_SCREEN_ON Intent가 broadcast 된다. 
 * 이런 정보를 받을때 사용되는 Receiver 
 * ACTION_SCREEN_OFF 를 받으면 위에서 만든 LockScreenActivity를 띄운다.
 * */
@SuppressWarnings("deprecation")
public class LockScreenReceiver extends BroadcastReceiver {
	// 기본 잠금화면 관련 
	private KeyguardManager keygardManager = null;
	private KeyguardManager.KeyguardLock keyLock = null;
	public boolean isOnreceive = false;
	
	// 통화상태 확인 
	private TelephonyManager telephonManager = null;
	private boolean isPhonIdle = true;
	private PhoneStateListener phoneListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state) {
			case TelephonyManager.CALL_STATE_IDLE :
				isPhonIdle = true;
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK :
			case TelephonyManager.CALL_STATE_RINGING : 
				isPhonIdle = false;
				break;
			}
		}
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			
			if(keygardManager == null) {
				keygardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			}
			
			if(keyLock == null) {
				keyLock = keygardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
				isOnreceive = true;
			}
			
			if(telephonManager == null) {
				telephonManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				telephonManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
			}
			
			if(isPhonIdle) {
				Intent i = new Intent(context, LockScreenActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
	}
	
	// 기본 잠금화면 없애기 
	public void reenableKeyguard() {
		keyLock.reenableKeyguard();
	}
	
	// 기본 잠금화면 나타내기 
	public void disableKeyguard() {
		keyLock.disableKeyguard();
	}
	
	
}
