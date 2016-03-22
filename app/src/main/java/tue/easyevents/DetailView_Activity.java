package tue.easyevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailView_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Get the intent from the MainActivity, the int sent along with it is the index of the
        //requested event
        Intent intent = getIntent();
        int eventIndex = intent.getIntExtra("eventIndex", 0);
        //This is just for testing, but can be changed to actually add functionality to detailView
        String title = MainActivity.events.get(eventIndex).titleEvent;
        String country = MainActivity.events.get(eventIndex).countryEvent;
    }


    public void btn_close_detailview(View v) {
        /**
         * Super class method back pressed, to get the original map activity back
         *
         * Need to change the onclick in the layout to refer to a different name.
         */
        super.onBackPressed();

    }



    }
