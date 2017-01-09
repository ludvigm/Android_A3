package com.example.ludvig.assignment3.weatherWidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ludvig.assignment3.R;
import com.example.ludvig.assignment3.weatherWidget.weatherReport.VaxjoWeather;
import com.example.ludvig.assignment3.weatherWidget.weatherReport.WeatherForecast;
import com.example.ludvig.assignment3.weatherWidget.weatherReport.WeatherHandler;
import com.example.ludvig.assignment3.weatherWidget.weatherReport.WeatherReport;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ludvig on 10/3/2016.
 */
/*
    Factory class
     */

public class WidgetListFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<WeatherForecast> list;
    private int appWidgetId;
    private Context context;
    private URL url;
    private boolean hasInternet;

    public WidgetListFactory(Context context, Intent intent) {
        super();
        list = new ArrayList<>();
        this.context = context;
        appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        String city = intent.getStringExtra("city");
        hasInternet = intent.getBooleanExtra("internet", true);
        if (city != null) {
            try {
                url = new URL(getUrlByCity(city));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getUrlByCity(String city) {
        if (city != null) {
            switch (city) {
                case "Växjö":
                    return Const.URL_VAXJO_WEATHER;
                case "Malmö":
                    return Const.URL_MALMO_WEATHER;
                case "Stockholm":
                    return Const.URL_STHLM_WEATHER;
                case "Kiruna":
                    return Const.URL_KIRUNA_WEATHER;
                default:
                    return Const.URL_VAXJO_WEATHER;
            }
        }
        return Const.URL_VAXJO_WEATHER;
    }

    private void populateList() {
        if (hasInternet) {
            if (url != null) {
                AsyncTask task = new WeatherRetriever().execute(url);
            } else {
                System.out.println("null url..");
                try {
                    AsyncTask task = new WeatherRetriever().execute(new URL(Const.URL_VAXJO_WEATHER));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(context, "No internet connection available..", Toast.LENGTH_SHORT).show();
        }
    }

    private class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {
        protected WeatherReport doInBackground(URL... urls) {
            try {
                return WeatherHandler.getWeatherReport(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(WeatherReport result) {
            list.addAll(result.getForecasts());
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
        }

    }

    @Override
    public void onCreate() {
        System.out.println("oncreate in listprovider fetching data...");
        populateList();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        System.out.println("ondestroy, list clear.");
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget_row);
        WeatherForecast forecast = list.get(position);

        remoteViews.setTextViewText(R.id.widget_listitem_time, forecast.getStartHHMM() + " - " + forecast.getEndHHMM());
        remoteViews.setImageViewResource(R.id.widget_listitem_icon, VaxjoWeather.getIconID(forecast.getPeriodCode(), forecast.getWeatherCode()));
        remoteViews.setTextViewText(R.id.widget_listitem_rain, "Rain: " + forecast.getRain() + "mm/h");
        remoteViews.setTextViewText(R.id.widget_listitem_temperature, "Temp: " + forecast.getTemperature() + "\u00B0");

        return remoteViews;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
