package sheridan.newsreader4;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class RSSFeed {


    public final static String NEW_FEED = "sheridan.newsreader4.NEW_FEED";
    private String title = null;
    private String lastBuildDate = null;
    private ArrayList<RSSItem> items;
        
    private SimpleDateFormat dateInFormat = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)");
        
    public RSSFeed() {
        items = new ArrayList<>();
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setLastBuildDate(String date) {
        this.lastBuildDate = date;
    }

    public long getLastBuildDateMillis() {
        Date date;
        try {
            date = dateInFormat.parse(lastBuildDate.trim());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long dateMillis = date.getTime();
        return dateMillis;
    }


    
    public String getLastBuildDate() {
        return lastBuildDate;
    }
    
    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }

    public String getLastBuildDateFormatted() {
        try {
            Date date = dateInFormat.parse(lastBuildDate.trim());
            String pubDateFormatted = dateOutFormat.format(date);
            return pubDateFormatted;
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    public RSSItem getItem(int index) {
        return items.get(index);
    }
    
    public ArrayList<RSSItem> getAllItems() {
        return items;
    }    
}