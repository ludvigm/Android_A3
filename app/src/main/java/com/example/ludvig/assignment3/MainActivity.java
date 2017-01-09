package com.example.ludvig.assignment3;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.ludvig.assignment3.CallHistory.CallHistoryMain;
import com.example.ludvig.assignment3.CityMap.MapsActivity;
import com.example.ludvig.assignment3.RoadMap.RoadMapActivity;
import com.example.ludvig.assignment3.weatherWidget.WidgetMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> l = new ArrayList(Arrays.asList("WeatherWidget", "Call History", "City Map","Road Map"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(adapterView.getContext(), WidgetMain.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(adapterView.getContext(), CallHistoryMain.class);
                        startActivity(intent);
                        break;

                    case 2:
                        intent = new Intent(adapterView.getContext(), MapsActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(adapterView.getContext(), RoadMapActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        System.out.println("?");
                }
            }
        });
    }

}
