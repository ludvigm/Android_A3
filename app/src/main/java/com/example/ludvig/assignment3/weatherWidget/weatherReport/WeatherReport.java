/**
 * WeatherReport.java
 * Created: May 9, 2010
 * Jonas Lundberg, LnU
 */
package com.example.ludvig.assignment3.weatherWidget.weatherReport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author jonasl
 *
 */
public class WeatherReport implements Iterable<WeatherForecast> {
	private List<WeatherForecast> forecasts = new ArrayList<WeatherForecast>();
	private String city, country;
	private Date last_updated, next_update;
	private double latitude, longitude;
	private int altitude;

	public WeatherReport() {
	}

	/*
	 * Access methods
	 */
	public String getCity() {return city;}
	public String getCountry() {return country;}
	public String getLastUpdated() {return TimeUtils.getHHMM(last_updated);}
	public String getNextUpdate() {return TimeUtils.getHHMM(next_update);}
	public double getLongitude() {return longitude;}
	public double getLatitude() {return latitude;}
	public int getAltitude() {return altitude;}

	/*
	Added by me(lm222ix)
	 */
	public List<WeatherForecast> getForecasts() {
		return this.forecasts;
	}
	@Override
	public Iterator<WeatherForecast> iterator() {return forecasts.iterator();}
	
	/*
	 * Diagnostics
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("*** Weather Report ***");
		buf.append("\nLocation: "+city+", "+country);
		buf.append("\nAlt: "+altitude+"m, Lat: "+latitude+", Long: "+longitude);
		buf.append("\nLast Updated: "+getLastUpdated() );
		buf.append("\nNext Update: "+getNextUpdate() );
		
		
		return buf.toString();
	}
	
	/* 
	 * Methods used by WeatherHandler to build forecast 
	 * */
	void setLocation(String lat, String lng, String alt) {
		altitude = Integer.parseInt(alt);
		latitude = Double.parseDouble(lat);
		longitude = Double.parseDouble(lng);		
	}
	
	void setLastUpdated(String last) {
		last_updated = TimeUtils.getDate(last);
	}
	
	void setNextUpdate(String next) {
		next_update = TimeUtils.getDate(next);
	}
	
	void addForecast(WeatherForecast forecast) { forecasts.add(forecast); }

	void setCity(String city) {
		this.city = city;
	}
	void setCountry(String country) {
		this.country = country;
	}

}
