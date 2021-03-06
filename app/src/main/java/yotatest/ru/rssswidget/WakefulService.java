package yotatest.ru.rssswidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Nikita on 09.06.2016.
 */
public class WakefulService extends IntentService {
    public WakefulService() {
        super("WakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.
        XMLParser parser =  XMLParser.initiate(RssAppWidgetProvider.getRssUrl());
        parser.fetchXML();
        Log.v("wakefulservice", parser.getNewsTitleAt(0));
        //update widget
        Intent intent_rss = new Intent(getApplicationContext(), RssAppWidgetProvider.class);
        intent_rss.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), RssAppWidgetProvider.class));
        intent_rss.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getApplicationContext().sendBroadcast(intent_rss);

        AMReceiver.completeWakefulIntent(intent_rss);
    }
}