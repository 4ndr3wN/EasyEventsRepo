/**
package tue.easyevents;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Boy on 17-3-2016.
 
public class FoursquareAPI {
    public static String baseAddress = "https://api.foursquare.com/v2/venues/search?";
    public static String apiKey = "&client_id=IH2AWVOCK1JJWKWV3WJWT0DSHSGNYWGXEWDU0SWIYVGDKOQY&client_secret=KKZOPAYISYFGVBIPHV5OFMNSHKGGRM5UQB440HK4JAOHQDWX&v=20160317";
    private final static int delay = 10000; // in milliseconds.

    /**
     * Search for events in a location within a certain timeframe
     *
     * If you want to change the amount of events returned by this function, change the page_size
     * argument in this method
     * @param latLong
     * @param query
     * @throws ConnectException
     * @return An integer representing the likes a venue has
     */
/**
    public static int getRating(String latLong, String query)
            throws ConnectException {


        String searchParameters = "&ll=" + latLong + "&query=" + query;
        String searchAddress = baseAddress + searchParameters + apiKey;
        int likesVenue = -1;


        //We log the search address for testing purposes
        Log.d("Address", searchAddress);


        //Here the connection is made with the API and the generated searchAddress
        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
//            HttpURLConnection connect = (HttpURLConnection) searchAddress.openConnection();
            InputStream in = new BufferedInputStream(connect.getInputStream());
            String result = null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
            JSONObject jObject = new JSONObject(result);
            JSONObject test =new JSONObject(result);

        //Error handling
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return likesVenue;
    }

    //This method creates the connection for the API call
    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//        conn.setInstanceFollowRedirects(false);
//        conn.setReadTimeout(EventfulAPI.timeOut);
//        conn.setConnectTimeout(EventfulAPI.timeOut);
//        conn.setRequestMethod("POST");
//        conn.setDoInput(true);
//        conn.setDoOutput(false);
//
//        conn.connect();
        return conn;
    }
}
*/