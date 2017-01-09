package com.example.ludvig.assignment3.weatherWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ludvig.assignment3.R;

/**
 * Created by Ludvig on 9/29/2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, UpdateWeatherWidgetService.class);
        intent.setAction(Const.UPDATE_WIDGET);
        intent.putExtra(appWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
        context.startService(intent);
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }
}
