package com.roots.map;

public final class ECEF {

	public final double x;
	public final double y;
	public final double z;
	
	private ECEF (double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Helper function to calculate the square of a double.
	 * 
	 * @param x
	 * @return x*x
	 */
	private static double square (double x)
	{
		return x * x;
	}
	
	/**
	 * WGS84 to ECEF coordinate transformation.
	 * Taken from my earlier Mathematica implementation.
	 * Parameter altitude has been omitted, i.e. calculated ECEF coordinate is always located on the WGS84 ellipsoid.
	 * 
	 * @param latDEG latitude part of WGS84 coordinate in DEG
	 * @param lonDEG longitude part of WGS84 coordinate in DEG
	 * @return ECEF coordinates x, y and z
	 */
	public static ECEF fromWGS84 (double latDEG, double lonDEG)
	{
		final double SemimajorAxis = 6378137.0;  // from WGS84 standard
		final double Ecc = 0.081819190842622; // from WGS84 standard
		
		double latRAD = Math.toRadians(latDEG);
		double lonRAD = Math.toRadians(lonDEG);
		
		double SINlat = Math.sin(latRAD);
		double COSlat = Math.cos(latRAD);
		double SINlon = Math.sin(lonRAD);
		double COSlon = Math.cos(lonRAD);
		
		double RCurv = SemimajorAxis / Math.sqrt (1.0 - square(Ecc*SINlat));
		
		double x = RCurv * COSlat * COSlon;
		double y = RCurv * COSlat * SINlon;
		double z = (1 - square(Ecc)) * RCurv * SINlat;
		
		return new ECEF(x,y,z);
	}
	
	/**
	 * Calculate in meters distance between two ECEF coordinates.
	 * 0 <= dist(a,b) = dist(b,a) holds for all a, b.
	 * 
	 * @param a
	 * @param b
	 * @return distance in meters between a and b, unsigned i.e. always positive  
	 */
	public static double dist (ECEF a, ECEF b)
	{
		double distsquared = square (a.x-b.x) + square (a.y-b.y) + square (a.z-b.z);
		return Math.sqrt(distsquared);
	}

}
