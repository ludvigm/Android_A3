/**
 * VaxjoWeather.java
 * Created: May 9, 2010
 * Jonas Lundberg, LnU
 */

package com.example.ludvig.assignment3.weatherWidget.weatherReport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ludvig.assignment3.R;
import com.example.ludvig.assignment3.weatherWidget.Const;

/**
 * This is a first prototype for a weather app. It is currently 
 * only downloading weather data for Växjö. 
 * 
 * This activity downloads weather data and constructs a WeatherReport,
 * a data structure containing weather data for a number of periods ahead.
 * 
 * The WeatherHandler is a SAX parser for the weather reports 
 * (forecast.xml) produced by www.yr.no. The handler constructs
 * a WeatherReport containing meta data for a given location
 * (e.g. city, country, last updated, next update) and a sequence 
 * of WeatherForecasts.
 * Each WeatherForecast represents a forecast (weather, rain, wind, etc)
 * for a given time period.
 * 
 * The next task is to construct a list based GUI where each row 
 * displays the weather data for a single period.
 * 
 *  
 * @author jlnmsi
 *
 */

public class VaxjoWeather extends AppCompatActivity {
	public static String TAG = "dv606.weather";


	private WeatherReport report = null;
	MultiAdapter listAdapter;
	private String city;
	private URL url;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        setContentView(R.layout.weather_main);
		// Initialize the toolbar
		city = getIntent().getStringExtra("city");
		System.out.println("in vaxjoWeather city=: " + city);
		final ListView weatherListView = (ListView) findViewById(R.id.weather_listview);

		ArrayList<WeatherForecast> forecasts = new ArrayList<>();
		int rowLayoutID = R.layout.weather_forecast_row;
		listAdapter = new MultiAdapter(this,rowLayoutID,forecasts);
		weatherListView.setAdapter(listAdapter);
		getWeather();
		final Button updateWeather = (Button) findViewById(R.id.weather_button);
		updateWeather.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateWeatherData();
			}
		});
    }

	//Button to get weather for växjö..
	public void getWeather() {
		//If network is down show toast..
		if(!isNetworkAvailable()) {
			Toast.makeText(this,"No internet connection, cannot get new weather data.", Toast.LENGTH_SHORT).show();

		} else {		//Else get new weather info
			//Clear adapter first.
			listAdapter.clear();
			//Your code as usual to retrieve weather info..
			try {
				if(city != null) {
					switch(city) {
						case "Växjö":
							url = new URL(Const.URL_VAXJO_WEATHER);
							break;
						case "Malmö":
							url = new URL(Const.URL_MALMO_WEATHER);
							break;
						case "Stockholm":
							url = new URL(Const.URL_STHLM_WEATHER);
							break;
						case "Kiruna":
							url = new URL(Const.URL_KIRUNA_WEATHER);
							break;
						default: url = new URL(Const.URL_VAXJO_WEATHER);
					}
				} else {
					url = new URL("http://www.yr.no/sted/Sverige/Kronoberg/V%E4xj%F6/forecast.xml");
				}

				AsyncTask task = new WeatherRetriever().execute(url);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void updateWeatherData() {
		if(!isNetworkAvailable()) {
			Toast.makeText(this,"No internet connection, cannot get new weather data.", Toast.LENGTH_SHORT).show();
		} else {
			listAdapter.clear();
			if(url != null) {
				AsyncTask task = new WeatherRetriever().execute(url);
			} else {
				try {
					url = new URL(Const.URL_VAXJO_WEATHER);
				} catch(MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather_menu, menu);
        return true;
    }
    
    private void printReportToLog() {
    	if (this.report != null) {

        	/* Print location meta data */
			Log.i(TAG, report.toString());

        	/* Print forecasts */
    		int count = 0;
    		for (WeatherForecast forecast : report) {
    			count++;
				Log.i(TAG, "Forecast #" + count);
				Log.i(TAG, forecast.toString());
    			//System.out.println("Forecast "+count);
    			//System.out.println( forecast.toString() );
    		}
    	}
    	else {
    		Log.e(TAG, "Weather report has not been loaded.");
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
			//Toast.makeText(getApplicationContext(), "WeatherRetriever task finished", Toast.LENGTH_LONG).show();
    		report = result;
			//printReportToLog();
			List l = result.getForecasts();
			listAdapter.addAll(l);
			setMetaData(result);
    	}
    }

	private void setMetaData(WeatherReport report) {
		TextView location = (TextView) findViewById(R.id.meta_location_textview);
		TextView altLatLong = (TextView) findViewById(R.id.meta_alt_lat_long_textview);
		TextView lastUpdated = (TextView) findViewById(R.id.meta_last_update_textview);
		TextView nextUpdate =(TextView) findViewById(R.id.meta_next_update_textview);

		location.setText("Location: "+report.getCity() + ", " + report.getCountry());
		altLatLong.setText("Alt: " + report.getAltitude()+", Lat: "+report.getLatitude()+", Long: " +report.getLongitude());
		lastUpdated.setText("Last update: "+report.getLastUpdated());
		nextUpdate.setText("Next update: "+report.getNextUpdate());
	}

	//Returns the ID/path to the correct icon according to time-period and weather code.
	//Period: 0=00-06, 1=06-12, 2=12-18, 3=18-24
	//I am unsure of what to count as nighttime or daytime. I will stick to nighttime symbols for 00-06, and the rest daytime.
	//I limit myself to implementing the three first weatherCodes with day + night icons. I skip "mørketid" icon. After these
	//Three codes i say the rest is raining. If the LNU symbol gets chosen something went wrong.
	public static int getIconID(int periodCode, int weatherCode) {
		//Over 4 == rain..
		if(weatherCode>4) {
			return R.drawable.weather_rain;
		} else {
			switch(weatherCode) {
				case 1:
					if(periodCode==1 | periodCode==2 | periodCode==3) {
						return R.drawable.weather_1_d;						//If its daytime..
					} else {
						return R.drawable.weather_1_n;						//If night time..
					}
				case 2:
					if( periodCode == 1 | periodCode ==2 | periodCode == 3) {
						return R.drawable.weather_2_d;
					} else {
						return R.drawable.weather_2_n;
					}
				case 3:
					if(periodCode ==1 | periodCode == 2 | periodCode == 3) {
						return R.drawable.weather_3_d;
					} else {
						return R.drawable.weather_3_n;
					}
				case 4: return R.drawable.weather_4;
				default:return R.mipmap.ic_launcher;
			}
		}
	}

	private class MultiAdapter extends ArrayAdapter {
		ArrayList<WeatherForecast> itemList;
		int rowLayoutId;
		public MultiAdapter(Context context, int rowLayoutId, ArrayList<WeatherForecast> list) {
			super(context,rowLayoutId,list);
			this.rowLayoutId = rowLayoutId;
			itemList = list;
		}

		@Override
		public View getView(int pos, View rowView, ViewGroup parent) {
			View row;

			if(rowView==null) {
				LayoutInflater layoutInflater = getLayoutInflater();
				row = layoutInflater.inflate(rowLayoutId,null);
			} else {
				row = rowView;
			}

			WeatherForecast forecast = itemList.get(pos);

			TextView date = (TextView) row.findViewById(R.id.weather_listitem_date_textview);
			TextView time = (TextView) row.findViewById(R.id.weather_listitem_time_textview);
			TextView weatherDescription = (TextView) row.findViewById(R.id.weather_listitem_weatherDescription_textview);
			TextView wind = (TextView) row.findViewById(R.id.weather_listitem_wind_textview);
			TextView temperature = (TextView) row.findViewById(R.id.weather_listitem_temperature_textview);
			TextView rain = (TextView) row.findViewById(R.id.weather_listitem_rain_textview);
			ImageView icon = (ImageView) row.findViewById(R.id.weather_listitem_icon);

			date.setText(createDateTextView(forecast));
			time.setText(forecast.getStartHHMM() +"-"+forecast.getEndHHMM());
			weatherDescription.setText(forecast.getWeatherName());
			wind.setText("Wind: "+forecast.getWindSpeed()+"m/s " + forecast.getWindDirection());
			temperature.setText("Temp: " +forecast.getTemperature()+"\u00B0");
			rain.setText("Rain: " + forecast.getRain()+"mm/h");
			icon.setImageResource(getIconID(forecast.getPeriodCode(),forecast.getWeatherCode()));
			return row;
		}

	}

	//Helper method for setting date to TextView
	private String createDateTextView(WeatherForecast forecast) {
		String startDate = forecast.getStartMMDD();
		String endDate = forecast.getEndMMDD();
		if(startDate.equals(endDate)) {
			return "Date: "+ forecast.getStartMMDD();
		} else {
			return "Date: " +forecast.getStartMMDD() + " to " + forecast.getEndMMDD();
		}
	}

	//Method to check for network..
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}