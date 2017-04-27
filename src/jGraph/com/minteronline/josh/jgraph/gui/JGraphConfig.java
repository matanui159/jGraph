package com.minteronline.josh.jgraph.gui;

import java.awt.Font;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class JGraphConfig {
	private static final Properties CONFIG = new Properties();
	private static final String CONFIG_FILE = "jgraph.cfg";
	
	static {
		try {
			CONFIG.load(new FileReader(CONFIG_FILE));
		} catch (IOException ex) {}
	}
	
	private static void save() {
		try {
			CONFIG.store(new FileWriter(CONFIG_FILE), null);
		} catch (IOException ex) {}
	}
	
	private static int getInteger(String key, int value) {
		String result = CONFIG.getProperty(key);
		if (result == null) {
			return value;
		} else {
			try {
				return Integer.parseInt(result);
			} catch (NumberFormatException ex) {
				return value;
			}
		}
	}
	
	public static void reset() {
		CONFIG.clear();
		save();
	}
	
	public static String getFunction() {
		return CONFIG.getProperty("function", "");
	}
	
	public static void setFunction(String func) {
		CONFIG.setProperty("function", func);
		save();
	}
	
	public static Font getFont() {
		String family = CONFIG.getProperty("font.family", Font.MONOSPACED);
		int style = getInteger("font.style", Font.PLAIN);
		int size = getInteger("font.size", 16);
		return new Font(family, style, size);
	}
	
	public static void setFont(Font font) {
		CONFIG.setProperty("font.family", font.getFamily());
		CONFIG.setProperty("font.style", Integer.toString(font.getStyle()));
		CONFIG.setProperty("font.size", Integer.toString(font.getSize()));
		save();
	}
	
	public static JGraphBounds getBounds() {
		JGraphBounds bounds = new JGraphBounds();
		bounds.xmin = getInteger("bounds.xmin", -10);
		bounds.xmax  = getInteger("bounds.xmax", 10);
		bounds.ymin = getInteger("bounds.ymin", -10);
		bounds.ymax = getInteger("bounds.ymax", 10);
		bounds.width  = bounds.xmax - bounds.xmin;
		bounds.height = bounds.ymax - bounds.ymin;
		return bounds;
	}
	
	public static void setBounds(JGraphBounds bounds) {
		CONFIG.setProperty("bounds.xmin", Integer.toString(bounds.xmin));
		CONFIG.setProperty("bounds.xmax", Integer.toString(bounds.xmax));
		CONFIG.setProperty("bounds.ymin", Integer.toString(bounds.ymin));
		CONFIG.setProperty("bounds.ymax", Integer.toString(bounds.ymax));
		save();
	}
}
