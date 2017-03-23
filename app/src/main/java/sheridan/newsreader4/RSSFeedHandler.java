package sheridan.newsreader4;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSFeedHandler extends DefaultHandler {

    private RSSFeed feed;
    private RSSItem item;

    private boolean isItem = false;
    private int depth = 0;

    private String localName = null;
    private StringBuilder cdata = null;

    public RSSFeed getFeed() {
        return feed;
    }

    @Override
    public void startDocument() throws SAXException {
        feed = new RSSFeed();
        item = new RSSItem();
    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {

        this.localName = localName;
        this.depth++;

        if(depth == 3){
            switch(localName){
                case "title":
                case "lastBuildDate":{
                    this.cdata = new StringBuilder();
                    break;
                }
            }
        }

        if(isItem){
            switch(localName){
                case "title":
                case "link":
                case "pubDate": {
                    this.cdata = new StringBuilder();
                    break;
                }
            }
            return;
        }

        if(localName.equals("item")){
            item = new RSSItem();
            isItem = true;
        }

    }

    @Override
    public void endElement(String namespaceURI, String localName,
            String qName) throws SAXException
    {

        if(depth == 3){
            switch(localName){
                case "title":{
                    feed.setTitle(cdata.toString());
                    break;
                }
                case "lastBuildDate":{
                    feed.setLastBuildDate(cdata.toString());
                    break;
                }
            }
        }

        if(isItem){
            switch(localName){
                case "item":{
                    feed.addItem(item);
                    isItem = false;
                    break;
                }
                case "title":{
                    item.setTitle(cdata.toString());
                    break;
                }
                case "link":{
                    item.setLink(cdata.toString());
                    break;
                }
                case "pubDate":{
                    item.setPubDate(cdata.toString());
                    break;
                }
            }
        }

        this.depth--;
    }

    @Override
    public void characters(char ch[], int start, int length) {

        if(depth == 3){
            switch(this.localName){
                case "title":
                case "lastBuildDate": {
                    this.cdata.append(ch,start,length);
                    break;
                }
            }
        }

        if(isItem) {
            switch (this.localName) {
                case "title":
                case "link":
                case "pubDate": {
                    this.cdata.append(ch, start, length);
                    break;
                }
            }
        }

    }
}