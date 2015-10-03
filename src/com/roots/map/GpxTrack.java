package com.roots.map;
import java.awt.Point;
import java.io.IOException;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;


public class GpxTrack {
	
    private static XPathFactory xFactory = XPathFactory.instance();
    private static XPathExpression<Element> expr1 = xFactory.compile("//*[name()='trkpt']", Filters.element());
    
    List<Element> trkptlist;
    
    // bounding box
    double xMin = Integer.MAX_VALUE;
    double xMax = Integer.MIN_VALUE;
    double yMin = Integer.MAX_VALUE;
    double yMax = Integer.MIN_VALUE;
    
	public GpxTrack (String xmlSource) throws JDOMException, IOException
	{
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(xmlSource);
        Element root = jdomDocument.getRootElement();
 //       List<Element> trks = root.getChildren();
        trkptlist = expr1.evaluate(root);
        
        for (Element trkpt : trkptlist)
        {
			double lon = trkpt.getAttribute("lon").getDoubleValue();
			double lat = trkpt.getAttribute("lat").getDoubleValue();
			
			if (lon < xMin)	{ xMin = lon; }
			if (lon > xMax)	{ xMax = lon; }
			if (lat < yMin)	{ yMin = lat; }
			if (lat > yMax)	{ yMax = lat; }
			
//        	System.out.println("lat=" + trkpt.getAttributeValue("lat") + "  lon=" + trkpt.getAttributeValue("lon"));
			
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
	
}
