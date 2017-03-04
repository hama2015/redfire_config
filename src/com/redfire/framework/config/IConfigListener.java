package com.redfire.framework.config;
/**
 * 配置信息变更监听器
 * 当IConfig初始化或者有属性更改时候发送监听通知 
 *
 * @author zgd
 * @version 20170303
 * 
 */
public interface IConfigListener {
	public void setValue(String key,String value);
}
