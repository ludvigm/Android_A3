package com.example.ludvig.assignment3.RoadMap;

import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ludvig.assignment3.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class RoadMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final String vaxjoToStockholm = "http://cs.lnu.se/android/VaxjoToStockholm.kml";
    final String vaxjoToCopenHagen = "http://cs.lnu.se/android/VaxjoToCopenhagen.kml";
    final String vaxjoToOdessa = "http://cs.lnu.se/android/VaxjoToOdessa.kml";
    File dir;
    Polyline currentRoute;
    private final ArrayList<Marker> activeMarkers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmaps);
        dir = getExternalFilesDir(null);
        AsyncTask task = new FileRetriever().execute(vaxjoToStockholm, vaxjoToCopenHagen, vaxjoToOdessa);
        System.out.println("after asynctask.");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.road_map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File file;
        switch (item.getItemId()) {
            case R.id.roadmap_no_route_option:
                if(currentRoute != null) {
                    currentRoute.remove();
                }
                if(!activeMarkers.isEmpty()) {
                    clearMarkers();
                }
                return true;
            case R.id.roadmap_stockholm_route_option:
                file = new File(dir, "VaxjoToStockholm.kml");
                parseAndDisplayRoute(file,"Stockholm");
                return true;
            case R.id.roadmap_copenhagen_route_option:
                file = new File(dir, "VaxjoToCopenhagen.kml");
                parseAndDisplayRoute(file,"Copenhagen");
                return true;
            case R.id.roadmap_odessa_route_option:
                file = new File(dir, "VaxjoToOdessa.kml");
                parseAndDisplayRoute(file,"Odessa");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void parseAndDisplayRoute(File file, String cityName) {
        ArrayList<String> stringCoordinates = parseCoordinates(file);
        ArrayList<LatLng> latLngCoordinates = stringToLatLngCoordinates(stringCoordinates.get(0));
        if(currentRoute != null) {
            currentRoute.remove();
        }
        if(!activeMarkers.isEmpty()) {
            clearMarkers();
        }
        currentRoute = mMap.addPolyline(new PolylineOptions().addAll(latLngCoordinates));                         //Turns out addAll has way better performance than a bunch of implementations using a new thread etc i tried.
        LatLng city1 = addCityMarker(stringCoordinates.get(1),"Växjö");
        LatLng city2 = addCityMarker(stringCoordinates.get(2),cityName);
        setCameraPosition(city1, city2);
    }

    private void clearMarkers() {
        for(Iterator<Marker> iter = activeMarkers.iterator(); iter.hasNext();) {
            Marker m = iter.next();
            m.remove();
            iter.remove();
        }
    }

    /**
     * Method to parse kml file
     *
     * @param file .kml file to parse
     * @return Returns a list containing the intermediate coordinates, the start position and the end position at index 0,1,2 respectively.
     */
    private ArrayList<String> parseCoordinates(File file) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(file);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(inputStream, null);
            while (xmlPullParser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlResourceParser.START_TAG) {
                    if (xmlPullParser.getName().equals("coordinates")) {
                        list.add(xmlPullParser.nextText());
                    }
                }
                xmlPullParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This method turns the long string of intermediate coordinates into a linkedlist of LatLng positions.
     * First split string by spaces to divide into positions, then split by "," to get latitude,longitude,0.0 where 0.0 is ignored. (2D map)
     *
     * @param cords A string of intermediate coordinates from parsing the kml file
     * @return A linkedlist of LatLng positions.
     */
    private ArrayList<LatLng> stringToLatLngCoordinates(String cords) {
        ArrayList<LatLng> coordinates = new ArrayList<>();
        String[] cordEntries = cords.split("\\s+");
        for (String entry : cordEntries) {
            String[] split = entry.split(",");
            LatLng position = new LatLng(Double.valueOf(split[1]), Double.valueOf(split[0]));                //Create LatLng Position
            coordinates.add(position);                                                                          //add to list
        }
        return coordinates;
    }

    private LatLng addCityMarker(String cityCords, String cityName) {
        String[] split = cityCords.split(",");
        LatLng pos = new LatLng(Double.valueOf(split[1]), Double.valueOf(split[0]));
        activeMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(cityName)
                .position(pos)
        ));
        return pos;
    }

    private void setCameraPosition(LatLng pos1, LatLng pos2) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pos1);
        builder.include(pos2);
        for(LatLng point : currentRoute.getPoints()) {
            builder.include(point);
        }


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 125);
        mMap.moveCamera(cu);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private class FileRetriever extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {
            try {
                downloadFilesToSDCard(urls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(File result) {
        }

    }

    private void downloadFilesToSDCard(String[] fileURLs) throws IOException {
        for (String stringURL : fileURLs) {
            URL url = new URL(stringURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = stringURL.substring(stringURL.lastIndexOf("/") + 1,
                        stringURL.length());
                System.out.println("downloading file from: " + stringURL + " to file: " + fileName);
                FileUtils.copyURLToFile(url, new File(dir, fileName));
            }
        }
    }

}
