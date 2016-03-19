package tue.easyevents;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Settings_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayOptions();
////

        // Code for dropdowns, see settings_view.xml for instructions on how to use
        // or check: http://developer.android.com/guide/topics/ui/controls/spinner.html
        Spinner spinner_time = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_time.setAdapter(adapter_time);

        Spinner spinner_range = (Spinner) findViewById(R.id.spinner_range);
        ArrayAdapter<CharSequence> adapter_range = ArrayAdapter.createFromResource(this,
                R.array.range_array, android.R.layout.simple_spinner_item);
        adapter_range.setDropDownViewResource(R.layout.dropdown_layouts);
        spinner_range.setAdapter(adapter_range);

    }

}
