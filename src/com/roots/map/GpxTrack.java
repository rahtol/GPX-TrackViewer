package com.roots.map;
import java.awt.Point;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;


/**
 * @author bec
 *
 */
public class GpxTrack {
	
    private static XPathFactory xFactory = XPathFactory.instance();
    private static XPathExpression<Element> expr1 = xFactory.compile("//*[name()='trkpt']", Filters.element());
    
    List<Element> trkptlist;
    TrackPoint trackpoint [];
    
    final int DIST_THRESHOLD = 4;
    
    /**
     * Bounding box of GPX-Track in WGS84 coordinates. Here: x coordinate of top left edge
     */
    double xMin = Integer.MAX_VALUE;
    
    /**
     * Bounding box of GPX-Track in WGS84 coordinates. Here: x coordinate of bottom right edge
     */
    double xMax = Integer.MIN_VALUE;
    
    /**
     * Bounding box of GPX-Track in WGS84 coordinates. Here: y coordinate of bottom right edge
     */
    double yMin = Integer.MAX_VALUE;
    
    /**
     * Bounding box of GPX-Track in WGS84 coordinates. Here: y coordinate of top left edge
     */
    double yMax = Integer.MIN_VALUE;
    
	/**
	 * Create a GpxTrack object from given filename. The specified file must be readable and a valid GPX file, i.e. conform to the Topografx XML schema for GPX.
	 * @param xmlSource			Filename of .gpx file to read
	 * @throws JDOMException
	 * @throws IOException
	 */
	public GpxTrack (String xmlSource) throws JDOMException, IOException
	{
        SAXBuilder jdomBuilder = new SAXBuilder(XMLReaders.XSDVALIDATING);
        Document jdomDocument = jdomBuilder.build(xmlSource);
        Element root = jdomDocument.getRootElement();
        trkptlist = expr1.evaluate(root);
        trackpoint = new TrackPoint [trkptlist.size()];

        int i = 0;
        for (Element trkpt : trkptlist)
        {
			TrackPoint trackpt = new TrackPoint (trkpt);
			if (i>0)
			{
				trackpt.distToPred = ECEF.dist (trackpt.ecef, trackpoint[i-1].ecef);
				trackpt.distFromStart = trackpoint[i-1].distFromStart + trackpt.distToPred;
			}
			trackpoint [i++] = trackpt; 
			
			if (trackpt.lon < xMin)	{ xMin = trackpt.lon; }
			if (trackpt.lon > xMax)	{ xMax = trackpt.lon; }
			if (trackpt.lat < yMin)	{ yMin = trackpt.lat; }
			if (trackpt.lat > yMax)	{ yMax = trackpt.lat; }
        }

        System.out.println("xMin=" + xMin + "  xMax=" + xMax + "  yMin=" + yMin + "  yMax=" + yMax);
		
	}
	
	public int[] getTrackxPoints (int x0, int z) throws DataConversionException
	{
		int xPoints [] = new int [trkptlist.size()];
		int i = 0;

		for (Element trkpt : trkptlist)
        {
			double lon = trkpt.getAttribute("lon").getDoubleValue();
        	xPoints [i++] = MapPanel.lon2position(lon, z) - x0;
        }
		
		return xPoints;
	}

	public int[] getTrackyPoints (int y0, int z) throws DataConversionException
	{
		int yPoints [] = new int [trkptlist.size()];
		int i = 0;

		for (Element trkpt : trkptlist)
        {
			double lat = trkpt.getAttribute("lat").getDoubleValue();
        	yPoints [i++] = MapPanel.lat2position(lat, z) - y0;
        }
		
		return yPoints;
	}

	public int getTracknPoints ()
	{
		return trkptlist.size();
	}
	
	public int getBoundingBoxDiagonalPixelAnz (int z)
	{
		int ixMin = MapPanel.lon2position(xMin, z);
		int ixMax = MapPanel.lon2position(xMax, z);
		int iyMin = MapPanel.lat2position(yMin, z);
		int iyMax = MapPanel.lat2position(yMax, z);
		int dx = ixMax - ixMin;
		int dy = iyMax - iyMin;
		int lenlen = dx*dx + dy*dy;
		return (int) Math.floor(Math.sqrt(lenlen)); 
	}
	
	public int calcZoom (int maxDiagonalPixelAnz)
	{
		int z = 0;
		while ((z < 19) && (getBoundingBoxDiagonalPixelAnz (z) < maxDiagonalPixelAnz))
		{
			z++;
		}
		return z-1;
	}
	
	public Point getCenter (int z)
	{
		int ixMin = MapPanel.lon2position(xMin, z);
		int ixMax = MapPanel.lon2position(xMax, z);
		int iyMin = MapPanel.lat2position(yMin, z);
		int iyMax = MapPanel.lat2position(yMax, z);
		int x = (ixMin + ixMax) / 2;
		int y = (iyMin + iyMax) / 2;
		return new Point(x,y);
	}
	
	/**
	 * @param p 	a Point in map coordinates with respect to current zoom level
	 * @return 		index of TrackPoint with minimum distance to p if this distance is less than DIST_THRESHOLD
	 * 		   		or -1 if no TrackPoint exists with distance less than DIST_THRESHOLD to p
	 */
	public List<Integer> detectTrackPointHit (Point p, int z)
	{
		List<Integer> result = new ArrayList<Integer>();
		int distthresholdsquared = DIST_THRESHOLD * DIST_THRESHOLD;
		int mindistsquared = distthresholdsquared;
		boolean inside = false;
		
		for(int i=0; i < trackpoint.length; i++)
		{
			int ix = MapPanel.lon2position(trackpoint[i].lon, z);
			int iy = MapPanel.lat2position(trackpoint[i].lat, z);
			int distsquared = ((ix - p.x) * (ix - p.x)) + ((iy - p.y) * (iy - p.y));
			
			if ((distsquared < distthresholdsquared) && (!inside))
			{
				// found a new trackpoint to be recorded
				result.add(i);
				mindistsquared = distsquared;
				inside = true;
			}

			if ((distsquared >= distthresholdsquared) && (inside))
			{
				// leave section of the track that was close to the target thereby finalizing a TrackPoint on the list
				mindistsquared = distthresholdsquared;
				inside = false;
			}

			if (distsquared < mindistsquared)
			{
				// optimize an already recorded trackpoint
				result.set(result.size()-1, i);
				mindistsquared = distsquared;
			}
		}
		
		return result;
	}
	
	public String gettoolTiptext (List<Integer> listofidx)
	{
		String tooltip = "<html>";
		String separator = "";
		
		for(int idx : listofidx)
		{
			Duration dt = Duration.between(trackpoint[0].datetime, trackpoint[idx].datetime);
			LocalTime t = LocalTime.MIN.plus(dt);
			tooltip += String.format("%s%s<br>t[%d]=%02d:%02d:%02d<br>s[%d]=%f", separator, trackpoint[idx].timestr, idx, t.getHour(), t.getMinute(), t.getSecond(), idx, trackpoint[idx].distFromStart);
			separator = "<br>------------<br>";
		}
		
		tooltip += "</html>";
		return 	tooltip;
	}
	
}
