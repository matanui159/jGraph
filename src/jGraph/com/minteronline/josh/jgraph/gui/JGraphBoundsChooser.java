package com.minteronline.josh.jgraph.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JGraphBoundsChooser {
	private static JPanel panel;
	private static JFormattedTextField xmin;
	private static JFormattedTextField xmax;
	private static JFormattedTextField ymin;
	private static JFormattedTextField ymax;
	
	static {
		panel = new JPanel(new GridLayout(4, 2, 4, 4));
		NumberFormat format = NumberFormat.getIntegerInstance();
		JGraphBounds bounds = JGraphConfig.getBounds();
		
		panel.add(new JLabel("X MIN"));
		xmin = new JFormattedTextField(format);
		xmin.setHorizontalAlignment(JFormattedTextField.RIGHT);
		xmin.setValue((long)bounds.xmin);
		panel.add(xmin);
		
		panel.add(new JLabel("X MAX"));
		xmax = new JFormattedTextField(format);
		xmax.setHorizontalAlignment(JFormattedTextField.RIGHT);
		xmax.setValue((long)bounds.xmax);
		panel.add(xmax);
		
		panel.add(new JLabel("Y MIN"));
		ymin = new JFormattedTextField(format);
		ymin.setHorizontalAlignment(JFormattedTextField.RIGHT);
		ymin.setValue((long)bounds.ymin);
		panel.add(ymin);
		
		panel.add(new JLabel("Y MAX"));
		ymax = new JFormattedTextField(format);
		ymax.setHorizontalAlignment(JFormattedTextField.RIGHT);
		ymax.setValue((long)bounds.ymax);
		panel.add(ymax);
	}
	
	public static JGraphBounds showDialog(Component parent) {
		if (JOptionPane.showConfirmDialog(parent, panel, "Bounds", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
				== JOptionPane.OK_OPTION) {
			JGraphBounds bounds = new JGraphBounds();
			bounds.xmin = ((Long)xmin.getValue()).intValue();
			bounds.xmax = ((Long)xmax.getValue()).intValue();
			bounds.ymin = ((Long)ymin.getValue()).intValue();
			bounds.ymax = ((Long)ymax.getValue()).intValue();
			JGraphConfig.setBounds(bounds);
			return bounds;
		}
		return null;
	}
}
