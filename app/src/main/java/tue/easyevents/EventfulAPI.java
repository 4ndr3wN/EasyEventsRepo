package tue.easyevents;


import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Adriaan on 10-3-2016.
 */
public class EventfulAPI {

    public static String BASE_ADDRESS = "http://api.eventful.com/rest/events";
    public static String APP_KEY = "&app_key=hTSLpFpxJ47C5W8t";
    private final static int TIME_OUT = 10000; // in milliseconds.


    /**
     * Search for events in a location within a certain timeframe
     *
     * If you want to change the amount of events returned by this function, change the page_size
     * argument in this method
     * @param latLong
     * @param from
     * @param to
     * @throws ConnectException
     * @return An arraylist of events fitting the criteria
     */
    public static ArrayList<Event> searchEvents(String latLong, String from, String to, ArrayList<String> cat)
            throws ConnectException{

        InputStream in = null;
        ArrayList<Event> events = new ArrayList<>();
        String TIMEFRAME = from + "00-" + to + "00";
        //To change the amount of returned events, edit the argument page_size here
        String SEARCH_PARAMETERS = "&location=" + latLong + "&date=" + TIMEFRAME + "&include=categories&page_size=10";
        String SEARCH_ADDRESS = BASE_ADDRESS + "/search?..." + SEARCH_PARAMETERS + APP_KEY;


        try {
            HttpURLConnection connect = getHttpConnection(SEARCH_ADDRESS);
            in = connect.getInputStream();

            // Set up an XML parser.
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null); // Enter the stream.
            parser.nextTag();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return events;

    }

    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setReadTimeout(EventfulAPI.TIME_OUT);
        conn.setConnectTimeout(EventfulAPI.TIME_OUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);

        conn.connect();
        return conn;
    }
}
