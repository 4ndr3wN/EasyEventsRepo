package tue.easyevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


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
        //getSupportActionBar().setDisplayOptions();


        // Code for dropdowns, see settings_view.xml for instructions on how to use
        // or check: http://developer.android.com/guide/topics/ui/controls/spinner.html
        final Spinner spinner_time = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_time.setAdapter(adapter_time);
        String data = inputFile("time_file");
        if(data.equals("Today")){
            spinner_time.setSelection(0);
        } else if(data.equals("Weekend")){
            spinner_time.setSelection(1);
        } else if(data.equals("Week")){
            spinner_time.setSelection(2);
        } else if(data.equals("Month")){
            spinner_time.setSelection(3);
        }

        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                outputFile("time_file", spinner_time.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        final Spinner spinner_range = (Spinner) findViewById(R.id.spinner_range);
        ArrayAdapter<CharSequence> adapter_range = ArrayAdapter.createFromResource(this,
                R.array.range_array, android.R.layout.simple_spinner_item);
        adapter_range.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_range.setAdapter(adapter_range);
        data = inputFile("range_file");
        if(data.equals("5 km")){
            spinner_range.setSelection(0);
        } else if(data.equals("10 km")){
            spinner_range.setSelection(1);
        } else if(data.equals("25 km")){
            spinner_range.setSelection(2);
        } else if(data.equals("50 km")){
            spinner_range.setSelection(3);
        } else if(data.equals("100 km")) {
            spinner_range.setSelection(4);
        }

        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                outputFile("range_file", spinner_range.getSelectedItem().toString());
            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        data = inputFile("address_file");
        final EditText addressText = (EditText) findViewById(R.id.editText_Home);
        addressText.setHint(data);

        Button saveButton = (Button) findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAddress = addressText.getText().toString();
                if(!(newAddress.equals(""))){
                    outputFile("address_file", newAddress);
                    addressText.setHint(newAddress);
                    addressText.setText("");
                }
            }
        });

        data = inputFile("GPSCheck");
        CheckBox gpsCheck = (CheckBox) findViewById(R.id.checkBox_GPS);
        if(data.equals("1")){
            gpsCheck.setChecked(true);
        } else{
            gpsCheck.setChecked(false);
        }

        gpsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    outputFile("GPSCheck", "1");
                } else{
                    outputFile("GPSCheck", "0");
                }

            }
        });



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

    //Home-icon takes you back to MainActivity
    public void btn_home(View v) {
        super.onBackPressed();
    }

}