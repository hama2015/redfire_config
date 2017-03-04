package com.redfire.framework.config;

import java.util.Map;

import org.apache.logging.log4j.core.config.Configurator;

public class TestConfig {

	
	public static void main(String[] args){
	      Configurator.initialize("Log4j2", "classpath:config/log4j/log4j2.xml");
		  for(int i=1;i<10;i++){
			String jdbc =PropConfig.get("app", "jdbc_dialect");
			ConfigFactory configFactory=	ConfigFactory.getInstance();
		    Map<String,IConfig>	configs=configFactory.getConfigMap();
		    
		    for(String key:configs.keySet()){
		    	IConfig config=configs.get(key);
		    	System.out.println(key+": "+config.id());
		    	
		    }
			System.out.println(jdbc);
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
