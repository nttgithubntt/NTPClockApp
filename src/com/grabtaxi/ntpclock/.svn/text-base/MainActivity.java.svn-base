package com.grabtaxi.ntpclock;

import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grabtaxi.ntpclock.analog.clock.AnalogClock;
import com.grabtaxi.ntpclock.analog.clock.NTTLog;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String NTP_SERVER = "0.us.pool.ntp.org";
	private long INTERVAL_TIME_UPDATE = 10 * 60 * 1000L;
	private Button mButtonSync;
	private TextView mTextViewTime;
	private AnalogClock mAnalogClock;
	private NTPServerTask mServerTask;
	private CounterClass mCounterClass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NTTLog.lf(this, 3, TAG + "==> onCreate");
		setContentView(R.layout.alarm_clock);
		mButtonSync = (Button) findViewById(R.id.btn_sync);
		mTextViewTime = (TextView) findViewById(R.id.txt_time);
		mAnalogClock = (AnalogClock) findViewById(R.id.clock);
		mButtonSync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				excuteNTPServerTask();

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		NTTLog.lf(this, 3, TAG + "==> onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		NTTLog.lf(this, 3, TAG + "==> onResume");
		excuteNTPServerTask();
	}

	@Override
	protected void onPause() {
		super.onPause();
		NTTLog.lf(this, 3, TAG + "==> onPause");
		cancelNTPServerTask();
	}

	@Override
	protected void onStop() {
		super.onStop();
		NTTLog.lf(this, 3, TAG + "==> onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NTTLog.lf(this, 3, TAG + "==> onDestroy");
	}

	public Long getNTPServerTime() {
		SntpClient client = new SntpClient();
		if (client.requestTime(NTP_SERVER, 30000)) {
			long time = client.getNtpTime();
			long newTime = time;
			Log.d(TAG, newTime + "....newTime");
			return newTime;
		}
		return 0L;
	}

	private void excuteNTPServerTask() {
		if (isConnectionAvailable()) {
			if (mServerTask != null) {
				mServerTask.cancel(true);
				mServerTask = null;
			}
			mServerTask = new NTPServerTask();
			mServerTask.execute();
		} else {
			showToast(getResources().getString(R.string.no_connection));
		}

	}

	private void cancelNTPServerTask() {
		if (mServerTask != null) {
			mServerTask.cancel(true);
		}
		if (mCounterClass != null) {
			mCounterClass.cancel();
			mTextViewTime
					.setText(getResources().getString(R.string.time_reset));
		}
	}

	public boolean isConnectionAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	private class NTPServerTask extends AsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			return getNTPServerTime();
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (pd != null) {
				pd.dismiss();
			}
			if (result <= 0L) {
				showToast(getResources()
						.getString(R.string.request_time_failed));
			} else {
				updateLayout(result);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pd == null) {
				pd = new ProgressDialog(MainActivity.this);
				pd.setTitle("Downloading...");
				pd.setMessage("Please wait...");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
			}
			pd.show();
		}

		private ProgressDialog pd;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public class CounterClass extends CountDownTimer {

		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mTextViewTime
					.setText(getResources().getString(R.string.time_reset));
			// synchronize with ntp server
			excuteNTPServerTask();
		}

		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onTick(long millisUntilFinished) {

			long millis = millisUntilFinished;
			String hms = String.format(
					"%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
									.toHours(millis)),
					TimeUnit.MILLISECONDS.toSeconds(millis)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(millis)));
			System.out.println(hms);
			mTextViewTime.setText(hms);
		}
	}

	public void updateLayout(Long result) {
		if (mAnalogClock != null) {
			mAnalogClock.onTimeChanged(result);
		}
		if (mCounterClass == null) {
			mCounterClass = new CounterClass(INTERVAL_TIME_UPDATE, 1000);
		}
		if (mCounterClass != null) {
			mCounterClass.cancel();
			mCounterClass.start();
		}
		// String dateFromNtpServer = "";
		// Calendar calendar = Calendar.getInstance();
		// try {
		// calendar.setTimeInMillis(result);
		// calendar.getTime();
		// GMTtoEST gmttoest = new GMTtoEST();
		// dateFromNtpServer = gmttoest.ReturnMeEst(calendar.getTime());
		// dateFromNtpServer = dateFromNtpServer + "  EST";
		//
		// } catch (Exception e) {
		// dateFromNtpServer = "No Response from NTP";
		// }

	}

}