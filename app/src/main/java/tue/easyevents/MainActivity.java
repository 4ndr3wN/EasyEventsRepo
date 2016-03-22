package tue.easyevents;

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

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String location;
    public String query;
    public static String geoCodedLocation;
    public String from;
    public String to;
    public ArrayList<Event> events;
    public int range;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                fos.write(string.getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                fos.close();
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
                fos.write(string.getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //TODO: search met location van user??
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
                //Log.d("test", query); //test

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
                    buffer.append(read + "\n" );
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

        //original menu in the drawer ALSO COMMENTED IN menu/activity_main_drawer.xml
        /*if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        /**
         * TESTING DRAWER GROUPS
         */

        if (id == R.id.group01) {

            System.out.println("CLICKED ON GROUP 1");

        } else if (id == R.id.group02) {

        } else if (id == R.id.group03) {

        } else if (id == R.id.group04) {

        } else if (id == R.id.group05) {

        } else if (id == R.id.group06) {

        } else if (id == R.id.group07) {

        } else if (id == R.id.group08) {

        } else if (id == R.id.group09) {

        } else if (id == R.id.group10) {

        }

        /**
         * TESTING DRAWER EVENT ITEMS
         */
        if (id == R.id.event01) {

            System.out.println("CLICKED ON ITEM 1");

        } else if (id == R.id.event02) {

        } else if (id == R.id.event03) {

        } else if (id == R.id.event04) {

        } else if (id == R.id.event05) {

        } else if (id == R.id.event06) {

        } else if (id == R.id.event07) {

        } else if (id == R.id.event08) {

        } else if (id == R.id.event09) {

        } else if (id == R.id.group10) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //use an Asynchronous Task to do the GeoCodingAPI call
    //TODO: runOnUiThread
    public class Search extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            try {
                geoCodedLocation = GeoCodingAPI.geoCode(query);

                try {
                    events = EventfulAPI.searchEvents(geoCodedLocation, from, to, range);

                    runOnUiThread(new Runnable() {
                    @Override
                        public void run() {
                            //doe functionaliteit op UI thread hier
                            //vul de list view
                            NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
                            Menu m = navView.getMenu();
                            SubMenu topChannelMenu = m.addSubMenu("Events");
                            topChannelMenu.add("Event 1");
                            topChannelMenu.add("Event 2");
                            topChannelMenu.add("Event 3");

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

