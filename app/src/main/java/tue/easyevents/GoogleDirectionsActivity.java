package tue.easyevents;

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
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GoogleDirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyANaZSYC__XBBCPvkP8VGbdfW7OX86w9PQ";
    //change preferably according to center of origin and destination
    private LatLng camera = new LatLng(51.4, 5.79);
    //change according to origin for directions
    private LatLng origin = new LatLng(51.31, 6.09);
    //change according to destination (location of event)
    private LatLng destination = new LatLng(51.45, 5.49);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions_activity);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 9));

        requestDirection();
    }

    public void requestDirection() {
        Log.d("requestDirection", "HOI");
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
                //eventueel .execute(new DirectionCallback() bla bla not sure
                // nieuwe key testen?
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        String testStatus;
        Log.d("onDirectionSuccess", "HOI2");
        testStatus = direction.getStatus();
        Log.d("TestStatus", testStatus);
        if (direction.isOK()) {
            Log.d("direction.isOK", "OK!");
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //nog niks
        Log.d("KAPOT", "WAAROM WERKT HET NIET");
    }
}
