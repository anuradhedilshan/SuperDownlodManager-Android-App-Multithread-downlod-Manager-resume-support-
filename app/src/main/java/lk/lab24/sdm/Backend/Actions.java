package lk.lab24.sdm.Backend;

public class Actions {

	public final static String ACTION_addMission = "com.lab24.addMission";
	public final static String ACTION_addExMission = "com.lab24.addExMission";
	public final static String ACTION_pauseAll = "com.lab24.pauseAll";
	public final static String ACTION_pause = "com.lab24.pause";
	public final static String ACTION_resume = "com.lab24.resume";
	public final static String ACTION_cancel = "com.lab24.cancel";
	public final static String ACTION_startAll = "co.lab24.startAll";
	public final static String ACTION_update = "com.lab24.update";

	//Request Code
	public final static int PROGRESS_UPDATE = 200;
	public final static int ERROR_UPDATE = 400;

	// Status
	public static int READY = 1;
	public static int PENDING = 2;
	public static int DOWNLODING = 3;
	public static int PAUSE = 4;
	public static int CROUPTDOWN = 6;
	public static int CONNECTIONTIMEOUT = 7;
	public static int LINKEROR = 8;
	public static int ERROR = 9;
	public static int COMPLETE = 10;
	public static int CANCELL = 11;
}
