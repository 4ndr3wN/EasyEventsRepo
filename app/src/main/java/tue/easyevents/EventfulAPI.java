package tue.easyevents;


import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by Adriaan on 10-3-2016.
 */
public class EventfulAPI {

    public static String baseAddress = "http://api.eventful.com/rest/events";
    public static String appKey = "&app_key=hTSLpFpxJ47C5W8t";
    private final static int timeOut = 10000; // in milliseconds.


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

    // Add this in as input for implementing categories: ArrayList<String> cat
    public static ArrayList<Event> searchEvents(String latLong, String from, String to, int range)
            throws ConnectException{

        InputStream in = null;
        ArrayList<Event> events = new ArrayList<>();
        String timeframe = from + "00-" + to + "00";
        //To change the amount of returned events, edit the argument page_size here
        String searchParameters = "&location=" + latLong + "&date=" + timeframe +
                "&units=km&include=categories,links&page_size=25" + "&within=" + range;
        String searchAddress = baseAddress + "/search?..." + searchParameters +
                "&sort_order=popularity&sort_direction=descending" + appKey;

        //We log the search address for testing purposes
        Log.d("Address", searchAddress);

        //Here the connection is made with the API and the generated searchAddress
        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
            in = connect.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(in));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("event");
            //For each event we found we parse the data and then create an Event object to store in
            //the arraylist we intend to retun
            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i) != null){
                    Node node = nodeList.item(i);
                    String ticket = "";
                    //This is the standard icon for our events, just in case no picture is found
                    String picture = "mipmap-xxxhdpi/ic_launcher.png";
                    String info = "";
                    String id = "";
                    String title = "";
                    String venue = "";
                    String longitude = "";
                    String latitude = "";
                    String dateString = "";
                    String address = "";
                    String country = "";
                    String city = "";

                    //Sometimes one or more of these values are not available, to prevent
                    //NullPointerExceptions we surround everything in a try/catch construction
                    try {
                        city = parseData(node, "city_name");
                    } catch (NullPointerException e){
                        Log.d("Null", "city");
                        e.printStackTrace();
                    }

                    try {
                        country = parseData(node, "country_name");
                    } catch (NullPointerException e){
                        Log.d("Null", "country");
                        e.printStackTrace();
                    }

                    try {
                        address = parseData(node, "venue_address");
                    } catch (NullPointerException e){
                        Log.d("Null", "address");
                        e.printStackTrace();
                    }

                    try {
                        dateString = parseData(node, "start_time");
                    } catch (NullPointerException e){
                        Log.d("Null", "date");
                        e.printStackTrace();
                    }

                    try {
                        latitude = parseData(node, "latitude");
                    } catch (NullPointerException e){
                        Log.d("Null", "latitude");
                        e.printStackTrace();
                    }

                    try {
                        longitude = parseData(node, "longitude");
                    } catch (NullPointerException e){
                        Log.d("Null", "longitude");
                        e.printStackTrace();
                    }

                    try {
                        venue = parseData(node, "venue_name");
                    } catch (NullPointerException e){
                        Log.d("Null", "venue");
                        e.printStackTrace();
                    }

                    try {
                        title = parseData(node, "title");
                    } catch (NullPointerException e){
                        Log.d("Null", "title");
                        e.printStackTrace();
                    }

                    try {
                        id = parseData(node, "id");
                    } catch (NullPointerException e){
                        Log.d("Null", "id");
                        e.printStackTrace();
                    }

                    Boolean ticketAvailable = true;
                    try {
                        ticket = parseData(node, "links");
                    } catch (NullPointerException e){
                        Log.d("Null", "ticket");
                        ticketAvailable = false;
                        e.printStackTrace();
                    }


                    try {
                        picture = parseData(node, "image");
                    } catch (NullPointerException e){
                        Log.d("Null", "image");
                        picture = "mipmap-xxxhdpi/ic_launcher.png";
                        e.printStackTrace();
                    }


                    try {
                        info = parseData(node, "description");
                    } catch (NullPointerException e){
                        Log.d("Null", "info");
                        e.printStackTrace();
                    }


                    Date dateDate = stringToDate(dateString);
                    Long date = dateDate.getTime();
                    //Create an Event object and add it to the arraylist
                    events.add(new Event(address, country, city, info, picture, ticket, title,
                            venue, longitude, latitude, 0, 0, 0, 0, date, id, ticketAvailable));
                }

            }
        //Error handling
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return events;

    }

    /**This method makes a Date out of the string we got from the Eventful API.
     * As long as Eventful maintains their current date format it will never throw the ParseException
     *
     * @param date
     * @return the date entered, but as a Date object
     * @throws ParseException
     */
    private static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date formattedDate = format.parse(date);
        return formattedDate;
    }


    /**This method parses the XML file and returns the information from the relevant tag.
     * It has a general way of parsing for most tags, but some require a different approach
     * @param node
     * @param tag
     * @return The string from the requested tag
     */
    private static String parseData(Node node, String tag) throws NullPointerException{
        String data = " ";

        Element firstElement = (Element) node;
        //The event ID is contained in the attribute of the firstElement and not as a child,
        //therefore we need to check if the tag is "id" first
        if(tag.equals("id")){
            data = firstElement.getAttribute("id");
            return data;
        }

        //Get all the elements with the proper tag name, in almost all cases this is just 1 child.
        NodeList dataList = firstElement.getElementsByTagName(tag);
        //The image urls are nested inside multiple children, which is why we need to check if
        //an image is requested
        if(tag.equals("image")){
            if(dataList.item(0).getChildNodes().getLength()>1){
                int biggestPicture = dataList.item(0).getChildNodes().getLength()-2;
                NodeList pictureInfo = dataList.item(0).getChildNodes().item(biggestPicture).getChildNodes();
                data = pictureInfo.item(1).getChildNodes().item(0).getNodeValue();

            }
            return data;
        }

        //The description of events is formatted in such a way that we need the second child of
        //dataList instead of the first
        if(tag.equals("description")){
            Element dataElement = (Element) dataList.item(1);
            dataList = dataElement.getChildNodes();
            if (dataList.item(0).getNodeValue() == null){
                return data;
            }
            data = dataList.item(0).getNodeValue();
            return data;
        }

        //The ticket links are again nested inside multiple children, which is why we need another
        //check
        if (tag.equals("links")){
            Element dataElement = (Element) dataList.item(0);
            if(dataElement.getChildNodes() == null){
                return data;
            }
            dataList = dataElement.getChildNodes();
            if(dataList.item(1).getChildNodes().item(1).getChildNodes() == null){
                return data;
            }
            data = dataList.item(1).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
            return data;
        }
        Element dataElement = (Element) dataList.item(0);
        dataList = dataElement.getChildNodes();

        if (dataList.item(0).getNodeValue() == null) {
            return data;
        }
        data = dataList.item(0).getNodeValue();


        return data;
    }

    //This method creates the connection for the API call
    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setReadTimeout(EventfulAPI.timeOut);
        conn.setConnectTimeout(EventfulAPI.timeOut);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);

        conn.connect();
        return conn;
    }
}
