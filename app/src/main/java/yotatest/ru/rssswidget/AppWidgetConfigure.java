package yotatest.ru.rssswidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by Nikita on 04.06.2016.
 */
public class AppWidgetConfigure extends Activity {

    public static String STORAGE="preferences";
    private int mAppWidgetId;
    private static String mRssUrl = "";
    private EditText etRss;

    public static String getmRssUrl(){
        //TODO: load from preferences
        return mRssUrl;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_layout);
        setResult(RESULT_CANCELED);

        Context context = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Button btnSave = (Button)findViewById(R.id.btnSave);
        etRss = (EditText)findViewById(R.id.etRssUrl);
        SetAlarm(context);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRssUrl = etRss.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences(STORAGE, MODE_PRIVATE).edit();
                editor.putString("url", mRssUrl);
                editor.apply();
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                setResult(RESULT_OK, resultValue);

                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, AppWidgetConfigure.this, RssAppWidgetProvider.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
                sendBroadcast(intent);

                finish();
            }
        });





    }

    public static void SetAlarm(Context context)
    {
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
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
        }
    }


}