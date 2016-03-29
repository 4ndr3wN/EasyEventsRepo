package tue.easyevents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    public String myLocation;
    public String lat;
    public String lon;
    public String query;
    public static String geoCodedLocation;
    public String from;
    public String to;
    public static ArrayList<Event> events;
    public int range;
    private GoogleMap mMap;
    public String lastMarkerClicked;
    public static String searchQuery = null;
    public static boolean alreadySearched = false;
    public boolean usedLocation;
    static final String PREVIOUS_SEARCH = "prev_search";

    LocationManager locationManager;

    //Keep track of wether we actually have a user location.
    public boolean locationAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //USER LOCATION
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //TODO: take standard location from settings
            //TODO: OR if that is not there take the location of the searched city
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    //PARSE LOCATION TO USABLE FORMAT
                    lat = Double.toString(location.getLatitude());
                    lon = Double.toString(location.getLongitude());
                    locationAvailable = true;
                }
            } else{
                locationAvailable = false;
            }

        } else {
            //permission already granted
            //get location :)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                //PARSE LOCATION TO USABLE FORMAT
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());
                locationAvailable = true;
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Make drawer dark fading colour transparent
        drawer.setScrimColor(Color.TRANSPARENT);
        //dat go-up-pijltje eindelijk uitgezet, hoera!
        toolbar.setNavigationIcon(null);

        //ORIGINAL: R.id.nav_view, had to bechanged due to the new wrapping NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_itemlist);
        navigationView.setNavigationItemSelectedListener(this);



        //check for fileoutput files
        try {
            openFileInput("time_file");
        } catch (FileNotFoundException e) {
            //set basic settings
            String FILENAME = "time_file";
            String string = "Week";
            outputFile(FILENAME, string);
        }

        try {
            openFileInput("range_file");
        } catch (FileNotFoundException e) {
            //set basic settings
            String FILENAME = "range_file";
            String string = "10 km";
            outputFile(FILENAME, string);
        }

        try {
            openFileInput("address_file");
        } catch (FileNotFoundException e) {
            //set basic settings
            String FILENAME = "address_file";
            String string = "Den Dolech 2, Eindhoven";
            outputFile(FILENAME, string);

        }

        try {
            openFileInput("GPSCheck");
        } catch (FileNotFoundException e) {
            //set basic settings
            String FILENAME = "GPSCheck";
            String string = "1";
            outputFile(FILENAME, string);
        }

        //For searching from different screens we need to have four different cases.
        //First if there is no search query, no search done yet, and a GPS location available,
        // we search by GPS location
        if(searchQuery == null && locationAvailable && !alreadySearched) {
            usedLocation = true;
            search(true);
            alreadySearched = true;
        //Secondly, if there is a searchQuery and no search done yet, we search by the searchQuery
        } else if(searchQuery != null && !alreadySearched){
            usedLocation = false;
            query = searchQuery;
            search(false);
            alreadySearched = true;
        //Thirdly, if there is no searchQuery, no search done yet but no location available we
        // should search by saved location;
        } else if(searchQuery == null && !alreadySearched && !locationAvailable){
            try{
                query = openFileInput("address_file").toString();
            } catch (FileNotFoundException e) {
                query = "Amsterdam";
                e.printStackTrace();
            }
            search(false);

        }
        //Finally, if there has already been searched, we should not search again, so nothing happens

        //Check if this is the first time the user has opened the app
        try {
            openFileInput("FirstTimeStart");
        } catch (FileNotFoundException e) {
            //Create the FirstTimeStart file so the next time this error will not be thrown
            String FILENAME = "FirstTimeStart";
            String string = "Nope";
            outputFile(FILENAME, string);
            //Create the overlay manual
            //http://stackoverflow.com/questions/18476088/how-do-i-create-the-semi-transparent-grey-tutorial-overlay-in-android This seems useful

            errorMessage("This is the first time running the app!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    //set location to myLocation
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        //PARSE LOCATION TO USABLE FORMAT
                        lat = Double.toString(location.getLatitude());
                        lon = Double.toString(location.getLongitude());
                    }
                } else {
                    //permission not granted
                    //dont use user location :(
                    //TODO: take standard location from settings
                    //TODO: OR if that is not there take the location of the searched city
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Set the listener for the map markers
        mMap.setOnMarkerClickListener(this);
    }

    public void addMarker(Double latitude, Double longitude, String title, String id) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(id));
    }

    //Toggles the drawer, used on the side-bar-buttons
    public void btn_toggleDrawer(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) findViewById(R.id.search_events);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String userQuery) {
                //Clear lastMarkerClicked to prevent errors
                lastMarkerClicked = null;
                usedLocation = false;

                //set query for geocoding API
                query = userQuery;

                //call search function to handle searching and API calls
                search(false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onMarkerClick(final Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        marker.showInfoWindow();

        if (marker.getSnippet().equals(lastMarkerClicked)) {
            String idString = marker.getSnippet();
            int id = Integer.parseInt(idString);
            Intent intent = new Intent(MainActivity.this, DetailView_Activity.class);
            intent.putExtra("eventIndex", id);
            intent.putExtra("usedLocation", usedLocation);
            if(!usedLocation){
                intent.putExtra("query", query);
            }
            startActivity(intent);
            return true;
        } else {
            lastMarkerClicked = marker.getSnippet();
            return true;
        }
    }

    public void search(boolean userLoc) {
        //get the date
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //set public String from
        from = date;

        //timerange additions
        long unixTime = System.currentTimeMillis();
        long unixDay = 86400000;

        String saved_time = inputFile("time_file");

        if (saved_time.equals("Today")) {
            to = date;
        } else if (saved_time.equals("Weekend")) {
            unixTime = unixTime + (unixDay * 3);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        } else if (saved_time.equals("Week")) {
            unixTime = unixTime + (unixDay * 7);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        } else if (saved_time.equals("Month")) {
            unixTime = unixTime + (unixDay * 30);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        }

        String saved_range = inputFile("range_file");

        if (saved_range.equals("5 km")) {
            range = 5;
        } else if (saved_range.equals("10 km")) {
            range = 10;
        } else if (saved_range.equals("25 km")) {
            range = 25;
        } else if (saved_range.equals("50 km")) {
            range = 50;
        } else if (saved_range.equals("100 km")) {
            range = 100;
        }

        //call the APIs and onward
        try {
            if (userLoc) {
                new Search2().execute();
            } else {
                new Search().execute();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //Reads the input file with specified file_name
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
            return true;
        } else if (id == R.id.action_about_us) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.action_about_us_msg)
                    .setTitle(R.string.action_about_us_title)
                    .setPositiveButton(R.string.action_about_us_like, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            // FIRE DEM MISSILES! A.K.A LIKE OUR APP
                            }
                    }).setNegativeButton(R.string.action_about_us_cancel, new DialogInterface.OnClickListener() {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, DetailView_Activity.class);
        intent.putExtra("usedLocation", usedLocation);
        if(!usedLocation){
            intent.putExtra("query", query);
        }
        intent.putExtra("eventIndex", id);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //use an Asynchronous Task to do the GeoCodingAPI and EventfulAPI calls
    public class Search2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            geoCodedLocation = lat + "," + lon;

            try {
                events = EventfulAPI.searchEvents(geoCodedLocation, from, to, range);
                //Updating the UI cannot be done in the background, so we run on UI thread
                runOnUiThread(new Runnable() {
                @Override
                    public void run() {
                        StringTokenizer st = new StringTokenizer(geoCodedLocation, ",");
                        try{
                            String searchLatitudeString = st.nextElement().toString();
                            String searchLongitudeString = st.nextElement().toString();
                            Double searchLatitudeDouble = Double.valueOf(searchLatitudeString);
                            Double searchLongitudeDouble = Double.valueOf(searchLongitudeString);
                            LatLng zoomLL = new LatLng(searchLatitudeDouble, searchLongitudeDouble);
                            if (range == 5) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 13));
                            } else if(range == 10) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 11));
                            } else if(range == 25) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 9));
                            } else if(range == 50) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 8));
                            } else if(range == 100) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 7));
                            }
                        } catch(NoSuchElementException e){
                            e.printStackTrace();
                        }

                        NavigationView navView = (NavigationView) findViewById(R.id.nav_itemlist);
                        Menu m = navView.getMenu();
                        m.clear();
                        mMap.clear();
                        SubMenu topChannelMenu = m.addSubMenu("Events");
                        //Add all the events to the list
                        int i = 0;
                        while(i < events.size()){
                            String title = events.get(i).titleEvent;
                            Double latitude = Double.valueOf(events.get(i).eventLatitude);
                            Double longitude = Double.valueOf(events.get(i).eventLongitude);
                            String id = Integer.toString(i);
                            topChannelMenu.add(R.id.eventsGroup, i, Menu.NONE, title);
                            addMarker(latitude, longitude, title, id);

                            i = i+1;
                        }
                        //This is required to refresh the list view
                        MenuItem mi = m.getItem(m.size()-1);
                        mi.setTitle(mi.getTitle());
                    }
                });

            } catch (ConnectException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //use an Asynchronous Task to do the GeoCodingAPI and EventfulAPI calls
    public class Search extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                geoCodedLocation = GeoCodingAPI.geoCode(query);

                try {
                    events = EventfulAPI.searchEvents(geoCodedLocation, from, to, range);
                    //Updating the UI cannot be done in the background, so we run on UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringTokenizer st = new StringTokenizer(geoCodedLocation, ",");
                            try {
                                String searchLatitudeString = st.nextElement().toString();
                                String searchLongitudeString = st.nextElement().toString();
                                Double searchLatitudeDouble = Double.valueOf(searchLatitudeString);
                                Double searchLongitudeDouble = Double.valueOf(searchLongitudeString);
                                LatLng zoomLL = new LatLng(searchLatitudeDouble, searchLongitudeDouble);
                                if (range == 5) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 13));
                                } else if (range == 10) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 11));
                                } else if (range == 25) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 9));
                                } else if (range == 50) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 8));
                                } else if (range == 100) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 7));

                                }

                            } catch (NoSuchElementException e) {
                                e.printStackTrace();
                            }


                            NavigationView navView = (NavigationView) findViewById(R.id.nav_itemlist);
                            Menu m = navView.getMenu();
                            m.clear();
                            mMap.clear();
                            SubMenu topChannelMenu = m.addSubMenu("Events");
                            //Add all the events to the list
                            int i = 0;
                            while (i < events.size()) {
                                String title = events.get(i).titleEvent;
                                Double latitude = Double.valueOf(events.get(i).eventLatitude);
                                Double longitude = Double.valueOf(events.get(i).eventLongitude);
                                String id = Integer.toString(i);
                                topChannelMenu.add(R.id.eventsGroup, i, Menu.NONE, title);
                                addMarker(latitude, longitude, title, id);

                                i = i + 1;
                            }
                            //This is required to refresh the list view
                            MenuItem mi = m.getItem(m.size() - 1);
                            mi.setTitle(mi.getTitle());
                        }
                    });
                } catch (ConnectException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessage("Unfortunately something went wrong getting the events," +
                                    " please try again.");
                        }
                    });
                }
            } catch (ConnectException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessage("We could not get a geocoded location, please check your input.");
                    }
                });

            }
            return null;
        }
    }

    //Method to store a String locally on the phone. Used in onCreate in case certain files are
    // not created yet.
    public void outputFile(String file_name, String data){

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(file_name, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.write(data.getBytes());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void errorMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(R.string.error_title)
                .setPositiveButton(R.string.error_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE DEM MISSILES! A.K.A LIKE OUR APP
                    }
                });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        //Set AlertDialog background to our theme
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.pop_up);
        //Display Dialog
        dialog.show();
    }


}