package tue.easyevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Settings_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //go-up-pijltje die het doet ook al is parent niet specified
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //go-up-pijltje die het alleen doet als parent specified is
        //getSupportActionBar().setHomeButtonEnabled(true); //laat nu dus niets zien! Want is niet specified
        //go-up-pijltje veranderen in eigen drawable
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_arrow);
//        getSupportActionBar().setDisplayOptions();
////

        // Code for dropdowns, see settings_view.xml for instructions on how to use
        // or check: http://developer.android.com/guide/topics/ui/controls/spinner.html
        final Spinner spinner_time = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_time.setAdapter(adapter_time);

        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String FILENAME = "time_file";
                String string = spinner_time.getSelectedItem().toString();

                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(string.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        final Spinner spinner_range = (Spinner) findViewById(R.id.spinner_range);

        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String FILENAME = "range_file";
                String string = spinner_range.getSelectedItem().toString();

                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(string.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapter_range = ArrayAdapter.createFromResource(this,
                R.array.range_array, android.R.layout.simple_spinner_item);
        adapter_range.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_range.setAdapter(adapter_range);

    }


    /**
     * This method closes the settings activity and returns to the inital map
     * @param v
     */
    public void btn_close_settings(View v) {


        /**
         * Super class method back pressed, to get the original map activity back
         */
        super.onBackPressed();

        /**
         * New intent, in case we want to redraw the google map

            Intent intent = new Intent(Settings_Activity.this,MainActivity.class);
            startActivity(intent);
         */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
        if (id == R.id.action_detailview) {
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, DetailView_Activity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_ptview) {
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
            //Intent intent = new Intent(MainActivity.this,Settings_Activity.class);
            //
            Intent intent = new Intent(this, AboutUs_Activity.class);
            startActivity(intent);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}