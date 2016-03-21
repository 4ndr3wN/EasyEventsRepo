package tue.easyevents;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayOptions();
////

        // Code for dropdowns, see settings_view.xml for instructions on how to use
        // or check: http://developer.android.com/guide/topics/ui/controls/spinner.html
        final Spinner spinner_time = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_time.setAdapter(adapter_time);

        final String time = spinner_time.getSelectedItem().toString();;
        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String FILENAME = "time_file";
                String string = time;

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

        Spinner spinner_range = (Spinner) findViewById(R.id.spinner_range);
        final String range = spinner_range.getSelectedItem().toString();
        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String FILENAME = "range_file";
                String string = range;

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

}