package yotatest.ru.rssswidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

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
        BootReciever.SetAlarm(context);
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
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {mAppWidgetId});
                sendBroadcast(intent);
                finish();
            }
        });


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);


    }


}