package com.roots.map;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.jdom2.Element;
import org.jdom2.JDOMException;

public class TrackPoint {
	
	/**
	 * Reference to the XML-Element of this TrackPoint.
	 */
	private Element trkpt;
	
	public double lat;
	public double lon;
	public String timestr;
	public Instant datetime;
	public double elevation;
	public ECEF ecef;
	public double distToPred = 0.0;
	public double distFromStart = 0.0;

	public TrackPoint (Element trkpt) throws JDOMException
	{
		this.trkpt = trkpt;
		
		this.lat = this.trkpt.getAttribute("lat").getDoubleValue();
		this.lon = this.trkpt.getAttribute("lon").getDoubleValue();
		ecef = ECEF.fromWGS84(lat, lon);
		
		timestr = this.trkpt.getChildTextNormalize("time", trkpt.getNamespace());
		datetime = Instant.parse(timestr);
		
//		double elevation = Double.valueOf(trkpt.getChildTextNormalize("ele", trkpt.getNamespace()));
//		Element extensions = trkpt.getChild("extensions", trkpt.getNamespace());
//		Element distance = extensions.getChild("distance");
		double distFromStart = 0; //Double.valueOf(distance.getTextNormalize());
	}

}
