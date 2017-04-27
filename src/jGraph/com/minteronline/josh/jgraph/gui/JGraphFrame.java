package com.minteronline.josh.jgraph.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import say.swing.JFontChooser;

public class JGraphFrame extends JFrame {
	private static final long serialVersionUID = 0L;
	private static JGraphFrame instance;
	public JGraphFunctionField input;
	public JLabel output;
	public JGraphCanvas canvas;
	public JMenu mload;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				instance = new JGraphFrame();
				instance.start();
				instance.setVisible(true);
			}
		});
	}
	
	public static void setResult(String text) {
		instance.output.setText(text);
	}
	
	public static JGraphCanvas getCanvas() {
		return instance.canvas;
	}
	
	private void addFunction(final String name) {
		JMenuItem menu = new JMenuItem(name);
		menu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileInputStream stream = new FileInputStream("user/" + name + ".jgf");
					stream.read();
					
					int c;
					StringBuilder builder = new StringBuilder();
					while ((c = stream.read()) != 0) {
						builder.append((char)c);
					}
					
					input.setText(builder.toString());
					stream.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		mload.add(menu);
	}

	public void start() {
		setTitle("jGraph");
		
		Font font = JGraphConfig.getFont();
		JFontChooser chooser = new JFontChooser();
		chooser.setSelectedFont(font);
		
		input = new JGraphFunctionField();
		input.setFont(font);
		getContentPane().add(input, BorderLayout.NORTH);
		
		output = new JLabel(" ");
		output.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new EmptyBorder(4, 4, 4, 4)));
		getContentPane().add(output, BorderLayout.SOUTH);
		
		canvas = new JGraphCanvas();
		getContentPane().add(canvas, BorderLayout.CENTER);
		
		JMenuBar mbar = new JMenuBar();
		setJMenuBar(mbar);
		JMenu mjgraph = new JMenu("jGraph");
		mbar.add(mjgraph);
		
		JMenuItem msave = new JMenuItem("Save");
		msave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Save As...");
				try {
					Files.copy(Paths.get("current.jgf"), Paths.get("user/" + name + ".jgf"),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception ex) {}
				addFunction(name);
			}
		});
		mjgraph.add(msave);
		
		mload = new JMenu("Load");
		mjgraph.add(mload);
		
		for (File file : new File("user").listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jgf")) {
				addFunction(file.getName().substring(0, file.getName().length() - 4));
			}
		}
		
		mjgraph.add(new JSeparator());
		JMenu mconfig = new JMenu("Config");
		mjgraph.add(mconfig);
		
		JMenuItem mbounds = new JMenuItem("Change Bounds");
		mbounds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JGraphBoundsChooser.showDialog(JGraphFrame.this) != null) {
					canvas.redrawBackground();
				}
			}
		});
		mconfig.add(mbounds);
		
		JMenuItem mfont = new JMenuItem("Change Font");
		mfont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chooser.showDialog(JGraphFrame.this) == JFontChooser.OK_OPTION) {
					input.setFont(chooser.getSelectedFont());
					JGraphConfig.setFont(chooser.getSelectedFont());
					pack();
				}
			}
		});
		mconfig.add(mfont);
		
		mconfig.add(new JSeparator());
		JMenuItem mreset = new JMenuItem("Reset");
		mreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JGraphConfig.reset();
				dispose();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						main(null);
					}
				});
			}
		});
		mconfig.add(mreset);
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

}
