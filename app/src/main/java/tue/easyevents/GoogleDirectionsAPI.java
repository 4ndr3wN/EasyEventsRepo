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
 * Created by Boy on 11-3-2016.
 */
public class GoogleDirectionsAPI {
    public static String BASE_ADDRESS = "http://maps.googleapis.com/maps/api/directions/XML?";
    public static String API_KEY = "&key=AIzaSyANaZSYC__XBBCPvkP8VGbdfW7OX86w9PQ";
    private final static int TIME_OUT = 10000; // in milliseconds.

    /**
     * Search for directions from A to B either by vehicle or by public transport
     *
     * @param origin
     * @param destination
     * @param mode
     * @param departure_time
     * @return An arraylist of directions fitting the criteria and parameters
     * @throws ConnectException
     */
    public static ArrayList<WAAROMWERKTDEZEVARIABELENIET> searchEvents(String origin, String destination, String mode, int departure_time, ArrayList<String> cat)
            throws ConnectException {

        InputStream in = null;
        ArrayList<WAAROMWERKTDEZEVARIABELENIET> routes = new ArrayList<>();
        String SEARCH_PARAMETERS = "origin=" + origin + "&destination=" + destination + "&mode=" + mode + "&departure_time=" + departure_time;
        String SEARCH_ADDRESS = BASE_ADDRESS + SEARCH_PARAMETERS + API_KEY;
    }
}


