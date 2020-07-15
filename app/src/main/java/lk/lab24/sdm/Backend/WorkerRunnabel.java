package lk.lab24.sdm.Backend;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


class WorkerRunnabel implements Runnable {

	private static final String TAG = "fuck";
	long off;
	long stop;
	HttpURLConnection con;
	File file;
	RandomAccessFile raf;
	URL url;
	BufferedInputStream bis;
	DownlodInfo df;
	String src;


	public WorkerRunnabel(long off, long stop, String src, File file, DownlodInfo df) {
		this.off = off;
		this.stop = stop;
		System.out.println(file.getAbsoluteFile());
		Log.d(TAG, "WorkerRunnabel: " + off + "and" + stop);
		try {
			this.url = new URL(src);
			this.con = (HttpURLConnection) url.openConnection();
			this.df = df;
			this.src = src;
			this.file = file;
		} catch (Exception ex) {
			df.setState(Actions.CONNECTIONTIMEOUT);
		}
	}

	@Override
	public void run() {
		Log.d("fuck", "Workor run");

		try {
			this.down();
		} catch (Exception ex) {
			Log.d(TAG, "run: Got a Error");

			this.df.setState(Actions.CONNECTIONTIMEOUT);
			Logger.getLogger(WorkerRunnabel.class.getName()).log(Level.SEVERE, null, ex);
		}


	}

	public void down() throws Exception {
		System.out.println("Down");

		this.con.setRequestProperty("Range", "bytes=" + off + "-" + stop);
		Log.d(TAG, "setPro: pro:" + this.con.getRequestProperty("Range"));
		Log.d(TAG, "setPro: Connection Length" + this.con.getContentLength());
		this.con.connect();
		this.raf = new RandomAccessFile(this.file, "rw");
		this.bis = new BufferedInputStream(con.getInputStream());
		byte[] buffer = new byte[1024];
		raf.seek(off);
		int read = 0;
		while ((read = bis.read(buffer)) != -1) {
			//cancell
			if (Thread.currentThread().isInterrupted() || df.is_COMPLETE || df.is_PAUSE) {
				break;
			}
			this.df.setProgress(read);
			raf.write(buffer, 0, read);

		}
		Log.d(TAG, "down: After while Downloded=" + df.getDownloded() + "file size =" + df.filesize);
		if (df.getDownloded() >= df.filesize) {
			Log.d(TAG, "down: DownlodComplete");
			Thread.sleep(500);
			df.setState(Actions.COMPLETE);
		}
		int responceCode = con.getResponseCode();
		if (responceCode < 300) {
			Log.d(TAG, "down: Connection fuvccdfhkjfhkj");


		}
		Log.d(TAG, "down: Responce code =" + con.getResponseCode() + "length =" + con.getContentLength());
		this.raf.close();
	}

}