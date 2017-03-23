package sheridan.newsreader4;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class NewsReaderApp extends Application {

    private long feedMillis = -1;
    private String lastNewsSite;

    public synchronized String getLastNewsSite() {
        return lastNewsSite;
    }

    public synchronized void setLastNewsSite(String lastNewsSite) {
        this.lastNewsSite = lastNewsSite;
    }

    public synchronized void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }

    public long getFeedMillis() {
        return feedMillis;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences savedState = getSharedPreferences("SavedSate", MODE_PRIVATE);
        int newsSelection = savedState.getInt("NewsSelection", MainActivity.TORONTO_NEWS);
        lastNewsSite = MainActivity.NEWS_SITES[newsSelection];

        setFeedMillis(savedState.getLong("FeedMillis", 0));

        Log.d("News reader", "App started");

        // start service
        //Intent service = new Intent(this, NewsReaderService.class);
        //startService(service);
    }


}