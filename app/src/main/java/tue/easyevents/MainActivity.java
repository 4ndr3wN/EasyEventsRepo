package tue.easyevents;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Spinner;

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
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
            GoogleMap.OnMarkerClickListener {

    public String location;
    public String query;
    public static String geoCodedLocation;
    public String from;
    public String to;
    public static ArrayList<Event> events;
    public int range;
    private GoogleMap mMap;
    public String lastMarkerClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.write(string.getBytes());
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

        try {
            openFileInput("range_file");
        } catch (FileNotFoundException e) {
            //set basic settings
            String FILENAME = "range_file";
            String string = "10 km";

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.write(string.getBytes());
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

        //TODO: search met location van user??
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

                //call search function to handle searching and API calls
                search(userQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onMarkerClick(final Marker marker){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        marker.showInfoWindow();

        if(marker.getSnippet().equals(lastMarkerClicked)){
            String idString = marker.getSnippet();
            int id = Integer.parseInt(idString);
            Intent intent = new Intent(MainActivity.this, DetailView_Activity.class);
            intent.putExtra("eventIndex", id);
            startActivity(intent);
            return true;
        } else {
            lastMarkerClicked = marker.getSnippet();
            return true;
        }
    }

    public void search(String userQuery) {
        query = userQuery;

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
        }
        else if (saved_time.equals("Weekend")) {
            unixTime =  unixTime + (unixDay*3);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        }
        else if (saved_time.equals("Week")) {
            unixTime =  unixTime + (unixDay*7);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        }
        else if (saved_time.equals("Month")) {
            unixTime = unixTime + (unixDay * 30);
            String date2 = new SimpleDateFormat("yyyyMMdd").format(unixTime);
            to = date2;
        }

        String saved_range = inputFile("range_file");

        if (saved_range.equals("5 km")) {
            range = 5;
        }
        else if (saved_range.equals("10 km")) {
            range = 10;
        }
        else if (saved_range.equals("25 km")) {
            range = 25;
        }
        else if (saved_range.equals("50 km")) {
            range = 50;
        }
        else if (saved_range.equals("100 km")) {
            range = 100;
        }

        //geocode the users requested location
        try {
            new Search().execute();
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
        if (id == R.id.action_detailview) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, DetailView_Activity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_ptview) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, GoogleDirectionsActivity.class);
            //Bundle bundle = new Bundle();
            //bundle.putString("1", geoCodedLocation);
            //intent.putExtras(bundle);
            //intent.putExtra("1", geoCodedLocation);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, Settings_Activity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about_us) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, AboutUs_Activity.class);
            startActivity(intent);
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
        intent.putExtra("eventIndex", id);
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //use an Asynchronous Task to do the GeoCodingAPI and EventfulAPI calls
    //TODO: Map markers
    public class Search extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            try {
                geoCodedLocation = GeoCodingAPI.geoCode(query);

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
                                if(range == 5){
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 13));
                                } else if(range == 10){
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 11));
                                }else if(range == 25){
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 9));
                                }else if(range == 50){
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLL, 8));
                                }else if(range == 100){
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
            } catch (ConnectException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}

