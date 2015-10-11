package com.roots.map;

import java.time.Instant;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class TrackPoint {
	
	/**
	 * Reference to the XML-Element of this TrackPoint.
	 */
	private Element trkpt;
	
	/**
	 * Value of mandatory attribute "lat". Latitude part of WGS84 coordinate.
	 */
	public final double lat;
	/**
	 * Value of mandatory attribute  "lon". Longitude part of WGS84 coordinate.
	 */
	public final double lon;
	/**
	 * ECEF representation of trackpoint. Derived from WGS84 coordinate, i.e. lat/lon.
	 */
	public final ECEF ecef;
	/**
	 * Distance in meters to predecessor trackpoint, if any. 0.0 for first point of track.
	 * Set from outside, since predecessor trackpoint is not known here.
	 */
	public double distToPred = 0.0;
	/**
	 * Accumulated distance in meters along the track to first point of track.
	 * Set from outside.
	 */
	public double distFromStart = 0.0;

	/**
	 * Normalized text of optional child elment "time". 
	 */
	public String timestr;
	/**
	 * Derived by parsing "timestr" member.
	 */
	public Instant datetime;

	/**
	 * Currently not in use. Desinged to be value  of optional child elment "ele" giving elevation in meters above sea level (?). 
	 */
	public double elevation;

	public TrackPoint (Element trkpt) throws JDOMException
	{
		this.trkpt = trkpt;
		
		this.lat = this.trkpt.getAttribute("lat").getDoubleValue();
		this.lon = this.trkpt.getAttribute("lon").getDoubleValue();
		ecef = ECEF.fromWGS84(lat, lon);
		
		timestr = this.trkpt.getChildTextNormalize("time", trkpt.getNamespace());
		datetime = Instant.parse(timestr);
		
// Trouble with namespaces here !!!
//		double elevation = Double.valueOf(trkpt.getChildTextNormalize("ele", trkpt.getNamespace()));
//		Element extensions = trkpt.getChild("extensions", trkpt.getNamespace());
//		Element distance = extensions.getChild("distance");
//		double distFromStart = 0; //Double.valueOf(distance.getTextNormalize());
	}

}
