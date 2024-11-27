package io.Manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author kedarpi
 *
 */
public class LoggerManager {

	private static LoggerManager instance = null;
	private LoggerManager(){
		
	}
	
	public static LoggerManager getInstance(){
		
		if(instance == null){
			instance = new LoggerManager();
		}
		return instance;
	}
	
	public synchronized void log(String message){
		
		writeLogIntoFile("Debug: "+message);
	}

	public synchronized void logError(String message){

		writeLogIntoFile("Error: "+message);
	}

	public synchronized void logInfo(String message){

		writeLogIntoFile("Info: "+message);
	}

	private synchronized void writeLogIntoFile(String message){
	
		String FILENAME = ScreenShots.getInstance().getHtmlOutputFolder();
		FILENAME = FILENAME + File.separator+"log.txt";
		try{
			File file = new File(FILENAME);
			if(!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(FILENAME,true);
			BufferedWriter bw = new BufferedWriter(fw);
			DateFormat dateformat = new SimpleDateFormat();
			Date date = new Date();
			String content = dateformat.format(date)+":" + TagMap.getInstance().getTagMapName().get("Test_Id") + ":"+message + "\n";
			bw.write(content);
			bw.newLine();
			bw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
