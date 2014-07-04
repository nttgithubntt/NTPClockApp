
package com.grabtaxi.ntpclock.analog.clock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.grabtaxi.ntpclock.BuildConfig;

/**
 * @author Nhan.Nguyen
 * 
 */
public class NTTLog {

    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int VERBOSE = Log.VERBOSE;

    public static boolean DEBUG_ON = BuildConfig.DEBUG;
	private static String TAG = "NTPClockApp";
	private static String LABS_LOGFILE = "NTPClockApp.txt";
	private static String mPathFile;
	private static File mLogFile;
	private static int mCanWriteToFile = -1;
	private static Context mLastContext = null;

	/**
	 * Write log in mode. All will be turn off if DEBUG_ON is false.
	 * 
	 * @param level
     *            - VERBOSE - verbose log (Log.v)
	 *            - DEBUG - debug log (Log.d)
     *            - INFO - information log (Log.i)
     *            - WARN - warning log (Log.w)
     *            - ERROR - error log (Log.e)
	 * @param message
	 */
	public static void log(int level, String message) {
		if (DEBUG_ON) {
			switch (level) {
			case ERROR:
				Log.e(TAG, message);
				break;
			case WARN:
				Log.w(TAG, message);
				break;
			case INFO:
				Log.i(TAG, message);
				break;
			case DEBUG:
				Log.d(TAG, message);
				break;
			case VERBOSE:
				Log.v(TAG, message);
				break;
            default:
				Log.v(TAG, message);
				break;
			}
		}
	}

	/**
	 * @Description: Write log Android log mode and specified log file.
	 *               (GKIM_LOGFILE)
	 * @param context
	 * @param mode
	 * @param message
	 */
	public static void lf(Context context, int mode, String message) {
		log(mode, message);
		if (DEBUG_ON) {
			if (mLogFile == null) {
				if (mCanWriteToFile == -1) {
					String state = Environment.getExternalStorageState();
					if (state.equals(Environment.MEDIA_MOUNTED)) {
						mCanWriteToFile = 1;
						mPathFile = Environment.getExternalStorageDirectory()
								+ File.separator + TAG;
					} else {
						if (context != null) {
							mLastContext = context;
							mCanWriteToFile = 2;
							mPathFile = context.getCacheDir() + File.separator
									+ TAG;
						} else if (mLastContext != null) {
							mCanWriteToFile = 2;
							mPathFile = mLastContext.getCacheDir()
									+ File.separator + TAG;
						} else {
							mCanWriteToFile = 0;
						}
					}
					File dextrFolder = new File(mPathFile);
					if (!dextrFolder.exists()) {
						dextrFolder.mkdir();
					}
					mPathFile += File.separator + LABS_LOGFILE;
				}
			}

			if (mCanWriteToFile > 0) {
				Log.v(TAG, "Writing log into(" + mCanWriteToFile + "): "
						+ mPathFile);
				try {
					if (mLogFile == null) {
						mLogFile = new File(mPathFile);
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							mLogFile, true));
					if (bw != null) {
						String messageout = (String) DateFormat.format(
								((CharSequence) "MM/dd/yy hh:mm:ssaa"),
								System.currentTimeMillis())
								+ " " + message;
						messageout += "\r\n";
						bw.write(messageout);
						bw.flush();
					}
					bw.close();
				} catch (IOException e) {
                    log(3, "Logger has failed to write: " + message
                            + " into file with error: " + e.getMessage());
				}
			}
		}
	}

}
