package com.redfire.framework.config.prop;

import java.io.File;
import com.redfire.framework.config.IConfigFilter;
/**
 * 功能描述：加载application.properties文件 # application.开始的属性 去掉application.设置到
 * appliction对象 # system. 开始的属性 去掉system.设置到 system.properties # mkdir. 开始的属性
 * 将依据value 创建目录 # ${WEB_FILEPATH} 将被替换为系统绝对路径 # seq_tablename 配置主键表名称 
 * @author zhao
 * 
 */
import com.redfire.framework.config.IConfigListener;
public class WebAppConfig implements IConfigFilter,IConfigListener  {
	 private String filePath = "application.properties";
	 private String basePath="./";
	 @Override
	 
	 public String filter(String key, String value) {
		if (value == null || "".equals(value.trim()))
			return null;
		if (value.indexOf("${WEB_FILEPATH}") > -1) {
			 value = value.replace("${WEB_FILEPATH}",basePath);
			 value =formatPath(value, "/");
		}
		return value;
	}
	/**
     * 功能描述:格式化文件路径，返回标准的字符串路径，
     * 统一使用pathSeparator指定文件分割符号分割文件，将替换连续多个文件分割符号,为单个文件分割符号
     * @param path 需要格式化的路径
     * @param pathSeparator  文件分割符号 一般为 "\\" ,"/" ,null, 当传入null时候模式使用 File.separator
     * @return 格式化后的文件路径
     * @since 20140916 
     * @author zgd 
     * 状态 已经测试
     */
    private  String formatPath(String path,String pathSeparator) {
    	 if(path==null || "".equals(path.trim())) return path;
    	 if(pathSeparator==null ||"".equals(pathSeparator.trim())) {
    		 pathSeparator=File.separator;
    	 }
    	 String[]  patterns={"\\\\","//","\\"};
    	 String    defSep="/";
    	 for(String pattern:patterns){
    	    while(path.indexOf(pattern)>-1){
    	      path = path.replace(pattern,defSep);
    	    }
    	 }
    	 if(!defSep.equals(pathSeparator)){
    	    while (path.indexOf(defSep)>-1){
    	     path = path.replace(defSep, pathSeparator);
    	    }
    	 }
         return path;
     } 
     @Override
	  public void setValue(String key, String value) {
		 if (key.startsWith("system.")) {
			System.setProperty(key.substring(7), value);
		 }
		 //WebContext.put(key, value); 
		 if (key.startsWith("application.")) {
		 //	servletContext.setAttribute(key.substring(12), value);
		 }
	  } 
   
}
