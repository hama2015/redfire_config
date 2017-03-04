package com.redfire.framework.pack;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.core.config.Configurator;

/**
 * 功能描述：替换本项目为特定公司的项目
 * @author zhao
 * @version 2012-2-21 v1.0
 */
public class Framework {
	private static String company1="peak";
    private String company="redfire";
    private String srcPath="D:/redfire_work/peak_config/";
    private String targetPat="D:/redfire_work/"+company+"_config/";
    private long filecount=0;
    private long copyReplacecount=0;
    private long copycount=0;
    
    private void createProject(){
	     createDir(new File(targetPat));
    }
    
    public static void main(String[] args){
    //	 Configurator.initialize("Log4j2", "classpath:config/log4j/log4j2.xml");
    	 Framework f=new Framework();
    	 //1.删除目标目录
    	 int step=1;
    	 System.out.println("开始创建项目: "+f.company);
    	 System.out.println(step+"：创建项目录 ");
    	 f.createProject();
    	  //1:源码目录
    	  String[] srcs={"src","test","pack"};
    	  for(String src:srcs){
    		 f.filecount=0;
    		 step++;
    	     System.out.println(step+"：copy替换"+src+"源代码目录");
    	     f.copySrc(src);
    	  }
    	  //2：模板目录 
    	  String[] templates={};
    	  for(String temp:templates){
    		  f.filecount=0;
    		  step++;
    	      System.out.println(step+"：copy替换"+temp+" 模板目录");
    	      f.copyTemplate(temp);
    	  }
    	 //3： 资源目录 
    	  String[] ress={"resource"};
    	  for(String res:ress){
    		  f.filecount=0;
    		  step++;
    	      System.out.println(step+"：copy替换"+res+"  resource目录");
    	      f.copyResource(res);
    	  }
    	  //工程文件 
    	  f.filecount=0;
    	  System.out.println("7：copy替换  工程目录");
    	  f.copyProjectFile();
    	 

    	  //直接复制文件
    	  String[] others={"web","doc"};
    	  for(String other:others){
    		       step++;
    		      int count=0;
    	   		  count= f.copy(other);
    	   		  f.copycount+=count;
    	    	  System.out.println(step+"：copy "+other+" 目录:"+count);
    	  }
    	  step++;
    	  System.out.println(step+"：replace web  目录:");
    	  f.replaceWeb();
    	 
    	  System.out.println("总计:"+ (f.copycount+f.copyReplacecount));
    	  System.out.println("copy:"+ f.copycount);
    	  System.out.println("copy替换:"+f.copyReplacecount);
    	 
    	 //---------------修规定daim--------------
       // String jspOld=company1+"/";
       // File actionTemplate=new File(f.targetPat,"resource/spring/applicationContext-ibatis3-dao.xml");
        //String content=FileTool.readFile(actionTemplate.getAbsolutePath());
        //content=content.replace(jspOld, f.company+"/");
        //FileTool.writeFile(actionTemplate,content);
    }
  
    public void copyProjectFile(){
    	   File targetDir=new File(targetPat);
    	   File srcDir=new File(srcPath);
           for(File file:srcDir.listFiles()){
        	   if(file.isFile()){
        		   copyProjectReplaceFile(file,srcDir,targetDir);
        	   }
           }
           File targetDirMyEclipse=new File(targetPat,".settings");
    	   File srcDirMyEclipse=new File(srcPath,".settings");
           for(File file:srcDirMyEclipse.listFiles()){
        	   if(file.isFile()){
        		   copyProjectReplaceFile(file,srcDir,targetDir);
        	   }
           }
    }
    public void copyResource(String res){
    	   File targetDir=new File(targetPat,res);
    	   createDir(targetDir);
    	   File srcDir=new File(srcPath,res);
    	    String[] fileTypes={"properties","xml"};
    	    for(String fileType:fileTypes){
    		   copySrcExt( srcDir, targetDir, fileType);
    	    }
    	    List<File> cc=SimpleFileFilter.deeplistFiles(srcDir,new SimpleFileFilter(SimpleFileFilter.EXTENSION, "bat"));
    	    for(File child:cc){
     		   this.copyFile(child, srcDir, targetDir);
     	    }
    	    List<File> cc2=SimpleFileFilter.deeplistFiles(srcDir,new SimpleFileFilter(SimpleFileFilter.EXTENSION, "txt"));
    	    for(File child:cc2){
      		   this.copyFile(child, srcDir, targetDir);
      	    }
    }
    public void copyTemplate(String templat){
    	//拷贝模板 这里有个bug
    	File srcDir=new File(srcPath+templat+"/");
    	
    	templat=templat.replace("company1",this.company);
        File targetDir=new File(targetPat+templat+"/");
  	    createDir(targetDir);
  	  
  	    String[] fileTypes={"java","include","xml","ftl","properties","jsp","js"};
  	    for(String fileType:fileTypes){
  		   copySrcExt(srcDir, targetDir, fileType);
  	    }
  	    //尖峰科技要求的特性
  	    peakReplace(targetDir);
    }
    //--------------------直接copy文件-----------------------------------------------------
    public int copy(String dirName){
    	//直接拷贝的目录 
    	return FileTool.copy(srcPath+dirName, targetPat+dirName, true);
    }
  
    private void copyFile(File child,File srcDir,File target){
    	//直接拷贝文件 
    	String filePath=child.getAbsolutePath().replace(srcDir.getAbsolutePath() ,target.getAbsolutePath());
    	File temp=new File(filePath);
    	if(!temp.getParentFile().exists()){
    		target.mkdirs();
    	}
       	filecount++;
       	System.out.println((copyReplacecount++)+":直接 copy 文件:"+temp.getAbsolutePath());
    	FileTool.copy(child, temp);
    }
   //-------------------------------------------------------------------------
   private void  createDir(File file){
	   //先删除后创建文件
	   if(file.exists()){
		   FileTool.deleteDir(file.getAbsolutePath());
	   }
	   file.mkdirs();
   }
  //-----------------copy 替换包名--------------------------------------------------------
   public void copySrc(String dirName){
	   //源文件目录 
       File targetDir=new File(targetPat+dirName+"/");
	   createDir(targetDir);
	   File srcDir=new File(srcPath+dirName+"/");
	   String[] fileTypes={"java","txt","properties","tld","xml"};
	   for(String fileType:fileTypes){
		   copySrcExt( srcDir, targetDir, fileType);
	   }
    }
   private void copySrcExt(File srcDir,File targetDir,String ext){
	   //源文件目录下的特定扩展名
   	     List<File> childList=SimpleFileFilter.deeplistFiles(srcDir,new SimpleFileFilter(SimpleFileFilter.EXTENSION, ext));
 	     if(childList==null ) return ;
   	     for(File child:childList){
   	    	copySrcReplaceFile(child,srcDir,targetDir);
 	     }
   }
   
   private void copySrcReplaceFile(File child,File srcDir,File target){
	  //替换源文件包名
	 if(child==null ||!child.exists())  {
		 System.out.println(":替换文件不存在 copy 文件:"+child.getAbsolutePath());
		 return ;
	 }
   	 String content=FileTool.readFile(child.getAbsolutePath());
   	 content=content.replaceAll("com\\."+company1, "com\\."+company);
     String filePath=child.getAbsolutePath().replace(srcDir.getAbsolutePath() ,target.getAbsolutePath());
   	 filePath=filePath.replace(company1+"\\", company+"\\");
   	 File temp=new File(filePath);;
   	 if(!temp.getParentFile().exists()){
     	temp.getParentFile().mkdirs();
      }
 	 filecount++;
 	// System.out.println(content);
   	 System.out.println((copyReplacecount++)+" 替换copy  "+child.getAbsolutePath()  +" to :文件:"+temp.getAbsolutePath());
    	FileTool.writeFile(temp,content);
    }
   	private void copyProjectReplaceFile(File child,File srcDir,File target){
  	  //替工程文件文件包名
     	String content=FileTool.readFile(child.getAbsolutePath());
     	content=content.replaceAll(company1, company);
     	String filePath=child.getAbsolutePath().replace(srcDir.getAbsolutePath() ,target.getAbsolutePath());
     	filePath=filePath.replace(company1+"\\", company+"\\");
     	File temp=new File(filePath);
     	if(!temp.getParentFile().exists()){
     		temp.getParentFile().mkdirs();
        }
   	    filecount++;
   	   System.out.println((copyReplacecount++)+" 替换copy  "+child.getAbsolutePath()  +" to :文件:"+temp.getAbsolutePath());
     
      	FileTool.writeFile( temp,content);
   }
   	//------------------------------------------------------------------------
    public void replaceWeb(){
 	    File targetDir=new File(targetPat+"web/");
 	    File srcDir=new File(srcPath+"web/");
 	
	   
	    String[] fileDirs={".","blue","common","yellow","extui"};
	    for(String fileDir:fileDirs){
 	      List<File> cc=SimpleFileFilter.deeplistFiles(new File(srcDir,fileDir),new SimpleFileFilter(SimpleFileFilter.EXTENSION, "jsp"));
 	      for(File child:cc){
  		     this.copySrcReplaceFile(child, srcDir, targetDir);
  	      }
	 	  List<File> cc2=SimpleFileFilter.deeplistFiles(new File(srcDir,fileDir),new SimpleFileFilter(SimpleFileFilter.EXTENSION, "html"));
		  for(File child:cc2){
	 		   this.copySrcReplaceFile(child, srcDir, targetDir);
	 	  }
	    }
	    //不用处理的目录 styles images
	    
	    
	    String[] files={"WEB-INF/web.xml","WEB-INF/tags/simpletable/pageToolbar.tag","WEB-INF/rapid.tld",};
	    for(String childFile:files){
		  	this.copySrcReplaceFile(new File(srcDir,childFile), srcDir, targetDir);
		}
	    copySrcReplaceFile(new File(targetDir,"scripts/dict.js") , srcDir, targetDir);
	    copySrcReplaceFile(new File(targetDir,"scripts/redfire.js") , srcDir, targetDir);
		File clazzes=new File(targetDir,"WEB-INF/classes");
  	    FileTool.deleteDir(clazzes.getAbsolutePath());
  	    clazzes=new File(targetDir,"WEB-INF/logs");
  	    FileTool.deleteDir(clazzes.getAbsolutePath());
    }
    
    private  void peakReplace(File targetDir){
    	System.out.println("peakTele 公司模板特性 ");
    	String jspNew="+\"${ut.toFirstLower(className)}\"+toFirstUpper(page)";
    	String jspOld="+page";
    	File actionTemplate=new File(targetDir,"java_src/${basepackage_dir}/${ut.toLower(className)}/action/${className}Action.java");
    	String content=FileTool.readFile(actionTemplate.getAbsolutePath());
     	content=content.replace(jspOld, jspNew);
    	FileTool.writeFile( actionTemplate,content);
    	
   	    File parentJsp=new File(targetDir,"web/${namespace}/${ut.toLower(className)}/");
   	    String[] fileName={"create.jsp","edit.jsp","form_include.jsp","list.jsp","show.jsp"};
        for(int i=0;i<fileName.length;i++){
        	File file=new File(parentJsp,fileName[i]);
        	String formNew="form_include.jsp";
         	String formOld="\\$\\{ut.toFirstLower\\(className\\)\\}Form_include.jsp";
         	if("create.jsp".equals(fileName[i]) || "edit.jsp".equals(fileName[i])){
	         	String contentTemp=FileTool.readFile(file.getAbsolutePath());
	         	contentTemp=contentTemp.replaceAll(formNew, formOld);
	         	FileTool.writeFile( file,contentTemp);
         	}
            File newFile=new File(parentJsp,"${ut.toFirstLower(className)}"+toFirstUpper(fileName[i]));
            file.renameTo(newFile);
        	
        }
        
       
    }
    public static String toFirstUpper(String className){
		if(className==null&& "".equals(className)) return "";
		return className.substring(0,1).toUpperCase()+className.substring(1);
    }
}
