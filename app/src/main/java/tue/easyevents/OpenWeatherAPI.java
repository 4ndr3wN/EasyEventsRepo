/*

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

public class OpenWeatherAPI {

    public static String baseAdress = "api.openweathermap.org/data/2.5/forecast?";
    public static String appid = "&appid=94217f697ba5de6a6ea15d90d6080bf1";
    private final static int timeOut = 10000; // in milliseconds.

*
     * Search for the weatherforecast at the location of the event
     * @param latitude
     * @param longitude
     * @throws ConnectException
     * @return An arraylist of the weather forecast from now




    public static ArrayList<Event> searchWeather(double latitude, double longitude)
            throws ConnectException, ParseException {

        InputStream in = null;
        ArrayList<Event> weather = new ArrayList<>();
        String locationSearch = "lat=" + latitude + "&lon=" +  longitude;     //Coordinaten van event locations
        String searchAddress = baseAdress + locationSearch + "&mode=xml" + appid;

        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
            in = connect.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(in));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("forecast");

            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i) != null){
                    Node node = nodeList.item(i);
                    String forecastTime = parseData(node, "from");
                    String icon = parseData(node, "var");

                    //Openweather geeft icoontjes per categorie
                    //Hieronder staat de rating die ik per icoontje toepasselijk vond

                    double ratingWeather = 0;

                    if (icon == "01d" || icon == "01n"){
                        ratingWeather = 10;
                    }
                    else if (icon == "02d" || icon == "02n"){
                        ratingWeather = 9;
                    }
                    else if (icon == "03d" || icon == "03n"){
                        ratingWeather = 7;
                    }
                    else if (icon == "04d" || icon == "04n"){
                        ratingWeather = 6;
                    }
                    else if (icon == "09d" || icon == "09n"){
                        ratingWeather = 4;
                    }
                    else if (icon == "10d" || icon == "10n"){
                        ratingWeather = 5;
                    }
                    else if (icon == "11d" || icon == "11n"){
                        ratingWeather = 4;
                    }
                    else if (icon == "13d" || icon == "13n"){
                        ratingWeather = 3;
                    }
                    else if (icon == "50d" || icon == "50n"){
                        ratingWeather = 5;
                    }

                    //Hier klopt sowieso niet zo veel van

                    if (forecastTime == Event.date){
                       Event.setRatingWeather(ratingWeather);
                    }


                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return weather;

    }


    private static String parseData(Node node, String tag) {
        String data = "";

        Element firstElement = (Element) node;
        //icon en time from staan als attribute ipv child
        if(tag.equals("from")){
            data = firstElement.getAttribute("from");
            return data;

            if(tag.equals("icon")){
                data = firstElement.getAttribute("icon");
                return data;

            NodeList dataList = firstElement.getElementsByTagName(tag);
            if (dataList.getLength() > 0){
                Element dataElement = (Element) dataList.item(0);
                dataList = dataElement.getChildNodes();
                data = dataList.item(0).getNodeValue();
        }

        return data;
    }

    private static HttpURLConnection getHttpConnection(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setReadTimeout(OpenWeatherAPI.timeOut);
        conn.setConnectTimeout(OpenWeatherAPI.timeOut);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(false);

        conn.connect();
        return conn;
    }
}
*/
