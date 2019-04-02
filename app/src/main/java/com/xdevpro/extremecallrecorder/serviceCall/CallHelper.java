package com.xdevpro.extremecallrecorder.serviceCall;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 *
 */
public class CallHelper {

	private Context mContext;
	private TelephonyManager tm;
	private CallStateListener callStateListener;

	private OutgoingReceiver outgoingReceiver;
	private Intent intent_audio;


	public CallHelper(Context ctx) {
		this.mContext = ctx;

		callStateListener = new CallStateListener();
		outgoingReceiver = new OutgoingReceiver();

		intent_audio = new Intent(ctx, ServiceVoice.class);
	}

	/**
	 * Listener to detect incoming calls. 
	 */
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// called when someone is ringing to this phone

				intent_audio.putExtra("PHONE_NUMBER", incomingNumber);
				if(!isMyServiceRunning(ServiceVoice.class)){
					mContext.startService(intent_audio);
			    }
				Toast.makeText(mContext, "New call : "+incomingNumber, Toast.LENGTH_LONG).show();

				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if(intent_audio != null){
					if(isMyServiceRunning(ServiceVoice.class)){
						mContext.stopService(intent_audio);
					}
				}
				Toast.makeText(mContext, "END CALL: "+incomingNumber, Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
	
	/**
	 * Broadcast receiver to detect the outgoing calls.
	 */
	public class OutgoingReceiver extends BroadcastReceiver {
	    public OutgoingReceiver() {
	    }

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

			intent_audio.putExtra("PHONE_NUMBER", number);
			if(!isMyServiceRunning(ServiceVoice.class)){
				mContext.startService(intent_audio);
			}
	        Toast.makeText(mContext, "Outgoing: "+number, Toast.LENGTH_LONG).show();
	    }

	}

	
	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		//IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		//mContext.registerReceiver(outgoingReceiver, intentFilter);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
		//mContext.unregisterReceiver(outgoingReceiver);
	}


	// Check if a service is running
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) mContext.getSystemService(mContext.getApplicationContext().ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
