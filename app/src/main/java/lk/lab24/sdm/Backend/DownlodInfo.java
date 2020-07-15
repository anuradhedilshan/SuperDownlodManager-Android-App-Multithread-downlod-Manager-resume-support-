package lk.lab24.sdm.Backend;


import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import lk.lab24.sdm.Database;
import lk.lab24.sdm.R;
import lk.lab24.sdm.dialogs.CustomNotification;

public class DownlodInfo {
	private static final String TAG = "fuck";


	public static Context context = null;
	public static ResultReceiver resultReceiver;
	static Database db;
	public AtomicLong downloded = new AtomicLong();
	public long filesize = 0;
	public boolean is_PAUSE = false;
	public boolean is_CANCELL = false;
	public boolean is_COMPLETE = false;
	Bundle b = new Bundle();
	private int state = 0;
	private File file = null;
	private int id = 0;
	private Notification.Builder notification;

	static void setDatabase(Database d) {

		db = d;
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		DownlodInfo.context = context;
	}

	public static void setResultReceiver(ResultReceiver resultReceiver) {
		DownlodInfo.resultReceiver = resultReceiver;
	}

	void setFile(File f) {
		b.putInt("ID", this.id);
		this.file = f;
	}

	public int getProgress() {

		double p = (double) (downloded.get() * 100) / filesize;
		final int currentProgress = (int) ((((double) downloded.get()) / ((double) filesize)) * 100);
		Log.d("fuck", "getProgress: " + currentProgress);
		return currentProgress;
	}

	void setProgress(int downlodedP) {
		downloded.addAndGet(downlodedP);
		b.putInt("ID", this.id);
		b.putInt("P", getProgress());
		resultReceiver.send(Actions.PROGRESS_UPDATE, b);
		try {
			this.getNotification().setProgress(100, getProgress(), false).setContentTitle(file.getName() + " " + "ID:" + id).setContentText("" + getProgress() + "%").setSmallIcon(R.drawable.downloading);
			CustomNotification.getNotificationManagerCompat().notify(id, this.getNotification().build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setError(String error) {
		Log.d(TAG, "setError: ");
		if (db != null) {
			Log.d(TAG, "setError: db not null");
			db.setErrorState(this.id);
			db.setPauseState(id);
			db.setError(id, error);
		}
		b.putInt("ID", this.id);
		b.putString("E", error);
		resultReceiver.send(Actions.ERROR_UPDATE, b);
		try {
			this.getNotification().setProgress(0, 0, false).setContentTitle("Cant Downlod" + file.getName().toLowerCase() + " " + "ID:" + id)
					.setContentText(error).setDefaults(Notification.DEFAULT_ALL).setPriority(Notification.PRIORITY_HIGH).setSmallIcon(R.drawable.error);
			CustomNotification.getNotificationManagerCompat().notify(id, this.getNotification().build());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void setComple() {
		resultReceiver.send(Actions.COMPLETE, b);
		if (db != null) {
			db.setDownloded(this.id, downloded.get());
			db.setComplete(this.id);
		}
		try {
			this.getNotification().setProgress(0, 0, false).
					setContentTitle("Downlod Complete" + file.getName().toLowerCase() + " " + "ID:" + id).setDefaults(Notification.DEFAULT_ALL).setPriority(Notification.PRIORITY_HIGH)
					.setOngoing(false).setSmallIcon(R.drawable.downloaded);
			CustomNotification.getNotificationManagerCompat().notify(id, this.getNotification().build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getDownloded() {
		Log.d("fuck", "getDownloded: " + downloded.get());
		return downloded.get();

	}

	public void setDownloded(long downloded) {
		Log.d(TAG, "setDownloded: " + downloded);
		this.downloded.set(downloded);
	}

	void setState(int state) {
		this.state = state;
		if (state == Actions.PENDING) {

		} else if (state == Actions.DOWNLODING) {
			this.is_PAUSE = false;
			this.is_CANCELL = false;

		} else if (state == Actions.CROUPTDOWN || state == Actions.CONNECTIONTIMEOUT) {
			this.setError("Connection Time Out");

		} else if (state == Actions.COMPLETE) {
			this.is_COMPLETE = true;
			this.setComple();
		} else if (state == Actions.PAUSE) {
			this.is_PAUSE = true;
		} else if (state == Actions.CANCELL) {
			this.is_CANCELL = true;
			try {
				this.file.deleteOnExit();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public String getFilename() {
		if (this.file != null) {
			return this.file.getName();
		} else {
			return "Undefinde";
		}
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesizes) {
		Log.d("fuck", "setFilesize: " + filesizes);
		this.filesize = filesizes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Notification.Builder getNotification() {
		return this.notification;
	}

	public void setNotification(Notification.Builder notification) {
		Log.d(TAG, "setNotification: " + notification.toString());
		this.notification = notification;
	}
}
