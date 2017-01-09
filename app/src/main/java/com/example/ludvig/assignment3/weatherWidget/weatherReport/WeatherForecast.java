/**
 * WeatherForecast.java
 * Created: May 9, 2010
 * Jonas Lundberg, LnU
 */
package com.example.ludvig.assignment3.weatherWidget.weatherReport;

import java.util.Date;

/**
 * Each WeatherForecast represents a forecast (weather, rain, wind, temperature, etc)
 * for a given time period.
 * 
 *  ToDO: Translate weather and wind names to English!
 *  
 * @author jonasl
 *
 */
public class WeatherForecast {
	private Date startTime, endTime;
	private int period_code, weather_code;
	private String weather_name;  // Name in Norwegian!
	private String wind_direction, direction_name; // Name in Norwegian!
	private double rain, wind_speed; 
	private String speed_name;
	private int temperature;
	
	/*
	 * Access methods
	 */
	
	/* Time period */
	public String getStartYYMMDD() {return TimeUtils.getYYMMDD(startTime);}
	public String getStartHHMM() {return TimeUtils.getHHMM(startTime);}
	public String getEndYYMMDD() {return TimeUtils.getYYMMDD(endTime);}

	public String getEndHHMM() {return TimeUtils.getHHMM(endTime);}
	public int getPeriodCode() {return period_code;}
	/* Weather */
	public String getWeatherName() {return weather_name;}
	public int getWeatherCode() {return weather_code;}
	/* Rain (mm/h), Temp (Celsius)*/
	public double getRain() {return rain;}
	public int getTemperature() {return temperature;}
	/* Wind */
	public String getWindDirection() {return wind_direction;}
	public String getWindDirectionName() {return direction_name;}
	public double getWindSpeed() {return wind_speed;}
	public String getWindSpeedName() {return speed_name;}
	
	//Added by lm222ix
	public String getStartMMDD() {return TimeUtils.getMMDD(startTime);}
	public String getEndMMDD() {return TimeUtils.getMMDD(endTime);}
	/* 
	 * Methods used by WeatherHandler to build forecast 
	 * */	
	void setPeriod(String from, String to, String period) {
		//System.out.println(from+"\t"+to+"\t"+period);
		startTime = TimeUtils.getDate(from);
		endTime = TimeUtils.getDate(to);
		period_code = Integer.parseInt(period);
	}
	
	void setWeather(String num, String name) {
		//System.out.println(num+"\t"+name);
		weather_code = Integer.parseInt(num);
		weather_name = name;
	}
	
	void setWindDirection(String dir, String name) {
		//System.out.println(dir+"\t"+name);
		wind_direction = dir;
		direction_name = name;
	}
	
	void setWindSpeed(String num, String name) {
		//System.out.println(num+"\t"+name);
		wind_speed = Double.parseDouble(num);
		speed_name = name;
	}
	
	void setRain(String num) {  // Precipitation, nederbörd
		//System.out.println(num);
		rain = Double.parseDouble(num);
	}
	
	void setTemperature(String num) {
		//System.out.println(num);
		temperature = Integer.parseInt(num);
	}
	
	/*
	 * Diagnostics
	 */
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Date: "+ TimeUtils.getYYMMDD(startTime));
		buf.append("\nFrom:" +TimeUtils.getHHMM(startTime)+", To: "+TimeUtils.getHHMM(endTime)
				   +", Period: "+period_code);
		buf.append("\nWeather: "+weather_name+", Code: "+weather_code);
		buf.append("\nWind: "+wind_direction+", Speed: "+wind_speed+"m/s");
		buf.append("\nTemperature: "+ temperature +", Rain: "+rain+"mm/h");
		return buf.toString();
	}
	
	


}
