package com.redfire.framework.config.util;

import java.io.File; 
import java.io.FileFilter; 
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author guodongz
 * @version 2007-10-30 1.0.0
 * 功能描述:文件过滤器
 * 范围:本类实现了一个简单的以字符串为查找关键字，在特定目录下进行深度的查找 文件，目录.
 * 	   返回符合条件的文件List, 关键字不支持模式匹配
 * 举例：
 *    查找后缀为xml,XML,xMl,....文件：
 *      1.List fileList=SimpleFileFilter.DeeplistFiles(srcDirectory, new SimpleFileFilter(SimpleFileFilter.EXTENSION,"xml"));
 *      2.List fileList=SimpleFileFilter.DeeplistFiles(srcDirectory, new SimpleFileFilter(SimpleFileFilter.EXTENSION,SimpleFileFilter.XMLFILEFILTER));
 *    查找目录文件：  
 *      1.List fileList=SimpleFileFilter.DeeplistFiles(srcDirectory, new SimpleFileFilter(SimpleFileFilter.DIRECTORY,null));
 *    查找文件：  
 *      1.List fileList=SimpleFileFilter.DeeplistFiles(srcDirectory, new SimpleFileFilter(SimpleFileFilter.FILENAME,"fileName"));
 */
public class SimpleFileFilter implements FileFilter { 
	/**
	 * 常量描述:目录名匹配类型
	 */
	public static int  DIRECTORY=1;
	/**
	 * 常量描述:文件名匹配类型
	 */
	public static int  FILENAME=2;
	/**
	 * 常量描述:文件扩展名匹配类型
	 */
	public static int  EXTENSION=3;
	//－常用的过滤器
	/**
	 * 常量描述：xml 文件过滤器
	 */
	public static SimpleFileFilter XMLFILEFILTER=new SimpleFileFilter(SimpleFileFilter.EXTENSION,"xml");
	/**
	 * 常量描述：properties文件过滤器
	 */
	public static SimpleFileFilter PROPERTIESFILEFILTER=new SimpleFileFilter(SimpleFileFilter.EXTENSION,"properties");
	private String key=""; //匹配的关键字
	private int  type=2; //匹配的类型
  /**
   * 功能描述：新建过滤器
   * @param type 匹配的类型
   * 			  1.匹配目录(SimpleFileFilter.DIRECTORY)； 
   *			  2:匹配文件(SimpleFileFilter.FILENAME)；   
   *			  3.匹配扩展名(SimpleFileFilter.EXTENSION)；  
   * 			  默认：2.匹配文件名
   * @param key   匹配的关键字； key为null或""为 全匹配； 
   *              key不区分大小写；
   */
	public SimpleFileFilter(int type,String key) { 
		  if (key!=null)
			this.key = key.toLowerCase(); 
		  if(type>0 && type<4)
			 this.type=type;
	} 
	
	public boolean accept(File file) { 
	  if(file==null) return false;
	   boolean returnValue= false;
	   switch(type){
		   case 1:
			   returnValue=  acceptDirectory( file);
			   break;
		   case 2:
			   returnValue= acceptFileName(file); 
			   break;
		   case 3:
			   returnValue=  acceptExtensionName( file);
			   break;
		   default:
			   returnValue= false;
	   }
	   return returnValue;
	} 
	
	//--功能函数
	private  boolean acceptDirectory(File file){
		 if(file.isFile()) { 
			 return false; 
		 } 
		 else {
			if("".equals(key)){
				return true;
			}
			else {
				  String fileName=file.getName().toLowerCase();
				  if (fileName.indexOf(key)!=-1){
					  return true;  
				  }
				  else {
					 return false;
				  }
			}
		 }
	}
	private boolean acceptFileName(File file){
		 if(file.isDirectory( )) { 
				return false; 
		} 
		 else {
				if("".equals(key)){
					return true;
				}
				else {
					  String fileName=file.getName().toLowerCase();
					  if (fileName.indexOf(key)!=-1){
						  return true;  
					  }
					  else {
						 return false;
					  }
				}
			 }
	}
	private boolean acceptExtensionName(File file){
		 if(file.isDirectory( )) { 
				return false; 
		} 
		 else {
			   if("".equals(key)){
				   return true;
			   }
			   else {
					String name = file.getName( ).toLowerCase(); 
					int index = name.lastIndexOf("."); 
					if(index == -1||index == name.length( )-1) { 
						return false; 
					}
					else {  
						return this.key.equals(name.substring(index+1));
					} 
			   }
		 }
	}
	/**
	 * 功能描述：递归的查找目录下边的符合过滤条件的文件
	 * @param directory 需要递归查找的目录
	 * @param fileFilter 文件过滤器
	 * @return 符合条件的File列表 List;
	 *         directory 为null或者是文件 返回null;
	 *         directory 为目录但是没有符合条件的文件 返回 empty列表;
	 */
	public static List<File> deeplistFiles(File directory,FileFilter fileFilter){
		List<File> returnlist=listFiles2(directory, fileFilter);
		return returnlist;
		
	}
	 private File getRootFile(){
   	  URL url = Thread.currentThread().getContextClassLoader().getResource("");
   	  File file=null;
		try {
			file = new File(URLDecoder.decode(url.getFile(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
   	  return file;
   }
	private static FileFilter DIRECTORY_TYPE=new SimpleFileFilter(SimpleFileFilter.DIRECTORY,null);
	//--------这里可以作优化----------
	private static List<File> listFiles2(File directory,FileFilter fileFilter){
		if(directory==null||directory.isFile())return null;
		
		List<File> returnValues=new ArrayList<File>();
		
		File[]  acceptFiles=directory.listFiles(fileFilter);
		//--当前目录下符合条件的文件查询
		if(acceptFiles!=null && acceptFiles.length>0)	
			returnValues.addAll(Arrays.asList(acceptFiles));
		//--当前目录下的所有子目录
		File[] childDirectory = directory.listFiles(DIRECTORY_TYPE); 
		//--无子目录就返回
		if(childDirectory==null || childDirectory.length<1)return returnValues;
		//--遍历查找子目录下的符合条件的文件
		for(int i=0;i<childDirectory.length;i++){
				List<File> grandsonFiles=listFiles2(childDirectory[i],fileFilter);
				if(grandsonFiles!=null && !grandsonFiles.isEmpty()){
					returnValues.addAll(grandsonFiles);
				}
		}
	   return returnValues;
		
		
	}
}