package com.redfire.framework.config.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redfire.framework.config.prop.PropBean;
/**
 * 实现文件修改时间的监控
 * @author zhao
 *
 */
public class WatchFile {
	private static Logger LOGGER = LogManager.getLogger(WatchFile.class.getName());
	/**
	 * 多少秒监控一次
	 */
	private   long   intervalSecond=45; //秒
	private   long   status=1;
	class     ConfigInfo{ long lastModified;File file; IFileListener listener;}
	
	private   ExecutorService cachedThreadPool;  
	
	private   List<ConfigInfo> watchMap=new ArrayList<ConfigInfo>();
	
	private  WatchFile(){
		  cachedThreadPool = Executors.newCachedThreadPool();
		  this.status=1;
		  check();
	}
	private  static WatchFile _self=new WatchFile();
	public   static WatchFile getInstance() {
		return _self;
	}
	/**
	 * 开启文件监控
	 */
	public void stop(){
		 LOGGER.trace(" pre stop" );
		 if(cachedThreadPool!=null && !cachedThreadPool.isShutdown()){
			this.status=0;
			cachedThreadPool.shutdown();
			cachedThreadPool=null;
			LOGGER.trace(" stop" );
		 } 
	}
	/**
	 * 只开启一次
	 */
	public void restart(){
		 stop();
		 LOGGER.trace(" start " );
		 cachedThreadPool = Executors.newCachedThreadPool();
		 this.status=1;
		 check();
	}
	/**
	 * 监控特定的文件修改 
	 * @param config
	 * @return -1 未添加 ，1 ：添加成功  2:替换成功 
	 */
	public synchronized   int  watch(File file ,IFileListener listener){
		 if(file==null) return -1;
		 for(ConfigInfo configInfo:watchMap){
            if(configInfo.file.equals(file)){  
            	 LOGGER.trace(" change  watch file  listener " +file);
            	 configInfo.listener=listener;
            	 return 2;
            }  
         } 
		 LOGGER.trace(" watch file   " +file);
		 ConfigInfo configInfo=new ConfigInfo();
		 configInfo.listener=listener;
		 configInfo.file=file;
		 configInfo.lastModified=file.lastModified();
		 watchMap.add(configInfo);
		 return 1;
	}
	private  void broadcase(final ConfigInfo configInfo){
          Runnable thread = new Runnable() {  
            @Override  
            public void run() {  
            	if(configInfo!=null){
            		try{
            			 LOGGER.trace("  file Modify broadcase " +configInfo.lastModified +":" +configInfo.file );
            		     configInfo.listener.change();
            		}catch(Throwable e){
            			 System.out.println(" prop 文件加载异常 " +configInfo.file.getAbsolutePath());
            		}
            	}
                 
             }  
           };  
          if(cachedThreadPool!=null) {
        	    cachedThreadPool.execute(thread);
          }
      
	}
	public void check(){  
         
		  Runnable checkThread = new Runnable() {  
             public void run() {  
              	 LOGGER.trace(" watch check  " );
            	 for(ConfigInfo configInfo:watchMap){
            		long lastModified = configInfo.file.lastModified();  
                    if(configInfo.lastModified != lastModified){  
                    	configInfo.lastModified=lastModified;
                    	 LOGGER.trace(" check change  " +configInfo.file.getAbsolutePath());
                    	 System.out.println(" --检测到配置文件被修改,通知监听器:"+configInfo.file.getAbsolutePath());
                    	 broadcase(configInfo);
                    }  
                 }  
            	 try {
					 Thread.sleep(intervalSecond*1000);
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
            	 if(status==1 && cachedThreadPool!=null) {
               	     cachedThreadPool.execute(this);
                 }
              }  
          };
          if(cachedThreadPool!=null) {
        	  cachedThreadPool.execute(checkThread);
          }
    }
}
