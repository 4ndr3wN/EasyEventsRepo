package tue.easyevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
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
import java.util.StringTokenizer;

public class GoogleDirectionsActivity extends AppCompatActivity
        implements OnMapReadyCallback, DirectionCallback {

    private GoogleMap googleMap;
    private String serverKey = "AIzaSyDc-zOLX34KkiohWYfd0JOmlfbEGv2VT9M";

    //change preferably according to center of origin and destination
    private LatLng camera;

    //change according to origin for directions
    private LatLng origin;

    //change according to destination (location of event)
    private LatLng destination;

    //get from intent, latitude of user
    public String latitude;

    //get from intent, longitude of user
    public String longitude;

    //get from intent, event location latitude
    public String latitudeLoc;

    //get from intent, event location longitude
    public String longitudeLoc;

    //departure time of public transport
    public String depart;

    //when not using user location, this is the standard location to find routes from
    public String standardLoc;

    //check if the "use location" checkbox is set in the settings
    public String userLoc;

    //total duration of the route find
    public String totalDuration;

    public String eventTitle;
    public String prevQuery;

    //doubles to parse the string above to doubles so that they can be used in API calls
    public double eventLa;
    public double eventLo;
    public double la;
    public double lo;
    public double cameraLat;
    public double cameraLon;
    public double date;
    public double hm; //hours + minutes of the duration of the route

    //wait for API to finish geocoding
    public Boolean go = false;

    //requested public transport or car route
    public Boolean pt = false;
    public Boolean car = false;

    public Boolean firstRequest = true;

    //boolean that keeps track if we used the user location or not
    public boolean usedLocation;

    //false for public transport, true for personal
    public Boolean personalTransport = true;

    //linear layout used to add the transport information programmatically
    public LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transport_view);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get values from previous activity
        Intent intent = getIntent();
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("lon");
        latitudeLoc = intent.getStringExtra("eventLat");
        longitudeLoc = intent.getStringExtra("eventLong");
        date = intent.getDoubleExtra("date", 0);
        eventTitle = intent.getStringExtra("title");
        usedLocation = intent.getBooleanExtra("usedLocation", false);
        if (!usedLocation) {
            prevQuery = intent.getStringExtra("prevQuery");
        }

        //layout
        TextView title = (TextView) findViewById(R.id.event_name);
        title.setText(eventTitle);

        //buttons
        //public transport
        Button ticketButton = (Button) findViewById(R.id.btn_tickets);
        //personal transport
        Button routeButton = (Button) findViewById(R.id.btn_detail_route);

        ticketButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                googleMap.clear();
                layout = (LinearLayout) findViewById(R.id.info_layout);
                layout.removeAllViews();
                requestDirectionPublic();
            }
        });

        routeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                googleMap.clear();
                layout = (LinearLayout) findViewById(R.id.info_layout);
                layout.removeAllViews();
                requestDirectionDriving();
            }
        });

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
                //wait for GeoCoding API to finish
            }

            //split up geocoded location
            StringTokenizer str = new StringTokenizer(standardLoc, ",");
            String oriLat = str.nextElement().toString();
            String oriLon = str.nextElement().toString();

            //parse la and lo to double
            la = Double.parseDouble(oriLat);
            lo = Double.parseDouble(oriLon);
        }

        //set origin LatLng according to above if-else clause
        origin = new LatLng(la, lo);

        //parse eventLa and eventLo to double
        eventLa = Double.parseDouble(latitudeLoc);
        eventLo = Double.parseDouble(longitudeLoc);

        //set destination LatLng
        destination = new LatLng(eventLa, eventLo);

        //set camera latitude
        cameraLat = la;
        //set camera longitude
        cameraLon = lo;
        //set camera
        camera = new LatLng(cameraLat, cameraLon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //Make sure you can search from this activity as well
        SearchView searchView = (SearchView) findViewById(R.id.search_events);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String userQuery) {

                //Set the searchQuery variable in the main activity
                MainActivity.searchQuery = userQuery;
                MainActivity.alreadySearched = false;

                Intent intent = new Intent(GoogleDirectionsActivity.this, MainActivity.class);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public void btn_close_detailview(View v) {
         //Super class method back pressed, to get the original map activity back
         //Need to change the onclick in the layout to refer to a different name.
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            Intent intent = new Intent(this, Settings_Activity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_about_us) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.action_about_us_msg)
                    .setTitle(R.string.action_about_us_title)
                    .setPositiveButton
                            (R.string.action_about_us_like, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE DEM MISSILES! A.K.A LIKE OUR APP
                        }
                    }).setNegativeButton
                    (R.string.action_about_us_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // CANCEL DIALOG
                }
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            //Set AlertDialog background to our theme
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.pop_up);
            //Display Dialog
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //set zoom of the camera/map
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 9));

        //public transport or personal transport
        //initialized to personal transport, can be used to start with public transport by passing
        //a parameter to this class, using the intent
        if (personalTransport) {
            car = true;
            requestDirectionDriving();
        } else {
            pt = true;
            requestDirectionPublic();
        }
    }

    //request the directions for personal transport
    public void requestDirectionDriving() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    //request the directions for public transport
    public void requestDirectionPublic() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(this);
    }

    //request the directions for personal transport, this time using a departure time set so that
    //the event will be reached on time
    public void requestDirectionDriving2() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .departureTime(depart)
                .execute(this);
    }

    //request the directions for public transport, this time using a departure time set so that
    //the event will be reached on time
    public void requestDirectionPublic2() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .departureTime(depart)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        String instruction;
        String duration;
        String distance;
        String minutes;
        String hours;

        //if this is the first request, all we want is to find the total duration of the route
        //then we can calculate the departure time accordingly and request the route again
        //using the departure time set so that the event is reached in time
        if(firstRequest) {
            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                Leg leg = route.getLegList().get(0);
                totalDuration = leg.getDuration().getText();

                //split up the totalDuration string
                StringTokenizer str = new StringTokenizer(totalDuration);
                hours = str.nextElement().toString();
                str.nextElement();
                if (str.hasMoreElements()) {
                    minutes = str.nextElement().toString();
                } else {
                    minutes = hours;
                    hours = "0";
                }

                int h = Integer.parseInt(hours);
                int m = Integer.parseInt(minutes);

                //get milliseconds
                h = h*3600000;
                m = m*60000;

                hm = h+m;

                //set departure time to event time-duration
                //divide by 1000 to get seconds for Google API
                date = (date - hm) /1000;

                //cast the date to long, so that it does not get put into scientific notation
                long d = (long) date;

                //get the value in a string, so that we can pass it to the API
                depart = Long.toString(d);

                //depending on car or public transport, request the route again
                if(car) {
                    firstRequest = false;
                    requestDirectionDriving2();
                } else if (pt) {
                    firstRequest = false;
                    requestDirectionPublic2();
                }
            }
            //on the second request we get the route and show it on the map
        } else if (!firstRequest) {
            //we only do something is the direction status returned by google is OK
            if (direction.isOK()) {
                //add markers on the origin and destination
                googleMap.addMarker(new MarkerOptions().position(origin));
                googleMap.addMarker(new MarkerOptions().position(destination));

                //add the red line that shows the route on the map
                ArrayList<LatLng> directionPositionList =
                        direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                googleMap.addPolyline(DirectionConverter.createPolyline
                        (this, directionPositionList, 5, Color.RED));

                //get the route, leg, and step
                Route route = direction.getRouteList().get(0);
                Leg leg = route.getLegList().get(0);
                Step step;

                //ArrayLists used to save information before displaying it in the layout
                ArrayList<String> instructions = new ArrayList<>();
                ArrayList<String> durations = new ArrayList<>();
                ArrayList<String> distances = new ArrayList<>();

                //add layouts
                layout = (LinearLayout) findViewById(R.id.info_layout);

                //requested a personal transport direction
                if (car) {
                    //for each step in the leg get the instructions
                    //strip some HTML tags from it and save it to the arraylist
                    //also save the distance and duration
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

                        //adding layouts to display the instructions
                        LinearLayout infoRow = new LinearLayout(this);

                        //every other row should have a different background color
                        if( i % 2 == 1) {
                            infoRow.setBackground(ContextCompat.getDrawable
                                    (this, R.drawable.gradient_light));
                        }

                        //textview that will display the duration of this instruction
                        TextView time = new TextView(this);
                        time.setText(durations.get(i));
                        time.setLayoutParams(new ViewGroup.LayoutParams
                                        (ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        time.setTextColor(getResources().getColor(R.color.colorGrey));
                        infoRow.addView(time);

                        //layout that will hold two textviews
                        LinearLayout inst = new LinearLayout(this);
                        inst.setPadding(20, 0, 0, 0);
                        inst.setOrientation(LinearLayout.VERTICAL);
                        inst.setGravity(Gravity.START);
                        inst.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        //textview that will display the instruction
                        TextView text = new TextView(this);
                        text.setText(instructions.get(i));
                        text.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setTextSize(16);
                        text.setTextColor(getResources().getColor(R.color.colorGrey));
                        inst.addView(text);

                        //textview that will display the distance of this instruction
                        TextView text2 = new TextView(this);
                        text2.setText(distances.get(i));
                        text2.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        text2.setTextColor(getResources().getColor(R.color.colorGrey));
                        inst.addView(text2);

                        //add the layouts to make them visible
                        infoRow.addView(inst);
                        layout.addView(infoRow);
                    }
                }

                //requested a public transport direction
                if (pt) {
                    StopPoint arrivalStopPoint;
                    TransitDetail transitDetail;
                    StopPoint departureStopPoint;
                    TimeInfo arriveTimeInfo;
                    TimeInfo departureTimeInfo;
                    Line transitLine;
                    String arrivalStopPointSTR = "";
                    String departureStopPointSTR = "";
                    String arrivalTime = "";
                    String departureTime = "";
                    String line = "";
                    String line2 = "";

                    //for each step see if the travel mode is walking or transit
                    //accordingly set values
                    for (int i = 0; i < leg.getStepList().size(); i++) {
                        step = leg.getStepList().get(i);

                        //travel mode is walking
                        if (step.getTravelMode().equals("WALKING")) {
                            //get HTML instructions and strip the tags
                            instruction = step.getHtmlInstruction();
                            instruction = instruction.replace("<b>", "");
                            instruction = instruction.replace("</b>", "");
                            instruction = instruction.replace("<div style=\"font-size:0.9em\">", "");
                            instruction = instruction.replace("</div>", "");

                            //add the instruction to the arraylist
                            instructions.add(instruction);

                            //add the distance
                            distance = step.getDistance().getText();
                            distances.add(distance);

                            //add the duration
                            duration = step.getDuration().getText();
                            durations.add(duration);
                        }

                        //travelmode is public transport
                        if (step.getTravelMode().equals("TRANSIT")) {
                            //get the transit information
                            transitDetail = step.getTransitDetail();

                            //get the arrival stop point, and set it
                            arrivalStopPoint = transitDetail.getArrivalStopPoint();
                            arrivalStopPointSTR = arrivalStopPoint.getName();

                            //get the departure stop point and set it
                            departureStopPoint = transitDetail.getDepartureStopPoint();
                            departureStopPointSTR = departureStopPoint.getName();

                            //get the arrival time and set it
                            arriveTimeInfo = transitDetail.getArrivalTime();
                            arrivalTime = arriveTimeInfo.getText();

                            //get the departure tiem and set it
                            departureTimeInfo = transitDetail.getDepartureTime();
                            departureTime = departureTimeInfo.getText();

                            //get the transit line and set it (together with its short name/number)
                            transitLine = transitDetail.getLine();
                            line = transitLine.getShortName();
                            line2 = transitLine.getName();

                            //add the instructions
                            instructions.add("Take bus/train " + line + " " + line2 + " to " +
                                    arrivalStopPointSTR + " at " + departureStopPointSTR + " at " +
                                    departureTime);
                            instructions.add("You will arrive at " + arrivalStopPointSTR + " at " +
                                    arrivalTime);
                        }

                        //add layouts
                        LinearLayout infoRow = new LinearLayout(this);

                        //textview that displays the duration
                        TextView time = new TextView(this);
                        time.setText(departureTime);
                        time.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        infoRow.addView(time);

                        //layout that holds the two textviews below
                        LinearLayout inst = new LinearLayout(this);
                        inst.setPadding(20, 0, 0, 0);
                        inst.setOrientation(LinearLayout.VERTICAL);
                        inst.setGravity(Gravity.START);
                        inst.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));

                        //textview that displays the instruction
                        TextView text = new TextView(this);
                        text.setText(instructions.get(i));
                        text.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setTextSize(16);
                        inst.addView(text);

                        //textview that displays the next instruction
                        TextView text2 = new TextView(this);
                        text2.setText(instructions.get(i+1));
                        text2.setLayoutParams(new ViewGroup.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                        inst.addView(text2);

                        //add the layouts to make them visible
                        infoRow.addView(inst);
                        layout.addView(infoRow);
                    }
                }
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //do nothing
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

    //Home-icon takes you back to MainActivity
    public void btn_home(View v) {
        Intent intent = new Intent(GoogleDirectionsActivity.this, MainActivity.class);
        if(usedLocation){
            MainActivity.alreadySearched = false;
            MainActivity.searchQuery = null;
        } else {
            MainActivity.alreadySearched = false;
            MainActivity.searchQuery = prevQuery;
        }
        startActivity(intent);
    }
}