package com.redfire.framework.config;

import java.util.Map;
/**
 * 系统配置接口
 * @author zgd
 * @since 20170303
 *
 */
public interface IConfig extends IConfigConstants {
	public static Factory<IConfig> FACTORY=ConfigFactory.getInstance();
	/**
	 * 获取配置项
	 * @param key
	 * @return 字符串形式的配置项
	 */
	public String get(String key);
	/**
	 * 获取配置项
	 *   c${0}d${1}
	 */
	public String get(String key,String[] values);
	/**
	 * 设置配置项
	 * @param key
	 * @return 
	 */
	public String set(String key);
	
	
	/**
	 * 获取配置项
	 * @param key
	 * @return 字符串形式的配置项
	 */
	public Map<String,String> getAll();
	//--------------------------------------------------------------
	/**
	 * 配置标示，全系统唯一
	 * @return
	 */
	 public String id();
	
	//------------------------------------------------------------
	/**
	 * 类描述
	 */
	public String describe();
	/**
	 * 检查配置项
	 * @param msg
	 * @return 如果没有获取的到必须配置的值，返回false;
	 */
	public boolean check(StringBuffer msg) ;
	/**
	 * 加载配置项 
	 * @param key
	 * @return
	 */
	 public int reload(String key);
	 /**
	 * 重新加载所有配置项
	 * @return
	 */
	 public int reloadAll();
	 /**
	  * 配置过滤器 只能是一个
	  */
	 public void setFilter(IConfigFilter filter);
	 /**
	  * 添加监听器 可以是多个 
	  */
	 public void addListener(IConfigListener Listener);
	 /**
	  * 移除属性监听器
	  */
	 public void removeListener(IConfigListener listener) ;
	 
}
