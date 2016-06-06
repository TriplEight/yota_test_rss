package yotatest.ru.rssswidget;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Nikita on 05.06.2016.
 */
public class XMLParser {
    private String title = "title";
    private String description = "description";
    private static String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ArrayList<ParsedObject> parsedObjects;
    private static XMLParser xmlParser;

    public int getNewsCount(){
        return parsedObjects.size();
    }

    public String getNewsTitleAt(int i){
        return parsedObjects.get(i).title;
    }
    public String getNewsDescrAt(int i){
        return parsedObjects.get(i).description;
    }
    public static XMLParser initiate(String url){
        if (xmlParser == null){
            xmlParser = new XMLParser(url);
        }
        if (!urlString.equals(url)){
            xmlParser = new XMLParser(url);
        }
        return xmlParser;

    }

    private XMLParser(String url){
        this.urlString = url;
        parsedObjects = new ArrayList<>();
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        parsedObjects = new ArrayList<>();
        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                if (name == null){
                    event = myParser.next();
                    continue;
                }
                if (!name.equals("item")){
                    event = myParser.next();
                    continue;
                } else {
                    event = myParser.next();
                    name = myParser.getName()==null?"":myParser.getName();
                    ParsedObject parsedObject = new ParsedObject();
                    while(!name.equals("item")){
                        name = myParser.getName()==null?"":myParser.getName();
                        switch (event){
                            case XmlPullParser.START_TAG:
                                break;

                            case XmlPullParser.TEXT:
                                text = myParser.getText();
                                break;

                            case XmlPullParser.END_TAG:

                                if(name.equals("title")){
                                    title = text;
                                    parsedObject.title = title;
                                } else if(name.equals("description")){
                                    description = text;
                                    parsedObject.description = description;
                                }
                                break;
                        }
                        event = myParser.next();
                    }
                    if (!parsedObjects.contains(parsedObject)){
                        parsedObjects.add(parsedObject);
                    }


                }
            }

            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();

                }

                catch (Exception e) {
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ParsedObject{
        private String title;
        private String description;
        @Override
        public boolean equals(Object object)
        {
            boolean sameSame = false;

            if (object != null && object instanceof ParsedObject)
            {
                sameSame = this.title.equals(((ParsedObject) object).title) & this.description.equals(((ParsedObject) object).description);
            }

            return sameSame;
        }

    }
}