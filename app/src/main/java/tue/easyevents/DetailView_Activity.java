package tue.easyevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailView_Activity extends AppCompatActivity {
    public Event detailEvent;
    public String latitude;
    public String longitude;
    public String eventLat;
    public String eventLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get the intent from the MainActivity, the int sent along with it is the index of the
        //requested event
        Intent intent = getIntent();
        int eventIndex = intent.getIntExtra("eventIndex", 0);
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("lon");
        //Store the required event as detailEvent to save a lot of typing
        detailEvent = MainActivity.events.get(eventIndex);

        //Check if we have a picture available, if so, set the new picture
        if(!(detailEvent.pictureEvent.equals("mipmap-xxxhdpi/ic_launcher.png"))){
            new DownloadImageTask((ImageView) findViewById(R.id.image_event))
                    .execute(detailEvent.pictureEvent);
        }

        //Get the ratings for this event
        try{
            new updateEventRating().execute();
        } catch (Exception e){
            e.printStackTrace();
        }

        //Find all the textViews so we can alter their text
        TextView titleView = (TextView) findViewById(R.id.event_name);
        TextView dateView = (TextView) findViewById(R.id.event_date);
        TextView addressView = (TextView) findViewById(R.id.event_address);
        TextView informationView = (TextView) findViewById(R.id.information_event);
        TextView cityView = (TextView) findViewById(R.id.event_city);
        TextView countryView = (TextView) findViewById(R.id.event_country);
        TextView venueView = (TextView) findViewById(R.id.venue_name);

        //Just setting textViews to their proper values
        titleView.setText(detailEvent.titleEvent);
        final Date dateEvent = new Date(detailEvent.dateEvent);
        final String dateString = new SimpleDateFormat("dd'-'MM'-'yyyy HH:mm").format(dateEvent);
        dateView.setText(dateString);
        addressView.setText(detailEvent.addressEvent);
        cityView.setText(detailEvent.cityEvent);
        countryView.setText(detailEvent.countryEvent);
        venueView.setText(detailEvent.venueEvent);
        if(!(detailEvent.infoEvent.equals(""))){
            informationView.setText(detailEvent.infoEvent);
        }

        //Add functionality to the buttons
        Button ticketButton = (Button) findViewById(R.id.btn_tickets);
        Button routeButton = (Button) findViewById(R.id.btn_detail_route);
        final String ticketURL;
        //Here we create the ticket URL. If there is one available we take that one
        if(detailEvent.ticketAvailable){
            ticketURL = detailEvent.ticketEvent;
            //Else we create a url that Googles the event for you.
        } else{
            String eventSearchString = detailEvent.titleEvent + ", " + dateString + ", "
                    + detailEvent.cityEvent;
            try {
                eventSearchString = URLEncoder.encode(eventSearchString, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ticketURL = "http://lmgtfy.com/?q=" + eventSearchString;

        }
        //Actually add the function to open a URL to the ticketButton
        ticketButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                goToUrl(ticketURL);
            }
        });

        routeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                eventLat = detailEvent.eventLatitude;
                eventLong = detailEvent.eventLongitude;
                Intent intent = new Intent(DetailView_Activity.this, GoogleDirectionsActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                intent.putExtra("eventLat", eventLat);
                intent.putExtra("eventLong", eventLong);
                Date date = new Date(detailEvent.dateEvent);
                double d = date.getTime();
                intent.putExtra("date", d);
                intent.putExtra("title", detailEvent.titleEvent);

                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }


    public void btn_close_detailview(View v) {
        /**
         * Super class method back pressed, to get the original map activity back
         *
         * Need to change the onclick in the layout to refer to a different name.
         */
        super.onBackPressed();

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

                Intent intent = new Intent(DetailView_Activity.this, MainActivity.class);
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

    /*      hele @Override van onOptionsItemSelected is gekopieerd uit MainActivity.java
            voor alle 'submenus', i.e. alle behalve de MainActivity, is het van belang
            om de huidige Activity the sluiten met finish(). Op deze manier ga je met de
            back-button of close-button terug naar de map, i.p.v. naar de 'previous' activity.
      */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ptview) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, GoogleDirectionsActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, Settings_Activity.class);
            startActivity(intent);
            finish();
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


    public class updateEventRating extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params){
            try{
                String latLong = detailEvent.eventLatitude + "," + detailEvent.eventLongitude;
                int foursquareRating = FoursquareAPI.getRating(latLong, detailEvent.venueEvent);
                int openWeatherRating = OpenWeatherAPI.searchWeather(detailEvent.eventLatitude,
                        detailEvent.eventLongitude, detailEvent.dateEvent);

                detailEvent.updateRatingWeather(openWeatherRating);
                detailEvent.updateRatingVenue(foursquareRating);

                //We need to update the ratings here, as otherwise the AsyncTask will not have
                // completed before we update the ratingBars
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Use the ratings to set the ratingBars
                        RatingBar averageRating = (RatingBar) findViewById(R.id.rating_overall);
                        RatingBar weatherRating = (RatingBar) findViewById(R.id.rating_weather);
                        RatingBar venueRating = (RatingBar) findViewById(R.id.rating_foursquare);
                        int venueRatingInt;

                        if(detailEvent.ratingVenue<=5 && detailEvent.ratingVenue>=0){
                            venueRating.setRating(detailEvent.ratingVenue);
                            venueRatingInt = detailEvent.ratingVenue;
                        } else if(detailEvent.ratingVenue<0){
                            venueRating.setRating(0);
                            venueRatingInt = 0;
                        } else {
                            venueRating.setRating(5);
                            venueRatingInt = 5;
                        }
                        weatherRating.setRating(detailEvent.ratingWeather);
                        int overallRating = (int)((double)((detailEvent.ratingWeather+venueRatingInt))/2);
                        averageRating.setRating(overallRating);
                    }
                });

            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    //Home-icon takes you back to MainActivity
    public void btn_home(View v) {
        super.onBackPressed();
    }

}