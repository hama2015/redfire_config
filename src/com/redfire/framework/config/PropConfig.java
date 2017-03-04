package com.redfire.framework.config;

/**
 * prop文件配置工具类
 * @author zgd 
 * @version 2017-03-03
 */
public class PropConfig {
  public static String get(String file,String key){
	  IConfig config= IConfig.FACTORY.getBean(file);
	  if(config!=null){
		  return config.get(key);
	  }
	  return null;
  }
  
  public static  boolean addListener(String file,IConfigListener listener){
	  IConfig config= IConfig.FACTORY.getBean(file);
	  if(config!=null){
		   config.addListener(listener);
		   return true;
	  }
	  return false;
  }
  public static  boolean setFilter(String file,IConfigFilter filter){
	  IConfig config= IConfig.FACTORY.getBean(file);
	  if(config!=null){
		   config.setFilter(filter);
		   return true;
	  }
	  return false;
  }
}
