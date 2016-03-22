package tue.easyevents;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class GoogleDirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyDc-zOLX34KkiohWYfd0JOmlfbEGv2VT9M";
    //change preferably according to center of origin and destination
    private LatLng camera = new LatLng(51.4, 5.79);
    //change according to origin for directions
    private LatLng origin;
    //change according to destination (location of event)
    private LatLng destination = new LatLng(51.31, 6.09);

    String latitude;
    String longitude;
    double la;
    double lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = getIntent();
        //String parameters = intent.getExtras().getString("1");
        //Log.d("Parameters", parameters);
        Log.d("test", "test");
        String loc = MainActivity.geoCodedLocation;
        //split geoCodedLocation
        StringTokenizer st = new StringTokenizer(loc, ",");
        latitude = st.nextElement().toString();
        longitude = st.nextElement().toString();

        Log.d("Lat", latitude);
        Log.d("Long", longitude);

        //Parse strings to double
        la = Double.parseDouble(latitude);
        lo = Double.parseDouble(longitude);

        //set destination LatLng
        origin = new LatLng(la, lo);

        //TODO: fix origin???
        //TODO: Set destination according to event location
        //TODO: Check settings for the checkbox/address


        setContentView(R.layout.directions_activity);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 9));

        requestDirectionDriving();
    }

    public void requestDirectionDriving() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    public void requestDirectionPublic() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        String testStatus;
        testStatus = direction.getStatus();
        Log.d("TestStatus", testStatus);
        if (direction.isOK()) {
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));

            Route route = direction.getRouteList().get(0);
            Leg leg = route.getLegList().get(0);
            Step step = leg.getStepList().get(0);
            Info infoDuration = leg.getDuration();
            Info infoDistance = leg.getDistance();
            String duration = infoDuration.getText();
            String distance = infoDistance.getText();

            String instruction = step.getHtmlInstruction();
            Log.d("inst", instruction);

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //nog niks
        Log.d("KAPOT", "WAAROM WERKT HET NIET");
    }
}
