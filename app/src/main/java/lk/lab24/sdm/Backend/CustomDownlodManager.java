package lk.lab24.sdm.Backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lk.lab24.sdm.Database;
import lk.lab24.sdm.dialogs.CustomNotification;

public class CustomDownlodManager {
	private static final String TAG = "fuck";
	public static HashMap<Integer, ExecutorService> executorServiceHashMap = new HashMap<>();
	public static HashMap<Integer, DownlodInfo> downlodInfoHashMap = new HashMap<>();
	ExecutorService es = Executors.newFixedThreadPool(2);
	WorkorManager wm;
	Context context;
	int idM = 1;
	private Database db;
	private CustomNotification c;


	public CustomDownlodManager(Database db, Context context) {
		this.db = db;
		this.context = context;
		Log.d(TAG, "CustomDownlodManager: ");
		c = new CustomNotification(this.context);
		DownlodInfo.setDatabase(this.db);
		this.wm = new WorkorManager(db, (int id, String error) -> {
			Log.d(TAG, "CustomDownlodManager: Error" + error);
		}, context);
	}

	public static HashMap<Integer, DownlodInfo> getDownlodInfoHashMap() {
		return downlodInfoHashMap;
	}


	public void setError(int id) {
		db.setErrorState(id);
	}

	// return Id
	public void addMision(int id, String url, String path, String name) {


		Log.d("fuck", "addMision: ");
		DownlodInfo d = new DownlodInfo();
		d.setNotification(c.createNotification(id, new File(path + name).getName()));
		d.setState(Actions.PENDING);
		d.setId(id);

		downlodInfoHashMap.put(id, d);
		es.submit(() -> {
			synchronized (this) {
				Log.d("fuck", "addMision: adddde" + id);
				db.setGetState(id);
				db.removeErrorState(id);
				ExecutorService s = wm.addMission(WorkorManager.NEWDOWNLOD, id, url, path, name, downlodInfoHashMap.get(id));
				executorServiceHashMap.put(id, s);
			}
		});
		System.out.println("Downlod hashmap sixe : " + downlodInfoHashMap.size());


	}

	public void addExitsMission(int id, String url, String exFile) {
		Log.d("fuck", "addExitsMission: ");
		DownlodInfo d = new DownlodInfo();
		d.setNotification(c.createNotification(id, new File(exFile).getName()));
		Log.d(TAG, "addExitsMission: After Notification");
		d.setState(Actions.PENDING);
		d.setId(id);
		db.removeErrorState(id);
		d.setDownloded(db.getDownloded(id));
		downlodInfoHashMap.put(id, d);
		es.submit(() -> {
			synchronized (this) {
				Log.d(TAG, "addExitsMission: " + exFile);
				db.setGetState(id);
				db.removeErrorState(id);
				ExecutorService s = wm.addMission(WorkorManager.RESUMEDOWNLOD, id, url, downlodInfoHashMap.get(id), exFile);
				executorServiceHashMap.put(id, s);
			}
		});


	}

	public boolean isRuning() {
		return true;
	}

	public void cancelMission(int id) {
		this.db.setcancelState(id);
		LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_update));
		DownlodInfo downlodInfo = downlodInfoHashMap.get(id);
		downlodInfo.setState(Actions.CANCELL);
		downlodInfoHashMap.remove(id);
		ExecutorService e = executorServiceHashMap.remove(id);
		e.shutdownNow();


	}

	public void pauseMission(int id) {
		new Thread(() -> {

			this.db.setPauseState(id);
			Log.d("fuck", "pauseMission");
			DownlodInfo downlodInfo = downlodInfoHashMap.get(id);

			ExecutorService s = executorServiceHashMap.get(id);
			if (s != null) {
				s.shutdownNow();
				Log.d(TAG, "pauseMission: isShutdown : " + s.isShutdown());
				Log.d(TAG, "pauseMission: isTerminated" + s.isTerminated());
				if (downlodInfo != null) {
					downlodInfo.setState(Actions.PAUSE);
				}
				synchronized (this) {
					while (!s.isTerminated()) {
						try {
							Thread.sleep(1000);
							Log.d(TAG, "pauseMission: in while");
							db.setDownloded(id, downlodInfo.getDownloded());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}


			executorServiceHashMap.remove(id);
			downlodInfoHashMap.remove(id);


		}).start();

	}

	public void resumeMission(int id, String src, String file) {
		Log.d("fuck", "resumeMission: ");
		this.addExitsMission(id, src, file);
		db.setResumeState(id);

	}

	public void finiseMission(int id) {

	}

	public boolean isRun() {
		return es.isShutdown();
	}


}


