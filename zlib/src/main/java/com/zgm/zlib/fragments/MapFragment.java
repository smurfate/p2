package com.zgm.zlib.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zgm.zlib.R;
import com.zgm.zlib.directions.model.Direction;
import com.zgm.zlib.directions.model.Leg;
import com.zgm.zlib.directions.model.Route;
import com.zgm.zlib.directions.util.DirectionConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private final String TAG = "Debug:MapFragment";

    private SupportMapFragment mapFragment = null;
    private GoogleMap map;
    private Marker myLocationMarker;
    private LatLng startupLocation = new LatLng(33.5138,36.2765);
    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;


    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapFragment = SupportMapFragment.newInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Log.e(TAG, "GPS is disabled");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frmMap, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (map != null) {
                    if (myLocationMarker != null) myLocationMarker.remove();
                    myLocationMarker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setInfoWindowAdapter(this);

        mListener.onMapReadyCallback(map);

    }

    /**
     * This function return the current location either from GPS device or network provider
     * @param provider can take LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER
     * @param errorResId integer value to identify the request in case of failier, the failier can be caused by either permission problem or disabled device
     * @return current location
     */

    public Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (locationManager.isProviderEnabled(provider) &&
                !(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            locationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, locationListener);
            location = locationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(getActivity(), errorResId, Toast.LENGTH_LONG).show();
        }
        return location;
    }

    /**
     * this function calculate the DIRECT distance between two locations, it is pure mathmatics
     * @param StartP first point
     * @param EndP second point
     * @return the distance in KM
     */
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    /**
     * this function together with the locationListener show current device location on the map using gps provider
     */
    public void showMyLocation()
    {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Log.e(TAG, "No permission for Location!");
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void moveToLocation(LatLng location)
    {
        if(map == null) return;
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(location)
                .zoom(10)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    public void moveToLocation(LatLng location, int zoom)
    {
        if(map == null) return;
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(location)
                .zoom(zoom)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    public void setMapSettings(boolean allGestures,boolean myLocation, boolean zoomButton, boolean mapBar, boolean showCompass)
    {
        if(map == null)
        {
            Log.e(TAG, "the map is null");
            return;
        }
        UiSettings settings = map.getUiSettings();
        settings.setAllGesturesEnabled(allGestures);
        settings.setMyLocationButtonEnabled(myLocation);
        settings.setZoomControlsEnabled(zoomButton);
        settings.setMapToolbarEnabled(mapBar);
        settings.setCompassEnabled(showCompass);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomGesturesEnabled(true);
    }

    public Marker addMarker(double lat, double lng, String title, int iconId)
    {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconId);
        return map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(bitmap).title(title));
    }

    public Marker addMarker(double lat, double lng, String title)
    {
        return map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title));
    }

    public Marker addMarker(double lat, double lng)
    {
        return map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
    }

    public void addMarker(List<LatLng> points)
    {
        for(int i = 0; i<points.size(); i++)
        {
            map.addMarker(new MarkerOptions().position(points.get(i)));
        }
    }

    public void drawDirection(Direction direction)
    {

        if(direction.isOK())
        {
            for(Route route : direction.getRouteList())
            {
                for(Leg leg:route.getLegList())
                {
                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                    map.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                }
            }
        }
        else
        {
            Log.e(TAG,"Something wrong with the direction");
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return mListener.infoWindow(marker);
    }


    public interface OnFragmentInteractionListener {

        void onMapReadyCallback(GoogleMap map);

        View infoWindow(Marker marker);
    }
}
