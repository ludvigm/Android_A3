package com.example.ludvig.assignment3.weatherWidget;

import android.app.Activity;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;

import com.example.ludvig.assignment3.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class WidgetConfiguration extends ListActivity {

    int widgetID;
    List<String> cities = new ArrayList<>(Arrays.asList("Växjö", "Malmö", "Stockholm", "Kiruna"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("widgetConfig");
        setContentView(R.layout.activity_widget_configuration);

        setResult(RESULT_CANCELED);     //Dont add widget if user cancels the config activity(this one)


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,cities);
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startWidget(cities.get(i));
            }
        });
    }

    private void startWidget(String city) {
        saveIdCityPair(city);
        Intent intent = new Intent(this,UpdateWeatherWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetID});
        intent.setAction(Const.UPDATE_WIDGET);
        startService(intent);
        finishAndReturn();
    }


    private void saveIdCityPair(String city) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(String.valueOf(widgetID),city);
        System.out.println("put string : " + city + " to id: " + widgetID);
        editor.apply();
    }

    private void finishAndReturn() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
