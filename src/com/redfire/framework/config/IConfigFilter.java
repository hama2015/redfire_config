package com.redfire.framework.config;
/**
 * 配置信息过滤器
 * 过滤器可以进行参数替换，替换配置配置内容中特定参数为 运行是内容 
 *  @author zgd
 *  @version 2017-03-03
 */
public interface IConfigFilter {
	public String filter(String key,String value);
}
