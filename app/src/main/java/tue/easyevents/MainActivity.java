package tue.easyevents;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.ConnectException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {
            new getVenueRating().execute();
        } catch (Exception e) {
            System.err.println("Error finding events " + e);
        }

//        try {
//            new getEvents().execute();
//        } catch (Exception e) {
//            System.err.println("Error finding events " + e);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Make drawer dark fading colour transparent
        drawer.setScrimColor(Color.TRANSPARENT);

        //ORIGINAL: R.id.nav_view, had to bechanged due to the new wrapping NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_itemlist);
        navigationView.setNavigationItemSelectedListener(this);

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
        return true;
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
            //
            //Comment voor de software scientist Koen Verhaegh toevegoed.
            Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class getVenueRating extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){

            try {
                int test = FoursquareAPI.getRating("40.700,-73.999", "Michael's Food Cart");
            } catch (ConnectException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    public class getEvents extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params){
            try {
                ArrayList<Event> test = EventfulAPI.searchEvents("32.746682,-117.162741", "20160314", "20160320");
            } catch (ConnectException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
