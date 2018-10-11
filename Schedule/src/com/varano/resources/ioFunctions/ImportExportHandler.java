//Thomas Varano
//Oct 9, 2018

package com.varano.resources.ioFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.varano.information.Schedule;
import com.varano.information.constants.ErrorID;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;

public class ImportExportHandler {
	
	public static void export(Schedule s) {
		String loc = requestExportFileLocation();
		if (loc == null) return;
		loc += FileHandler.SUFFIX;
		new SchedWriter(loc).write(s);
	}
	
	public static String formatFileLocation(String unformatted) {
		if (unformatted == null) return null;
		return unformatted.substring(unformatted.indexOf(":")).replace(':', '/');
	}
	
	public static String requestImportFileLocation() {
		String[] in = getScriptInput(FileHandler.IMPORT);
		if (in.length == 0) return null;
		return formatFileLocation(in[0]);
	}
	
	public static String requestExportFileLocation() {
		String[] in = getScriptInput(FileHandler.EXPORT);
		if (in.length == 0) return null;
		return formatFileLocation(in[0]);
	}
	
	private static String[] getScriptInput(String scriptLocation) {
		try {
			Process p = new ProcessBuilder("osascript", scriptLocation).start();			
			String s = null;
			ArrayList<String> sb = new ArrayList<String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				sb.add(s);
			}
			
			
			//PRINT ERRORS
			br = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			while ((s = br.readLine()) != null) {
				System.out.println(s);
			}
			
			p.waitFor();
			Agenda.log("Script input exit at "+p.exitValue());
			
			return sb.toArray(new String[sb.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void reinitializeWith(Agenda age, String fileRoute) {
		if (fileRoute == null) return;
		SchedReader read = new SchedReader(fileRoute);
		if (!read.checkValidity()) {
			JOptionPane.showMessageDialog(null, "Invalid data. Using previous schedule.", 
					Agenda.APP_NAME + "ERROR", JOptionPane.ERROR_MESSAGE, null);
			return;
		}
		
		//just for update
		try {
			FileHandler.write(fileRoute, FileHandler.SCHED_FINDER);
			FileHandler.setSchedRoute();
		} catch (IOException e) {
			ErrorID.showError(e, true);
		}
		
		age.getManager().reset(true);	
	}
	
	public static void main(String[] args) {
		FileHandler.initialFileWork();
		
		System.out.println(requestImportFileLocation());
	}
}
