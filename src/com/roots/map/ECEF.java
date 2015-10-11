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
	
	private static double square (double x)
	{
		return x * x;
	}
	
	public static ECEF fromWGS84 (double latDEG, double lonDEG)
	{
		final double SemimajorAxis = 6378137.0;
		final double Ecc = 0.081819190842622;
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
	
	public static double dist (ECEF a, ECEF b)
	{
		double distsquared = square (a.x-b.x) + square (a.y-b.y) + square (a.z-b.z);
		return Math.sqrt(distsquared);
	}

}
