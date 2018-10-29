package edu.nd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class JLoader {
	public JSONObject loadJson(String filename) throws Exception {
		File files = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(files));
		
		String ks = "";
		String line = "";
		while((line = br.readLine()) != null) {
			ks = ks + line;
		}
		br.close();
		JSONObject obj = new JSONObject(ks);
		
		return obj;
	}
}
