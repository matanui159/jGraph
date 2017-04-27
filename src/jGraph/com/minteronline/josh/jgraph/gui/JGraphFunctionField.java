package com.minteronline.josh.jgraph.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class JGraphFunctionField extends JTextField {
	private static final long serialVersionUID = 0L;
	private static final String PREFIX = "y=";
	
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Timer timer;
	private boolean sysSet = false;
	
	public JGraphFunctionField() {
		super(JGraphConfig.getFunction());
		
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JGraphConfig.setFunction(getText());
				JGraphFrame.getCanvas().redrawForeground();
			}
		});
		timer.setRepeats(false);
		
		((AbstractDocument)getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass bypass, int offset, String text, AttributeSet attr) throws BadLocationException {
				if (!sysSet && offset < PREFIX.length()) {
					toolkit.beep();
				} else {
					bypass.insertString(offset, text, attr);
				}
				timer.restart();
			}
			
			@Override
			public void remove(FilterBypass bypass, int offset, int length) throws BadLocationException {
				if (offset < PREFIX.length()) {
					length -= (PREFIX.length() - offset);
					offset = PREFIX.length();
				}
				if (sysSet || length > 0) {
					bypass.remove(offset, length);
				} else {
					toolkit.beep();
				}
				timer.restart();
			}
			
			@Override
			public void replace(FilterBypass bypass, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
				if (!sysSet && offset < PREFIX.length()) {
					toolkit.beep();
				} else {
					bypass.replace(offset, length, text, attr);
				}
				timer.restart();
			}
		});
	}
	
	@Override
	public void setText(String text) {
		sysSet = true;
		super.setText(PREFIX + text);
		sysSet = false;
		if (timer != null) {
			timer.restart();
		}
	}
	
	@Override
	public String getText() {
		return super.getText().substring(PREFIX.length());
	}
}