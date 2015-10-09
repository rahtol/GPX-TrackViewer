package com.roots.map;

public class TrackPoint {
	
	public double lat;
	public double lon;
	public String time;
	public double distFromStart;
	public double elevation;
	
	public TrackPoint (double lat, double lon, String time, double distFromStart, double elevation)
	{
		this.lat = lat;
		this.lon = lon;
		this.time =  time;
		this.distFromStart = distFromStart;
		this.elevation = elevation;
	}

}
