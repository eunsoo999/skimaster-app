package lecture.mobile.final_project.ma01_20160940;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
	NotificationManager manager;
	Context cont;
	String name;
	public void onReceive(Context context, Intent intent) {
		Bundle data = intent.getExtras();
		name = data.getString("skiName");
		manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		cont = context;
		createNotification();

        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtone.play();

	}
	private void createNotification() {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(cont, "default");
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setAutoCancel(true);// 사용자가 탭을 클릭하면 자동 제거
		builder.setContentTitle("개장 알림");
		builder.setContentText(name + "개장하였습니다. 빨리 놀러가세요!!!");
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		//builder.setColor(Color.RED);

		// 알림 표시
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
		}

		manager.notify(1, builder.build());
	}
}