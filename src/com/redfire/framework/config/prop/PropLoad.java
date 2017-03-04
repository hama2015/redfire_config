package com.redfire.framework.config.prop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class PropLoad {
	/**
	 * 功能描述：加载文件并替换 ${WEB_FILEPATH}
	 * @param file
	 * @return
	 */
	public  Map<String, String> loadProperties(String classPathfile) {
		Map<String,String> appConfig = new HashMap<String,String>();
		try {
			loadAllPropertiesFromClassLoader(appConfig, classPathfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return appConfig;
	}
	
	public   File getFileFormClassPath(String resourceName){
		URL url = PropLoad.class.getClassLoader().getResource(resourceName);
		if(url.getFile()!=null){
	    	File file=new File(url.getFile());
	    	return file;
		}else{
			return null;
		}
		
	}
	public    String[] loadAllPropertiesFromClassLoader(Map<String,String> appConfig ,String... resourceNames) throws IOException {
		List<String> successLoadProperties = new ArrayList<String>();
		for(String resourceName : resourceNames) {
			Enumeration<URL> urls = PropLoad.class.getClassLoader().getResources(resourceName);
			while (urls.hasMoreElements()) {
				Properties properties =new Properties();
				URL url = urls.nextElement();
				successLoadProperties.add(url.getFile());
				InputStream input = null;
				try {
					URLConnection con = url.openConnection();
					con.setUseCaches(false);
					input = con.getInputStream();
					if(resourceName.endsWith(".xml")){
						properties.loadFromXML(input);
					}else {
						properties.load(input);
					}
					
					for (Object key : properties.keySet()) {
						 String value = properties.getProperty(key.toString());
						 appConfig.put(key.toString(), value);
					}
				}
				finally {
					if (input != null) {
						input.close();
					}
				
				}
			}
		}
		return (String[])successLoadProperties.toArray(new String[0]);
	}
}
