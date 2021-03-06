package yotatest.ru.rssswidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Nikita on 05.06.2016.
 */
public class AMReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent_received) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setUpNextAlarmInMin(context);
        }
        //get new data
        RssAppWidgetProvider.findUrl(context);
        Log.v("AMReceiver",""+RssAppWidgetProvider.getRssUrl() );
        if (RssAppWidgetProvider.getRssUrl() != null ) {

            // This is the Intent to deliver to our service.
            Intent service = new Intent(context, WakefulService.class);
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, service);
            RssAppWidgetProvider.displayedNewsNumber=0;
        }
        //update widget
        Intent intent = new Intent(context, RssAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, RssAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void setUpNextAlarmInMin(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, AMReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        //set wake up every minute.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.roll(Calendar.MINUTE, 1);
        Log.v("widgetdate", calendar.getTime().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }



}
