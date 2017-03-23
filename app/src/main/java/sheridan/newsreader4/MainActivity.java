package sheridan.newsreader4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private NewsReaderApp app;
    private RSSFeed feed;
    private FileIO io;

    private TextView titleTextView;
    private TextView lastBuildDateTextView;
    private ListView itemsListView;

    private NewFeedReceiver newFeedReceiver;
    private IntentFilter newFeedFilter;

    private final static String URL_TORONTO_NEWS = "http://rss.cbc.ca/lineup/canada-toronto.xml";
    private final static String URL_CANADA_NEWS = "http://rss.cbc.ca/lineup/canada.xml";
    private final static String URL_WORLD_NEWS = "http://rss.cbc.ca/lineup/world.xml";


    // GUI / menu sate
    private SharedPreferences savedState;
    public final static int TORONTO_NEWS = 0;
    public final static int CANADA_NEWS = 1;
    public final static int WORLD_NEWS = 2;
    public final static String[] NEWS_SITES = {URL_TORONTO_NEWS, URL_CANADA_NEWS, URL_WORLD_NEWS};
    private int newsSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        io = new FileIO(getApplicationContext());

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        lastBuildDateTextView = (TextView) findViewById(R.id.lastBildDateTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);

        itemsListView.setOnItemClickListener(this);

        savedState = getSharedPreferences("SavedSate", MODE_PRIVATE);

        app = (NewsReaderApp) getApplication();

        // create intent filter and receiver
        newFeedFilter = new IntentFilter(RSSFeed.NEW_FEED);
        newFeedReceiver = new NewFeedReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch(newsSelection){
            case TORONTO_NEWS:{
                menu.findItem(R.id.nav_toronto).setChecked(true);
                break;
            }
            case CANADA_NEWS:{
                menu.findItem(R.id.nav_canada).setChecked(true);
                break;
            }
            case WORLD_NEWS:{
                menu.findItem(R.id.nav_world).setChecked(true);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.nav_toronto:{
                (new DownloadFeed()).execute(URL_TORONTO_NEWS);
                app.setLastNewsSite(URL_TORONTO_NEWS);
                item.setChecked(true);
                newsSelection = TORONTO_NEWS;
                return true;
            }
            case R.id.nav_canada:{
                (new DownloadFeed()).execute(URL_CANADA_NEWS);
                app.setLastNewsSite(URL_CANADA_NEWS);
                item.setChecked(true);
                newsSelection = CANADA_NEWS;
                return true;
            }
            case R.id.nav_world:{
                (new DownloadFeed()).execute(URL_WORLD_NEWS);
                app.setLastNewsSite(URL_WORLD_NEWS);
                item.setChecked(true);
                newsSelection = WORLD_NEWS;
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt("NewsSelection", newsSelection);
        editor.commit();
        unregisterReceiver(newFeedReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsSelection = savedState.getInt("NewsSelection", TORONTO_NEWS);
        String lastNewsSite = NEWS_SITES[newsSelection];
        app.setLastNewsSite(lastNewsSite);
        (new DownloadFeed()).execute(lastNewsSite);

        // register receiver for filter
        registerReceiver(newFeedReceiver, newFeedFilter);
    }

    public void updateDisplay()
    {
        if (feed == null) {
            titleTextView.setText("Unable to get RSS feed");
            return;
        }

        // set the title and buil date for the feed
        titleTextView.setText(feed.getTitle());
        lastBuildDateTextView.setText(feed.getLastBuildDateFormatted());

        // get the items for the feed
        ArrayList<RSSItem> items = feed.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"date", "title"};
        int[] to = {R.id.pubDateTextView, R.id.titleTextView};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);

        Log.d("MainActivity", "Feed displayed");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {

        // get the item at the specified position
        RSSItem item = feed.getItem(position);

        // create an intent
        Intent intent = new Intent(this, ItemActivity.class);

        intent.putExtra("pubDate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }

    class DownloadFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            io.downloadFile(params[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("DowloadFeed", "Feed downloaded");
            (new ReadFeed()).execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, RSSFeed> {

        @Override
        protected RSSFeed doInBackground(Void... params) {
            return io.readFile();
        }

        @Override
        protected void onPostExecute(RSSFeed feed) {
            Log.d("ReadFeed", "Feed read");

            MainActivity.this.feed = feed;
            app.setFeedMillis(feed.getLastBuildDateMillis());
            Log.d("ReadFeed","Build Date" + feed.getLastBuildDate());

            // update the display for the activity
            MainActivity.this.updateDisplay();
        }
    }

    class NewFeedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("News reader", "New items broadcast received");

            String test = intent.getStringExtra("test");
            Log.d("News reader", "test: " + test);

            updateDisplay();
        }
    }
}
