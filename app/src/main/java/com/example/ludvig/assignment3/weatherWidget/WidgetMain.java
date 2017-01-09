package com.example.ludvig.assignment3.weatherWidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WidgetMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER));
       // setContentView(R.layout.activity_weather_widget);

        Intent intent = new Intent(this,WidgetProvider.class);
        intent.setAction(Const.UPDATE_WIDGET);
        sendBroadcast(intent);
        System.out.println("sent update..?");
    }
}
