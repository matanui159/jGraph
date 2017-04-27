package com.minteronline.josh.jgraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JGraphCanvas extends JPanel {
	private static final long serialVersionUID = 0L;
	private static final int WIDTH  = 600;
	private static final int HEIGHT = 600;
	
	private BufferedImage background;
	private BufferedImage foreground;
	
	public JGraphCanvas() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		foreground = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		redrawBackground();
		redrawForeground();
	}
	
	public void redrawBackground() {
		Graphics2D g = background.createGraphics();
		int width  = background.getWidth();
		int height = background.getHeight();
		
		JGraphBounds bounds = JGraphConfig.getBounds();
		int xstep = width  / bounds.width;
		int xmid = xstep * -bounds.xmin;
		int ystep = height / bounds.height;
		int ymid = ystep * -bounds.ymin;
		
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, width, height);
		
		g.setColor(Color.CYAN);
		for (int i = 0; i < width; i += xstep) {
			g.drawLine(i, 0, i, height);
		}
		for (int i = 0; i < height; i += ystep) {
			g.drawLine(0, i, width, i);
		}
		
		g.setColor(Color.GRAY);
		g.drawLine(0, ymid, width, ymid);
		g.drawLine(xmid, 0, xmid, height);
		repaint();
	}
	
	private boolean isValidValue(double value) {
		if (value != Double.NaN && value != Double.NEGATIVE_INFINITY && value != Double.POSITIVE_INFINITY) {
			return true;
		}
		return false;
	}
	
	public void redrawForeground() {
		if (JGraphConfig.getFunction().trim().isEmpty()) {
			return;
		}
		if (!JGraphCompiler.compile()) {
			return;
		}
		if (!JGraphRuntime.load()) {
			return;
		}
		
		Graphics2D g = foreground.createGraphics();
		int width  = foreground.getWidth();
		int height = foreground.getHeight();
		
		JGraphBounds bounds = JGraphConfig.getBounds();
		double xmul = (double)bounds.width / width;
		int xadd = bounds.xmin;
		int ysub = bounds.ymin;
		double ydiv = (double)bounds.height / height;
		
		double[] graph = new double[width];
		for (int x = 0; x < width; x++) {
			graph[x] = (JGraphRuntime.eval(x * xmul + xadd) - ysub) / ydiv;
		}
		
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, width, height);
		
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(2));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int x = 1; x < width; x++) {
			if (isValidValue(graph[x - 1]) && isValidValue(graph[x])) {
				g.drawLine(x - 1, height - (int)graph[x - 1], x, height - (int)graph[x]);
			}
		}
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
		g.drawImage(foreground, 0, 0, null);
	}
}
