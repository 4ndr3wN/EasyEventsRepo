package tue.easyevents;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
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
 * Created by Adriaan on 17-3-2016.
 */
public class GeoCodingAPI {
    public static String baseAddress = "https://maps.googleapis.com/maps/api/geocode/xml?address=";
    public static String appKey = "&key=AIzaSyDc-zOLX34KkiohWYfd0JOmlfbEGv2VT9M";
    private final static int timeOut = 10000; // in milliseconds.


    /**
     * Get the latitude and longitude of an address
     *
     * @param address
     * @return The latitude and longitude in the format LAT-LONG
     */
    public static String geoCode(String address) throws ConnectException{

        InputStream in = null;
        String latLong = "";
        try {
            address = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchAddress = baseAddress + address + appKey;
        //Log the search address for testing
        Log.d("Address", searchAddress);

        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
            in = connect.getInputStream();

            //Change the inputstream into a document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(in));
            doc.getDocumentElement().normalize();

            //Parse the document
            NodeList nodeList = doc.getElementsByTagName("location");
            Node node = nodeList.item(0);
            NodeList dataList = node.getChildNodes();
            String latitude = dataList.item(1).getChildNodes().item(0).getNodeValue();
            String longitude = dataList.item(3).getChildNodes().item(0).getNodeValue();

            //Eventful requires this format of LAT,-LONG so that is how we return it.
            latLong = latitude + "," + longitude;


        //Error handling
        } catch (ParserConfigurationException e) {
            throw new ConnectException("ParserConfigurationException");
        } catch (SAXException e) {
            throw new ConnectException("SAXException");
        } catch (IOException e) {
            throw new ConnectException("IOException");
        } catch (NullPointerException e){
            throw new ConnectException("No data returned");
        }


        return latLong;
    }

    //This method creates the connection for the API call
    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setReadTimeout(GeoCodingAPI.timeOut);
        conn.setConnectTimeout(GeoCodingAPI.timeOut);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);

        conn.connect();
        return conn;
    }
}
