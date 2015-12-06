package com.roots.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TrackPointMarker extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5501206737022634838L;
	
	int idx = -1;
	GpxTrack gpxtrack = null;
	
    private static BufferedImage makeIcon(Color c) {
        final int WIDTH = 16, HEIGHT = 16;
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < HEIGHT; ++y)
            for (int x = 0; x < WIDTH; ++x)
                image.setRGB(x, y, 0);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(c);
        g2d.fillOval(4, 4, WIDTH - 8, HEIGHT - 8);

        double hx = 4;
        double hy = 4;
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
              double dx = x - hx;
              double dy = y - hy;
              double dist = Math.sqrt(dx * dx + dy * dy);
              if (dist > WIDTH) {
                 dist = WIDTH;
              }
              int color = image.getRGB(x, y);
              int a = (color >>> 24) & 0xff;
              int r = (color >>> 16) & 0xff;
              int g = (color >>> 8) & 0xff;
              int b = (color >>> 0) & 0xff;
              double coef = 0.7 - 0.7 * dist / WIDTH;
              image.setRGB(x, y, (a << 24) | ((int) (r + coef * (255 - r)) << 16) | ((int) (g + coef * (255 - g)) << 8) | (int) (b + coef * (255 - b)));
           }
        }
        return image;
    }

	TrackPointMarker ()
	{
        setFocusable(false);
        setText(null);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder());
        
//        BufferedImage hl = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g = (Graphics2D) hl.getGraphics();
//        g.drawImage(image, 0, 0, null);
//        drawRollover(g, hl.getWidth(), hl.getHeight());
//        hl.flush();
        setIcon(new ImageIcon(makeIcon(Color.red)));
		
	}
	
	public void setTrackPoint (GpxTrack gpxtrack, int idx)
	{
		this.idx = idx;
		this.gpxtrack = gpxtrack;
		List<Integer> l = new ArrayList<Integer>();
		l.add(idx);
		setToolTipText(gpxtrack.gettoolTiptext((l)));
		setVisible(true);
	}
	
	public void clearTrackPoint ()
	{
		setVisible(false);
		this.idx = -1;
	}
	
	public void setBounds (Point mapposition, int z)
	{
		double lon = gpxtrack.trackpoint[idx].lon;
		double lat = gpxtrack.trackpoint[idx].lat;
		int x = MapPanel.lon2position(lon, z) - mapposition.x;
		int y = MapPanel.lat2position(lat, z) - mapposition.y;
		setBounds (x-8, y-8, 16, 16);
	}

}
