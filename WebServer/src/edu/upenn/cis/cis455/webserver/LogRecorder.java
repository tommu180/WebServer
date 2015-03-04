package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Chunxiao Mu
 *
 */
public class LogRecorder {
	public static File logFile = new File("error.log");
	//public static 
	public static void addMessage(String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(msg).append(":\t").append(new Date().toString());
		writeFile(sb);
	}
	
	/**
	 * Write error and its message to the log file
	 * @param ex
	 * @param msg
	 */
	public static void addErrorMessage(Exception ex, String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n**********************\n").append(new Date().toString());
		sb.append(System.getProperty("line.separator"));
		sb.append(msg).append("\n");
		sb.append(ex.toString());
		sb.append("\n**********************\n");
		// if file doesnt exists, then create it
		writeFile(sb);
	} 

	
	/**
	 * Write string buffer to the file
	 * @param sb
	 */
	private static void writeFile(StringBuilder sb) {
		try {
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
		} catch (SecurityException se){
			System.err.println("The log cannot be opened to write.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the content in a string to add to the response
	 * @return a string of log content
	 */
	public static String attachLog() {
		StringBuilder sb = new StringBuilder();
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			BufferedReader bR = new BufferedReader(new FileReader(logFile.getAbsolutePath()));
			String line;
			while ((line = bR.readLine()) != null) {
				sb.append("<P>").append(line).append("</P>");
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find error log");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Cannot create log file");
			e.printStackTrace();
		}
		return sb.toString();
	}
}
