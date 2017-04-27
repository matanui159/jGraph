package com.minteronline.josh.jgraph.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JGraphCompiler {
	public static boolean compile() {
		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec(new String[] {"cgraph", "current.jgf", JGraphConfig.getFunction()});
			process.waitFor();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String result = reader.readLine();
			JGraphFrame.setResult(result);
			return !result.startsWith("!");
		} catch (Exception ex) {
			JGraphFrame.setResult("!" + ex.getMessage());
			return false;
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {}
		}
	}
}