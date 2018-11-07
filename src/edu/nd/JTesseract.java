package edu.nd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JTesseract {
    public JTesseract() {
    	
    }
    
    public static synchronized String doOCR(String name) {
    	String ret = "";
    	try {
        	Runtime rt = Runtime.getRuntime();
        	// --psm 5 --oem 1
        	String output = System.getProperty("user.home") + File.separator + "output";
        	String tess_exec = ImageViewer.tesseract;
        	String[] options = ImageViewer.option.split(";");
        	String[] commands = {tess_exec, name, output};
        	String[] com = new String[options.length + commands.length];
        	
        	int i=0;
        	for(String element: commands) {
        		com[i] = element;
        		i++;
        	}
        	for(String element: options) {
        		com[i] = element;
        		i++;
        	}

        	Process proc = rt.exec(com);
        	int exitVal = proc.waitFor();
        	Logger.debug("Process exitValue: " + exitVal);
        	
        	output = output + ".txt";
        	
        	File fileDir = new File( output );
    		
    		BufferedReader in = new BufferedReader(
    		   new InputStreamReader(
                          new FileInputStream(fileDir), "UTF8"));
    		        
    		String str = "";
    		      
    		while ((str = in.readLine()) != null) {
    		    ret = ret + str + " ";
    		}
            in.close();
    	} catch (Exception ex) {
    		Logger.err(ex.getMessage(), ex);
    		//ex.printStackTrace();
    	}
    	
    	return ret;
    }

}
