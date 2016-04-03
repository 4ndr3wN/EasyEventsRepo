
package tue.easyevents;

import android.util.Log;

import org.json.JSONArray;
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
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Boy on 17-3-2016.
*/
 
public class FoursquareAPI {
    public static String baseAddress = "https://api.foursquare.com/v2/venues/";
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

    public static int getRating(String latLong, String query)
            throws ConnectException, UnsupportedEncodingException {

        String urlQuery = URLEncoder.encode(query, "UTF-8");
        String searchParameters = "&ll=" + latLong + "&query=" + urlQuery;
        String searchAddress = baseAddress + "search?" + apiKey + searchParameters;
        int likesVenue = -1;

        //Here the connection is made with the API and the generated searchAddress
        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
            InputStream in = new BufferedInputStream(connect.getInputStream());
            String result = null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            //Parse the JSON response into a string
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();

            //Here we get the information we need out of the returned JSON object.
            JSONObject jObject = new JSONObject(result);
            JSONObject response = jObject.getJSONObject("response");
            JSONArray venues = response.getJSONArray("venues");
            //Check if there are any venues available
            if(venues.length()>0){
                JSONObject venue = venues.getJSONObject(0);
                String id = venue.getString("id");
                //Call likesVenue with the id of the requested venue
                likesVenue = getVenueLikes(id);
            }
        //Error handling
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return likesVenue;
    }

    private static int getVenueLikes(String id){
        int likes = -1;

        String searchAddress = baseAddress + id + "?" + apiKey;

        //Here we connect to the API
        try {

            HttpURLConnection connect = getHttpConnection(searchAddress);
            InputStream in = new BufferedInputStream(connect.getInputStream());

            String result = null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            //Parse the JSON response into a string
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();

            //Get the likes count from the JSONObject
            JSONObject jObject = new JSONObject(result);
            JSONObject response = jObject.getJSONObject("response");
            JSONObject venue = response.getJSONObject("venue");
            JSONObject likesJSON = venue.getJSONObject("likes");
            likes = likesJSON.getInt("count");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return likes;
    }

    //This method creates the connection for the API call
    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setReadTimeout(FoursquareAPI.delay);
        conn.setConnectTimeout(FoursquareAPI.delay);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);

        conn.connect();
        return conn;
    }
}
