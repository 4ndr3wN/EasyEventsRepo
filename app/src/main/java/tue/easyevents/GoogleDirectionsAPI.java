//
//package tue.easyevents;
//
//<<<<<<< HEAD
//import android.util.Xml;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Array;
//=======
//>>>>>>> origin/master
//import java.net.ConnectException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
///**
// * Created by Boy on 11-3-2016.
// */
//public class GoogleDirectionsAPI {
//    public static String baseAddress = "http://maps.googleapis.com/maps/api/directions/XML?origin=";
//    public static String key = "&key=AIzaSyANaZSYC__XBBCPvkP8VGbdfW7OX86w9PQ";
//
//    private static int timeOut = 10000; // in milliseconds
//
//    public static String[] testReturn = new String[0];
//
//    /**
//     * Search for directions from A to B either by vehicle or by public transport
//     *
//     * @param origin
//     * @param destination
//     * @param arrival_time
//     * @return An arraylist of directions fitting the criteria and parameters
//     * @throws ConnectException
//     */
//<<<<<<< HEAD
//    public static String[] searchEvents(String origin, String destination, int arrival_time)
//            throws ConnectException {
//
//
//        InputStream in = null;
//        String searchParameters = "origin=" + origin + "&destination=" + destination + "&arrival_time=" + arrival_time;
//        String searchAddress = baseAddress + searchParameters + key;
//
//        try {
//            HttpURLConnection connect = getHttpConnection(searchAddress);
//            in = connect.getInputStream();
//
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            Document doc = db.parse(new InputSource(in));
//            doc.getDocumentElement().normalize();
//
//            NodeList nodeList = doc.getElementsByTagName("event");
//
//            for (int i = 0; i < nodeList.getLength(); i++) {
//                Node node = nodeList.item(i);
//
//                String info = parseData(node, "description");
//                String picture = parseData(node, "image");
//                String ticket = "";
//                String title = parseData(node, "title");
//                String venue = parseData(node, "venue_name");
//                String longitude = parseData(node, "longitude");
//                String latitude = parseData(node, "latitude");
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
//
//        return testReturn;
//    }
//
//    private static String parseData(Node node, String tag) {
//        String data = "";
//
//        Element firstElement = (Element) node;
//
//        NodeList dataList = firstElement.getElementsByTagName(tag);
//        if (dataList.getLength() > 0){
//            Element dataElement = (Element) dataList.item(0);
//            dataList = dataElement.getChildNodes();
//            data = dataList.item(0).getNodeValue();
//        }
//
//        return data;
//    }
//
//    private static HttpURLConnection getHttpConnection(String link)
//            throws IOException {
//        URL url = new URL(link);
//        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//        conn.setInstanceFollowRedirects(false);
//        conn.setReadTimeout(GoogleDirectionsAPI.timeOut);
//        conn.setConnectTimeout(GoogleDirectionsAPI.timeOut);
//        conn.setRequestMethod("GET");
//        conn.setDoInput(true);
//        conn.setDoOutput(false);
//
//        conn.connect();
//        return conn;
//    }
//=======
////    public static ArrayList<WAAROMWERKTDEZEVARIABELENIET> searchEvents(String origin, String destination, String mode, int departure_time, ArrayList<String> cat)
////            throws ConnectException {
////
////        InputStream in = null;
////        ArrayList<WAAROMWERKTDEZEVARIABELENIET> routes = new ArrayList<>();
////        String SEARCH_PARAMETERS = "origin=" + origin + "&destination=" + destination + "&mode=" + mode + "&departure_time=" + departure_time;
////        String SEARCH_ADDRESS = BASE_ADDRESS + SEARCH_PARAMETERS + API_KEY;
////    }
//>>>>>>> origin/master
//}
//
