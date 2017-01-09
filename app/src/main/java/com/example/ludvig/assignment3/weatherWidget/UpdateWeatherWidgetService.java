package com.example.ludvig.assignment3.weatherWidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;


import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ludvig.assignment3.R;
import com.example.ludvig.assignment3.weatherWidget.weatherReport.VaxjoWeather;

/**
 * Created by Ludvig on 9/29/2016.
 */
public class UpdateWeatherWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Const.WIDGET_CLICKED)) {
                    Intent weatherActivityIntent = new Intent(this, VaxjoWeather.class);
                    weatherActivityIntent.putExtra("city", intent.getStringExtra("city"));
                    weatherActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(weatherActivityIntent);
                } else if (action.equals(Const.UPDATE_WIDGET)) {
                    int[] ids = intent.getIntArrayExtra(manager.EXTRA_APPWIDGET_IDS);
                    for (int i = 0; i < ids.length; i++) {
                        System.out.println("updating id: " + ids[i]);
                        manager.updateAppWidget(ids[i], createNewViews(this, ids[i]));
                    }
                } else if (action.equals(Const.BUTTON_CLICKED)) {
                    if (isNetworkAvailable()) {
                        manager.notifyAppWidgetViewDataChanged(intent.getIntExtra("id", 0), R.id.widget_listview);
                    } else {
                        Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
        stopSelf();
        return START_STICKY;
    }

    public RemoteViews createNewViews(Context context, int id) {
        System.out.println("in createNewViews...");
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.weather_widget);

        String city = getCityFromPrefs(id);
        System.out.println("City after getting from prefs: " + city);

        views.setOnClickPendingIntent(R.id.weather_widget_layout, createServicePendingIntent(context, id, Const.WIDGET_CLICKED, city));
        views.setOnClickPendingIntent(R.id.button_1, createServicePendingIntent(context, id, Const.BUTTON_CLICKED, city));

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        if (city != null) {
            intent.putExtra("city", city);
        }
        intent.setData(Uri.fromParts("content", String.valueOf(id), null));
        views.setRemoteAdapter(R.id.widget_listview, intent);
        views.setEmptyView(R.id.widget_listview, R.id.list_empty);

        if (city != null) {
            views.setTextViewText(R.id.weather_widget_city_text, city);
        }

        return views;
    }

    private String getCityFromPrefs(int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String city = prefs.getString(String.valueOf(id), "Växjö");
        System.out.println("Tried to fetch city at id: " + id + ". Result: " + city);
        return city;
    }

    private PendingIntent createServicePendingIntent(Context context, int id, String action, String city) {
        Intent intent = new Intent(context, UpdateWeatherWidgetService.class);
        intent.putExtra("city", city);
        intent.putExtra("id", id);
        intent.setAction(action);
        //Using ID as requestcode..
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}


