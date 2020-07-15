package lk.lab24.sdm.Backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lk.lab24.sdm.Database;

public class WorkorManager {
	private static final String TAG = "fuck";
	public static int NEWDOWNLOD = 100;
	public static int RESUMEDOWNLOD = 200;
	private Database db;
	private Context context;
	private Events events;


	public WorkorManager(Database db, Events events, Context context) {
		this.db = db;
		this.context = context;
		this.events = events;


	}

	public ExecutorService addMission(int TaskId, int id, String url, String path, String name, DownlodInfo di) {
		URLConnection con = this.getUrlConnetion(url, di);
		ExecutorService executorService = null;
		File f = null;
		try {
			Log.d("fuck", "addMission: Create File");
			String extension = "." + MimeTypeMap.getFileExtensionFromUrl(url);
			Log.d(TAG, "addMission: Extensions" + extension);
			f = this.creatFile(url, path, name, con);
			di.setFile(f);
			if (!f.exists() && !f.canWrite()) {
				Log.d(TAG, "addMission: cant write");
				events.error(id, "File Not exists Or Cant Write Data");
				di.setError("File not exits or Cant write data");


			} else if (f.getFreeSpace() < con.getContentLength()) {
				events.error(id, "Out of Storage");
				di.setError("Out of storage");
			} else {

				long length = con.getContentLength();
				di.setFilesize(length);
				this.db.setFileType(id, extension);
				this.db.setFileSize(id, length);
				LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_update));
				Log.d("fuck", "downlod prnekh");
				if (TaskId == NEWDOWNLOD) {

					if (con.getHeaderField("Accept-Ranges") != null) {
						Log.d("fuck", " support");
						long off = 0;
						long start = 0;
						long stop = length / 2;
						String src = url;
						executorService = Executors.newFixedThreadPool(2);
						executorService.execute(new WorkerRunnabel(off, stop, src, f, di));
						executorService.execute(new WorkerRunnabel(stop, length, src, f, di));
						executorService.shutdown();
					} else {
						Log.d(TAG, "addMission: NotSupport");
						long off = 0;
						long start = 0;
						long stop = length;
						String src = url;
						executorService = Executors.newFixedThreadPool(1);
						executorService.execute(new WorkerRunnabel(off, stop, src, f, di));

					}

				}
			}
		} catch (IOException e) {
			Log.d("fuck", "Error : " + e.toString());
			events.error(id, e.getMessage());

			e.printStackTrace();

		}
		return executorService;
	}


	//https://www.youtube.com/watch?v=RdR7SaycGyU&t=82s
	public ExecutorService addMission(int TaskId, int id, String url, DownlodInfo di, String ex) {
		URLConnection con = this.getUrlConnetion(url, di);
		Log.d(TAG, "addMission: EXMISSION");
		ExecutorService executorService = null;
		long downlodedSize = 0;
		long clength = 0;
		try {
			Log.d(TAG, "addEXMission: creat file " + ex);
			File f = new File(ex);
			long downloded = db.getDownloded(id) <= 0 ? f.length() : db.getDownloded(id);
			Log.d(TAG, "addMission: " + downloded);
			di.setDownloded(downloded);
			di.setFilesize(db.getFilesize(id));
			di.setFile(f);
			if (di.getDownloded() <= 0) {
				downlodedSize = f.length();
			} else {
				downlodedSize = di.getDownloded();
			}
			clength = di.getFilesize() <= 0 ? con.getContentLength() : di.getFilesize();

			String extension = "." + MimeTypeMap.getFileExtensionFromUrl(url);
			if (!f.exists() && !f.canWrite()) {
				Log.d(TAG, "addMission: fileEx =" + f.exists() + " canWrite =" + f.canWrite());
				events.error(id, "File Not exists Or Cant Write Data");
				di.setError("File not exits or Cant write data");

			} else if (f.getFreeSpace() < clength) {
				events.error(id, "Out of Storage");
				di.setError("Out of storage");


			} else if (downlodedSize >= clength) {
				events.error(id, "CAnnot Resume Alerdy Downloded Or file Error");
				di.setError("Cannot Resume file Arerdy downloded or File error");
			} else {
				String n = f.getName();
				if (ex == n) {
					f.renameTo(new File(n.split("\\.")[0] + extension));
					Log.d(TAG, "addMission: REsume Mision ex f;e");
				} else {
					f.getName();
				}
				long length = con.getContentLength();
				String src = url;
				LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_update));
				if (TaskId == RESUMEDOWNLOD) {
					if (con.getHeaderField("Accept-Ranges") != null) {
						long needDownlod = length - downlodedSize;
						long partSize = needDownlod / 2;
						executorService = Executors.newFixedThreadPool(2);
						executorService.execute(new WorkerRunnabel(downlodedSize, partSize, src, f, di));
						executorService.execute(new WorkerRunnabel(partSize, length, src, f, di));
						executorService.shutdown();
					} else {
						long needDownlod = length - downlodedSize;
						executorService = Executors.newFixedThreadPool(1);
						executorService.execute(new WorkerRunnabel(0, needDownlod, src, f, di));
					}

				}


			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		return executorService;
	}

	//-----------------------------------------------------------------------------------------------------------------------------------
	//Create FIle
	public File creatFile(String url, String paths, String names, URLConnection con) throws IOException {
		Log.d("fuck", " Create File inside");
		File downFile = null;
		File path = new File(paths);
		if (path.exists()) {
			String extension = "." + MimeTypeMap.getFileExtensionFromUrl(url);
			Log.d(TAG, "ex" + extension);
			Log.d(TAG, "extension = " + extension + "s" + path + "/" + names + extension);
			downFile = new File(paths + "/" + names + extension);
			Log.d("fuck", "creatFile: Downlfile name" + downFile.getAbsolutePath());
			downFile.createNewFile();
			return downFile;

		} else {
			path.mkdir();
			this.creatFile(url, paths, names, con);
		}
		return downFile;

	}

	//Creat Connection
	public URLConnection getUrlConnetion(String src, DownlodInfo di) {
		Log.d(TAG, "getUrlConnetion: ");
		URLConnection con;
		try {
			URL url = new URL(src);
			con = url.openConnection();
			//con.setDoOutput(true); (get Error Subtime)
			Log.d(TAG, "getUrlConnetion: " + con.getContentLength());

		} catch (Exception ex) {
			Log.d("fuck", "getUrlConnetion: Erro");
			di.setState(Actions.CONNECTIONTIMEOUT);
			con = null;
		}
		return con;

	}


	interface Events {
		void error(int id, String erro);
	}
}
