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

    public static String baseAdress = "http://api.openweathermap.org/data/2.5/forecast?";
    public static String appid = "&appid=94217f697ba5de6a6ea15d90d6080bf1";
    private final static int timeOut = 10000; // in milliseconds.




    /**
     * Search for the weatherforecast at the location of the event
     * @param latitude
     * @param longitude
     * @return An arraylist of the weather forecast from now
     * @throws ConnectException
     * @throws ParseException
     */
    public static int searchWeather(String latitude, String longitude, long date)
            throws ConnectException, ParseException {

        InputStream in = null;
        int ratingWeather = 0;

        //Coordinaten van event locations
        String locationSearch = "lat=" + latitude + "&lon=" +  longitude;
        String searchAddress = baseAdress + locationSearch + "&mode=xml" + appid;

//        String searchAddress = "http://api.openweathermap.org/data/2.5/forecast?lat=32.746682&lon=-117.162741&mode=xml&appid=0dfcf6d089af67d15ee8bcb4221b55c9";
        try {
            HttpURLConnection connect = getHttpConnection(searchAddress);
            in = connect.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(in));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("time");
            String icon = "xx";

            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i) != null){
                    Node node = nodeList.item(i);
                    Element firstElement = (Element) node;
                    String dateTime = firstElement.getAttribute("from");
                    Date dateDate = stringToDate(dateTime);
                    Long dateUnix = dateDate.getTime();
                    Long timeDifference = Math.abs(date-dateUnix);
                    if(timeDifference < 10800000){
                        icon = parseData(node, "symbol");
                        i = nodeList.getLength();
                    }
                }
            }

            //Openweather geeft icoontjes per categorie
            //Hieronder staat de rating die ik per icoontje toepasselijk vond
            if(icon.equalsIgnoreCase("xx")){
                ratingWeather = 0;
            }
            else if (icon.equalsIgnoreCase("01d") || icon.equalsIgnoreCase("01n")){
                ratingWeather = 5;
            }
            else if (icon.equalsIgnoreCase("02d") || icon.equalsIgnoreCase("02n")){
                ratingWeather = 5;
            }
            else if (icon.equalsIgnoreCase("03d") || icon.equalsIgnoreCase("03n")){
                ratingWeather = 4;
            }
            else if (icon.equalsIgnoreCase("04d") || icon.equalsIgnoreCase("04n")){
                ratingWeather = 3;
            }
            else if (icon.equalsIgnoreCase("09d") || icon.equalsIgnoreCase("09n")){
                ratingWeather = 2;
            }
            else if (icon.equalsIgnoreCase("10d") || icon.equalsIgnoreCase("10n")){
                ratingWeather = 2;
            }
            else if (icon.equalsIgnoreCase("11d") || icon.equalsIgnoreCase("11n")){
                ratingWeather = 1;
            }
            else if (icon.equalsIgnoreCase("13d") || icon.equalsIgnoreCase("13n")){
                ratingWeather = 1;
            }
            else if (icon.equalsIgnoreCase("50d") || icon.equalsIgnoreCase("50n")){
                ratingWeather = 3;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return ratingWeather;

    }

    /**This method makes a Date out of the string we got from the Eventful API.
     * As long as Eventful maintains their current date format it will never throw the ParseException
     *
     * @param date
     * @return the date entered, but as a Date object
     * @throws ParseException
     */
    private static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date formattedDate = format.parse(date);
        return formattedDate;
    }


    private static String parseData(Node node, String tag) {
        String data = "";
        Element firstElement = (Element) node;
        if(tag.equals("symbol")){
            //icon en time from staan als attribute ipv child
            NodeList dataList = firstElement.getChildNodes();
            Node dataNode = dataList.item(0);
            Element dataElement = (Element) dataNode;
            data = ((Element) dataNode).getAttribute("var");
        }
        return data;
    }


    private static HttpURLConnection getHttpConnection (String link)throws IOException {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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