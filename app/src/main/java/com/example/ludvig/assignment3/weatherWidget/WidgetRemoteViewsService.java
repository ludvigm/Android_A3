package com.example.ludvig.assignment3.weatherWidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Ludvig on 10/4/2016.
 */
public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetListFactory(this.getApplicationContext(), intent));
    }
}
