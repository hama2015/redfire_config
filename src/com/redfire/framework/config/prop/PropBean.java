package com.redfire.framework.config.prop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redfire.framework.config.ConfigFactory;
import com.redfire.framework.config.IConfig;
import com.redfire.framework.config.IConfigFilter;
import com.redfire.framework.config.IConfigListener;
import com.redfire.framework.config.file.IFileListener;
import com.redfire.framework.config.file.WatchFile;

/**
 * 这个类建议单例使用 
 * @author zgd
 *  
 */
public class PropBean  implements IConfig,IFileListener{
	private static Logger LOGGER = LogManager.getLogger(PropBean.class.getName());
	private String id;
	private Set<IConfigListener> listenerSet=new HashSet<IConfigListener>();
	private IConfigFilter filter;
	private  Map<String,String> config=new HashMap<String,String>();
	public String id() {
		return id;
	}
	public PropBean(String id){
		    this.id=id;
		    LOGGER.debug("create PropBean ："+this.id);
		    reloadAll();
		    PropLoad propLoad=new PropLoad();
		    File file= propLoad.getFileFormClassPath(this.id);
		    LOGGER.debug("pre watchFile ："+this.id);
		    WatchFile.getInstance().watch(file,this);  
	}
	@Override
	public String get(String key) {
		if(key==null) return null;
		return config.get(key.toUpperCase());
	}

	@Override
	public String set(String key) {
		return null;
	}

	@Override
	public String describe() {
		return id +" 文件配置 ";
	}

	@Override
	public boolean check(StringBuffer msg) {
		return true;
	}

	@Override
	public int reload(String key) {
		return 0;
	}

	@Override
	public  synchronized int reloadAll() {
		  PropLoad propLoad=new PropLoad();
		  Map<String ,String> map2=new HashMap<String,String>(); 
		  
		  Map<String ,String> map=propLoad.loadProperties(this.id);
		  LOGGER.debug("content ："+this.id);
		  for(String key:map.keySet()){
	   		  String value=map.get(key);
	   		  LOGGER.trace("item : "+this.id +"=" +key +"="+value );
	   		  map2.put(key.toUpperCase(), value);
	      }
		  map.clear();map=this.config;map.clear();
		  
		  this.config=map2;
		  filter();
		  broadcast();
		  return 1;
	}
	private   synchronized  void filter(){
		  if(filter!=null){
			  Map<String ,String> map=new HashMap<String,String>(); 
			  for(String key:this.config.keySet()){
	   		     String value=this.config.get(key);
	   		     String  newValue =filter.filter(key, value);
	   		     LOGGER.trace("filter : "+this.id +"=" +key +"="+value  +" to "+ newValue);
	   		     map.put(key.toUpperCase(), newValue);
	          }
			  this.config= map; 
	     }
	}
	private void broadcast() {
		//TODO 未实现分项通知得功能 
		for(IConfigListener listener:listenerSet){
			broadcastOne( listener);
        }
	}
	private void broadcastOne(IConfigListener listener) {
		//TODO 未实现分项通知得功能 
		if(listener!=null){
		     for(String key:this.config.keySet()){
	    		 String value=this.config.get(key);
	    	     LOGGER.trace("broadcase : "+this.id +"=" +key +"="+value );
	    		 listener.setValue(key, value);
	    	 }
	     }
	}
	//---------------------------------------------------
	@Override
	public void setFilter(IConfigFilter filter) {
		  LOGGER.trace("addFilter : "+this.id );
	      this.filter=filter;
	      filter();
		  broadcast();
	}
	@Override
	public void addListener(IConfigListener listener) {
		  LOGGER.trace("addListener : "+this.id );
	      this.listenerSet.add(listener);	
	      broadcastOne( listener);
	}
	@Override
	public void removeListener(IConfigListener listener) {
		  LOGGER.trace("removeListener : "+this.id );
	      this.listenerSet.remove(listener);	
	}
	@Override
	public Map<String, String> getAll() {
		 Map<String ,String> map2=new HashMap<String,String>();
		 LOGGER.trace("getAll : "+this.id );
		 map2.putAll(this.config);
		 return map2;
	}
	@Override
	public String get(String key, String[] values) {
		  String value=get(key) ;
          if(value==null) return null;   
	      for(int i=0;i<values.length;i++){ 
	    	  //TODO
	          value=value.replaceAll("\\$\\{"+i+"\\}", values[i]);          
	      }     
          return value; 
	}
	@Override
	public boolean change() {
		     LOGGER.trace("change : "+this.id );
	    	 this.reloadAll();
	    	 return true;
	}
	
}
