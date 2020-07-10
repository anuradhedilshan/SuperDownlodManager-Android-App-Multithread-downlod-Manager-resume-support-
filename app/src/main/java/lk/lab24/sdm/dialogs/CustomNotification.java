package lk.lab24.sdm.dialogs;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import lk.lab24.sdm.R;

public class CustomNotification {
	public static NotificationManagerCompat notificationManagerCompat;
	public static String NOTIFICATIONGROUP = "anuradhe.lab24";
	private Context context;

	public CustomNotification(Context context) {
		this.context = context;
		this.notificationManagerCompat = NotificationManagerCompat.from(context);
	}


	public static NotificationManagerCompat getNotificationManagerCompat() {
		return notificationManagerCompat;
	}

	@SuppressLint("WrongConstant")
	public Notification.Builder createNotification(int id, String name) {
		@SuppressLint("WrongConstant")
		Notification.Builder builder = null;

		builder = new Notification.Builder(this.context).setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
				.setContentTitle(name)
				.setContentText("Pending")
				.setAutoCancel(true)
				.setProgress(100, 0, true)
				.setPriority(NotificationCompat.PRIORITY_MIN);


		this.notificationManagerCompat.notify(id, builder.build());
		return builder;
	}


}
