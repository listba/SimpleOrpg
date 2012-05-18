package com.openorpg.simpleorpg.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
	private static PropertiesLoader instance = new PropertiesLoader();
	
	private PropertiesLoader() {
	}
	
	public static PropertiesLoader getInstance() {
		return instance;
	}
	 public Properties load(String propertiesFile) {
		 Properties properties = new Properties();
		 try {
			 InputStream in = new FileInputStream(propertiesFile);
			 properties.load(in);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 return properties;
 }

}
