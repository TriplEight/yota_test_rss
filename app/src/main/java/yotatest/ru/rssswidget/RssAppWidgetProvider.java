package yotatest.ru.rssswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Nikita on 05.06.2016.
 */
public class RssAppWidgetProvider extends AppWidgetProvider{

    private static int displayedNewsNumber=0;
    public static String WIDGET_BUTTON = "android.appwidget.WIDGET_BUTTON_RIGHT";
    public static String WIDGET_BUTTON_LEFT = "android.appwidget.WIDGET_BUTTON_LEFT";
    public void changeCurrentNews(int newNum){
        displayedNewsNumber = newNum;
    }
    private void firstUpdate(){
        XMLParser parser = XMLParser.initiate(AppWidgetConfigure.getmRssUrl());
        parser.fetchXML();
    }
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        XMLParser parser = XMLParser.initiate(AppWidgetConfigure.getmRssUrl());
        if (parser.getNewsCount()==0){
            firstUpdate();
        }
        if (displayedNewsNumber==parser.getNewsCount()){
            Toast.makeText(context, "Далее новостей нет!", Toast.LENGTH_SHORT).show();
            displayedNewsNumber--;
        }
        if (displayedNewsNumber==-1){
            Toast.makeText(context, "Пока обновлений не было!", Toast.LENGTH_SHORT).show();
            displayedNewsNumber++;
        }
        for (int i=0; i< N; i++){
            int appWidgetId = appWidgetIds[i];

            Log.v("widget","updating widget");
            //Intent intent = new Intent(context, SampleActivity.class);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent , 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            if (parser.getNewsCount()>0) {
                views.setTextViewText(R.id.twCaption, parser.getNewsTitleAt(displayedNewsNumber));
                views.setTextViewText(R.id.twText, parser.getNewsDescrAt(displayedNewsNumber));
            }
            Intent intent = new Intent(WIDGET_BUTTON);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.btnRight, pendingIntent );

            Intent intentl = new Intent(WIDGET_BUTTON_LEFT);
            PendingIntent pendingIntentl = PendingIntent.getBroadcast(context, 0, intentl, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.btnLeft, pendingIntentl );

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // do your stuff here on ACTION_RELOAD
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            displayedNewsNumber++;
        }
        if (WIDGET_BUTTON_LEFT.equals(intent.getAction())) {
            displayedNewsNumber--;
        }
        if (WIDGET_BUTTON_LEFT.equals(intent.getAction())||WIDGET_BUTTON.equals(intent.getAction())) {
            if (AppWidgetConfigure.getmRssUrl() != null) {
                XMLParser parser = XMLParser.initiate(AppWidgetConfigure.getmRssUrl());
                parser.fetchXML();
                Log.v("widget", parser.getNewsCount() + "");
            }
            //update widget
            Intent intentn = new Intent(context, RssAppWidgetProvider.class);
            intentn.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, RssAppWidgetProvider.class));
            intentn.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intentn);
        }
    }


}
