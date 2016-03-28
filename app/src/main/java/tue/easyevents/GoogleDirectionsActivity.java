package tue.easyevents;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Line;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.model.StopPoint;
import com.akexorcist.googledirection.model.TimeInfo;
import com.akexorcist.googledirection.model.TransitDetail;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class GoogleDirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyDc-zOLX34KkiohWYfd0JOmlfbEGv2VT9M";
    //change preferably according to center of origin and destination
    private LatLng camera;
    //change according to origin for directions
    private LatLng origin;
    //change according to destination (location of event)
    private LatLng destination;

    String latitude;
    String longitude;
    double la;
    double lo;

    public String latitudeLoc;
    public String longitudeLoc;
    public double eventLa;
    public double eventLo;
    public String standardLoc;
    public String userLoc;
    public double cameraLat;
    public double cameraLon;
    public Boolean go = false;
    public Boolean pt = false;
    public Boolean car = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("lon");
        latitudeLoc = intent.getStringExtra("eventLat");
        longitudeLoc = intent.getStringExtra("eventLong");

        //check settings for standard origin (user location or different address)
        //see if the "use location" checkbox is checked
        //if so, set la and lo to the users latitude and longitude
        userLoc = inputFile("GPSCheck");
        if (userLoc.equals("1") && latitude != null && longitude != null) {
            //Parse user location strings to double
            la = Double.parseDouble(latitude);
            lo = Double.parseDouble(longitude);
        } else {
            //use location checkbox not set
            //use standard location
            standardLoc = inputFile("address_file");
            new GeoCode().execute();

            while (!go) {
                //wait
            }

            //split up geocoded location
            StringTokenizer str = new StringTokenizer(standardLoc, ",");
            String oriLat = str.nextElement().toString();
            String oriLon = str.nextElement().toString();

            //parse oriLat and oriLon to double
            la = Double.parseDouble(oriLat);
            lo = Double.parseDouble(oriLon);
        }
        //set origin LatLng according to above if-else clause
        origin = new LatLng(la, lo);

        eventLa = Double.parseDouble(latitudeLoc);
        eventLo = Double.parseDouble(longitudeLoc);

        //set destination LatLng
        destination = new LatLng(eventLa, eventLo);

        //set camera latitude in the middle of the origin and destination
        cameraLat = (eventLa + la)/2;
        //set camera longitude in the middle of the origin and destination
        cameraLon = (eventLo + lo)/2;

        //set camera
        camera = new LatLng(cameraLat, cameraLon);

        //TODO: find some way to set the zoom appropriately depending on the distance

        setContentView(R.layout.directions_activity);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //TODO: find some way to set the zoom appropriately depending on the distance
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 9));

        //car = true;
        //requestDirectionDriving();
        pt = true;
        requestDirectionPublic();
    }

    public void requestDirectionDriving() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    public void requestDirectionPublic() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        String testStatus;
        String instruction;
        String duration;
        String distance;
        testStatus = direction.getStatus();
        Log.d("TestStatus", testStatus);
        if (direction.isOK()) {
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));

            Route route = direction.getRouteList().get(0);
            Leg leg = route.getLegList().get(0);
            Step step;

            ArrayList<String> instructions = new ArrayList<>();
            ArrayList<String> durations= new ArrayList<>();
            ArrayList<String> distances = new ArrayList<>();

            if (car) {
                for (int i = 0; i < leg.getStepList().size(); i++) {
                    step = leg.getStepList().get(i);
                    instruction = step.getHtmlInstruction();
                    instruction = instruction.replace("<b>", "");
                    instruction = instruction.replace("</b>", "");
                    instruction = instruction.replace("<div style=\"font-size:0.9em\">", "");
                    instruction = instruction.replace("</div>", "");
                    instructions.add(instruction);
                    distance = step.getDistance().getText();
                    distances.add(distance);

                    duration = step.getDuration().getText();
                    durations.add(duration);
                }
            }

            if(pt) {
                StopPoint arrivalStopPoint;
                TransitDetail transitDetail;
                StopPoint departureStopPoint;
                TimeInfo arriveTimeInfo;
                TimeInfo departureTimeInfo;
                Line transitLine;
                String arrivalStopPointSTR;
                String departureStopPointSTR;
                String arrivalTime;
                String departureTime;
                String line;
                String line2;

                for (int i = 0; i < leg.getStepList().size(); i++) {
                    step = leg.getStepList().get(i);

                    if (step.getTravelMode().equals("WALKING")) {
                        instruction = step.getHtmlInstruction();
                        instruction = instruction.replace("<b>", "");
                        instruction = instruction.replace("</b>", "");
                        instruction = instruction.replace("<div style=\"font-size:0.9em\">", "");
                        instruction = instruction.replace("</div>", "");
                        instructions.add(instruction);
                        distance = step.getDistance().getText();
                        distances.add(distance);

                        duration = step.getDuration().getText();
                        durations.add(duration);
                    }

                    if (step.getTravelMode().equals("TRANSIT")) {
                        transitDetail = step.getTransitDetail();
                        arrivalStopPoint = transitDetail.getArrivalStopPoint();
                        arrivalStopPointSTR = arrivalStopPoint.getName();
                        //Log.d("test1", arrivalStopPointSTR);

                        departureStopPoint = transitDetail.getDepartureStopPoint();
                        departureStopPointSTR = departureStopPoint.getName();
                        //Log.d("test2", departureStopPointSTR);

                        arriveTimeInfo = transitDetail.getArrivalTime();
                        arrivalTime = arriveTimeInfo.getText();
                        //Log.d("time", arrivalTime);

                        departureTimeInfo = transitDetail.getDepartureTime();
                        departureTime = departureTimeInfo.getText();
                        //Log.d("time2", departureTime);

                        transitLine = transitDetail.getLine();
                        line = transitLine.getShortName();
                        //Log.d("line2", line);
                        line2 = transitLine.getName();

                        instructions.add("Take bus/train " + line + " " + line2 + " to " + arrivalStopPointSTR + " at " + departureStopPointSTR + " at " + departureTime);
                        instructions.add("You will arrive at " + arrivalStopPointSTR + " at " + arrivalTime);
                    }
                }
                for (int i = 0; i < instructions.size(); i++) {
                    Log.d("instr", instructions.get(i));
                }
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //nog niks
        Log.d("KAPOT", "WAAROM WERKT HET NIET");
    }

    public String inputFile(String file_name) {
        String saved = "";
        String read;
        //check for fileinput files
        try {
            FileInputStream fis = openFileInput(file_name);
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            if (fis != null) {
                while ((read = reader.readLine()) != null) {
                    buffer.append(read);
                }
            }
            fis.close();
            saved = buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saved;
    }

    //use an Asynchronous Task to do the GeoCodingAPI and EventfulAPI calls
    public class GeoCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                standardLoc = GeoCodingAPI.geoCode(standardLoc);
                go = true;
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
