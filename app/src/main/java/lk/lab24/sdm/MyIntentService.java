package lk.lab24.sdm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import lk.lab24.sdm.Backend.Actions;
import lk.lab24.sdm.Backend.CustomDownlodManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {
	String TAG = "fuck";
	BroadcastReceiver broadcastReceiver;
	PowerManager.WakeLock wakeLock;
	boolean isReg = false;
	private Database db;
	private CustomDownlodManager cdm;
	private SQLiteDatabase readabelDb;


	public MyIntentService() {
		super("MyIntentService");
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate: ");
		this.db = new Database(getApplicationContext());
		this.cdm = new CustomDownlodManager(this.db, getApplicationContext());
		this.readabelDb = this.db.getReadableDatabase();
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Log.d(TAG, "onCreate: register intent service");
		Notification notification =
				new Notification.Builder(this)
						.setContentTitle("DOWMLOD SERVICE START")
						.setContentText("POWERD BY : LAB24")
						.setSmallIcon(R.drawable.ic_navigate_next_black_24dp)
						.setContentIntent(pendingIntent)
						.setTicker("TRICKER")
						.build();

		startForeground(2, notification);

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"MyApp::MyWakelockTag");
		wakeLock.acquire();
		super.onCreate();
	}


	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		this.broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "onReceive: IntentSevice");
				String action = intent.getAction();
				int id = intent.getIntExtra("ID", 0);
				switch (action) {
					case Actions.ACTION_addMission:
						Log.d(TAG, "onReceive: admision intent Service");
						if (isNetworkConnected()) {
							startOne(id);
						} else {
							Toast.makeText(getApplicationContext(), "PLEASE CONNECT TO THE INTERNET.", Toast.LENGTH_SHORT);
						}

						break;
					case Actions.ACTION_addExMission:
						Log.d(TAG, "onReceive: exMission");
						if (isNetworkConnected()) {
							startExcitsone(id);
						} else {
							Toast.makeText(getApplicationContext(), "PLEASE CONNECT TO THE INTERNET.", Toast.LENGTH_SHORT);
						}

						break;
					case Actions.ACTION_pause:
						Log.d(TAG, "onReceive: Action pause" + id);
						pause(id);
						break;
					case Actions.ACTION_resume:
						Log.d(TAG, "onReceive: action resu,e" + id);
						if (isNetworkConnected()) {
							resume(id);
						} else {
							Toast.makeText(getApplicationContext(), "PLEASE CONNECT TO THE INTERNET.", Toast.LENGTH_SHORT);
						}

						break;
					case Actions.ACTION_cancel:
						cancel(id);
						break;
					case Actions.ACTION_pauseAll:
						Log.d(TAG, "onReceive: PAsue All");
						pauseAll();
						break;
					case Actions.ACTION_startAll:
						Log.d(TAG, "onReceive: Start All");
						if (isNetworkConnected()) {
							startAll();
						} else {
							Toast.makeText(getApplicationContext(), "PLEASE CONNECT TO THE INTERNET.", Toast.LENGTH_SHORT);
						}

						break;
				}


			}
		};


		//intent Filter
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Actions.ACTION_addMission);
		intentFilter.addAction(Actions.ACTION_addExMission);
		intentFilter.addAction(Actions.ACTION_pauseAll);
		intentFilter.addAction(Actions.ACTION_startAll);
		intentFilter.addAction(Actions.ACTION_pause);
		intentFilter.addAction(Actions.ACTION_resume);
		intentFilter.addAction(Actions.ACTION_cancel);


		if (!isReg) {
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.broadcastReceiver, intentFilter);
			isReg = true;
		}

		while (true) {
			SystemClock.sleep(10000);
		}


	}

	//check Connection
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}


	private void startAll() {
		Cursor c = this.readabelDb.rawQuery("SELECT * FROM queue WHERE is_downloded = 0 ", null);
		Log.d(TAG, "startAll: ");
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("id"));
			String src = c.getString(c.getColumnIndex("src"));
			String path = c.getString(c.getColumnIndex("path"));
			String file_name = c.getString(c.getColumnIndex("file_name"));
			boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
			boolean is_get = c.getInt(c.getColumnIndex("is_get")) == 1;
			boolean is_error = c.getInt(c.getColumnIndex("is_error")) == 1;
			String file_type = c.getString(c.getColumnIndex("file_type"));
			Log.d(TAG, "startAll: While");
			try {
				if (is_pause) {
					cdm.resumeMission(id, src, "" + path + "/" + file_name + file_type);
				} else if (is_error) {
					db.removeErrorState(id);
					cdm.resumeMission(id, src, "" + path + "/" + file_name + file_type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startOne(int id) {
		Cursor c = this.readabelDb.rawQuery("SELECT * FROM queue WHERE id = ?", new String[]{Integer.toString(id)});
		c.moveToPosition(0);
		String src = c.getString(c.getColumnIndex("src"));
		String path = c.getString(c.getColumnIndex("path"));
		String file_name = c.getString(c.getColumnIndex("file_name"));
		boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
		boolean is_get = c.getInt(c.getColumnIndex("is_get")) == 1;
		if (!is_get) {
			Log.d(TAG, "startOne: Addmission");
			cdm.addMision(id, src, path, file_name);
		} else {
			if (is_pause) {
				Log.d(TAG, "onReceive: added ex item");
			}
		}
	}

	private void pauseAll() {
		Cursor c = this.readabelDb.rawQuery("SELECT * FROM queue WHERE is_downloded = 0 ", null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("id"));
			try {
				cdm.pauseMission(id);
				Log.d(TAG, "pauseAll: ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void pause(int id) {
		try {
			cdm.pauseMission(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startExcitsone(int id) {
		Cursor c = this.readabelDb.rawQuery("SELECT * FROM queue WHERE id = ?", new String[]{Integer.toString(id)});
		c.moveToPosition(0);
		String src = c.getString(c.getColumnIndex("src"));
		String path = c.getString(c.getColumnIndex("path"));
		String file_type = c.getString(c.getColumnIndex("file_type"));
		String file_name = c.getString(c.getColumnIndex("file_name"));
		boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
		boolean is_get = c.getInt(c.getColumnIndex("is_get")) == 1;
		if (!is_get) {
			Log.d(TAG, "startOne: Addmission");
			Log.d(TAG, "startExcitsone: " + path + file_name);
			cdm.resumeMission(id, src, path + "/" + file_name);
		} else {
			if (is_pause) {
				Log.d(TAG, "onReceive: added ex item");
			}
		}
	}

	public void resume(int id) {
		Log.d(TAG, "resume: ");
		Cursor c = MyIntentService.this.readabelDb.rawQuery("SELECT * FROM queue WHERE id = ?", new String[]{Integer.toString(id)});
		c.moveToPosition(0);
		String src = c.getString(c.getColumnIndex("src"));
		String path = c.getString(c.getColumnIndex("path"));
		String file_name = c.getString(c.getColumnIndex("file_name"));
		String file_type = c.getString(c.getColumnIndex("file_type"));
		boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
		boolean is_get = c.getInt(c.getColumnIndex("is_get")) == 1;
		try {
			cdm.resumeMission(id, src, "" + path + "/" + file_name + file_type);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void cancel(int id) {
		try {
			cdm.cancelMission(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy: ");
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.broadcastReceiver);
		wakeLock.release();
		super.onDestroy();

	}
}


