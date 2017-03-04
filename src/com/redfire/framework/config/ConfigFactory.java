package com.redfire.framework.config;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redfire.framework.config.prop.PropBean;
public class ConfigFactory implements Factory<IConfig>	{
	private static Logger LOGGER = LogManager.getLogger(ConfigFactory.class.getName());
	private static String PROP_PATH="config.properties";
	protected   Map<String,IConfig> configMap=new HashMap<String,IConfig>();
	
	private static ConfigFactory _self=new ConfigFactory();
	public  static ConfigFactory getInstance() {return _self;}
	//工厂默认实现为一个单例
	private ConfigFactory(){
		 loadProp(this);
	}
	public static void loadProp(ConfigFactory configFactory){
		 LOGGER.debug("- 文件配置启动--开始 ");
		 PropBean propBean=new PropBean(PROP_PATH);
		 Map<String,String> map=propBean.getAll();
		 Set<String> keys= map.keySet();
		 LOGGER.debug("- load properties file ");
		 int error=0,success=0;
		 for(String key:keys){
			String value=map.get(key);
			URL url = ConfigFactory.class.getClassLoader().getResource(value);
			if(url==null||url.getFile()==null){
				 LOGGER.error("config.properties: "+key +" ="+ value+" 文件未发现");	
				 error++;
			}else{
				LOGGER.debug("config.properties: 加载 "+key +" ="+ value);	
			    PropBean propBean2=new PropBean(value);
			    configFactory.configMap.put(value.toUpperCase(), propBean2);
			    configFactory.configMap.put(key.toUpperCase(), propBean2);
			    success++;
			}
		 }
		 LOGGER.debug("- 文件配置启动 完成-- e:"+error +" s:"+success);
	}
	public Map<String, IConfig> getConfigMap() {
		return configMap;
	}
	public void setConfig(IConfig config) {
		  this.configMap.put(config.id().toUpperCase(), config);
	}
	public void setConfigs(Collection<IConfig> configs) {
		 for(IConfig config:configs){
			 if(this.configMap.containsKey(config.id().toUpperCase())){
				 LOGGER.error("重复设置配置:"+config.id());	
			 }
		     this.configMap.put(config.id().toUpperCase(), config);
		 }
	}
	@Override
	public IConfig getBean(String id) {
        if(id!=null){
		    return this.configMap.get(id.toUpperCase());
        }else{
        	return null;
        }
	}	
}