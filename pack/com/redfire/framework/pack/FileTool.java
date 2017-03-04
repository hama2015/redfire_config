package com.redfire.framework.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 文件处理工具类
 * @author 赵国栋
 * @version 0.1 20160324
 */
public class FileTool{
	private static Logger logger = LogManager.getLogger(FileTool.class.getName());
	private static String CHARSET="UTF-8";
    private static int DEFAULTBUFFERSIZE = 2048;
	//--------读取字节文件-------------

	/**
     * 返回文件字节内容 文件不要过大
     * @param filePath 文件路径
     * @return
     */
	public static byte[] getFileByte (String filePath){
		 return getFileByte(new File(filePath));
	}
    public static  byte[] getFileByte(File file){
		 return  toByteArray2(file);
	 
    }
    protected  static byte[] toByteArray1(File file) {  
         ByteArrayOutputStream bos = new ByteArrayOutputStream((int)file.length());  
         BufferedInputStream in = null;  
         InputStream fileIn=null;
         int len = 0;  
         try{  
        	fileIn=new FileInputStream(file);
            in = new BufferedInputStream(fileIn);  
            byte[] buffer = new byte[DEFAULTBUFFERSIZE];  
            while((len = in.read(buffer))!=-1){  
                bos.write(buffer,0,len);  
            } 
            return bos.toByteArray();
        }catch (IOException e) {  
            e.printStackTrace();  
            logger.error(e);
        }finally{  
        	close(bos,in,fileIn); 
        } 
        return null;
    }  
       
    /** 
     * NIO way 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    protected  static byte[] toByteArray2(File file){  
          
        FileChannel channel = null;  
        FileInputStream fs = null;  
        byte[] result = null;   
        try{  
            fs = new FileInputStream(file);  
            channel = fs.getChannel();  
            result = new byte[(int)channel.size()];  
            ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULTBUFFERSIZE);  
            int  length=-1;
            int  offset= 0;
            while((length=channel.read(byteBuffer)) > 0){ 
            	 byteBuffer.flip();
            	 byteBuffer.get(result,offset,length);
            	 byteBuffer.clear();
            	 offset+=length;
            }  
            return result;  
        }catch (IOException e) {  
        	e.printStackTrace();  
            logger.error(e);
        }finally{  
        	close(channel,fs);
        }  
        return null;
    }  
      
      
    /** 
     * Mapped File  way 
     * MappedByteBuffer 可以在处理大文件时，提升性能 
     * @param filename 
     * @return 
     * @throws IOException 
     * 
     */  
    protected static byte[] toByteArray3(File file){  
          
        FileChannel fc = null;  
        try{  
            fc = new RandomAccessFile(file,"r").getChannel();  
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size());  
            byteBuffer.load();
            byte[] result = new byte[(int)fc.size()];  
            //这里可能有问题 TODO
            if (byteBuffer.remaining() > 0) {  
                byteBuffer.get(result, 0, byteBuffer.remaining());  
            }  
            return result;  
        }catch (IOException e) {  
        	e.printStackTrace();  
            logger.error(e);
        }finally{  
        	close(fc);
        }  
        return null;
    }  
    /**
     * 删除指定目录,也可以直接删除文件
     * @param dirName
     */
    public static void deleteDir(String dirName)
    {
    	 deleteDir(new File(dirName));
    }

    public static void deleteDir(File fp)
    {
        if (fp.isFile()){
            fp.delete();
            return;
        }
        File childs[] = fp.listFiles();
        if(childs==null)  return;
        for (File child: childs) {
            if (child.isFile()) {
            	child.delete();
            } else if (child.isDirectory()) {
                deleteDir(child);
            }
        }
        fp.delete();
    }
  //----------复制文件---------------------------------------------------------------
    /**
     * 拷贝一个文件
     * @param src 目录或文件
     * @param dest  目录或文件
     * @param  overwrite 
     */
   public  static int  copy(String src, String dest){
	      return copy( src ,dest,  false);
   }
   public  static int  copy(String src, String dest, boolean overwrite){
	      File srcFile = new File(src);
	      File destFile = new File(dest);
	      return copy(srcFile ,destFile,  overwrite);
   }
   /**
	 * 
	 * @param src  源文件
	 * @param dest 目标文件
	 * @param bufferSize  缓冲大小
	 */
	public static void copy(File src, File dest) {
		 copy(src,dest, false);
    }
    public  static int  copy(File src , File dest, boolean overwrite)
    {

	    int count=0;
	    if(src.isDirectory()) {
	        dest.mkdirs();
	        File[] files = src.listFiles();
	        for (File srcChild: files) {
	        	File destFile=new File(dest,srcChild.getName());
	        	count+= copy(srcChild,destFile, overwrite);
	        }
	    }else if (src.isFile()) {
	        File toFile =dest;
	        if (dest.isDirectory()){
	      	     toFile=new File(dest,src.getName());
	        }else{
	             File parentFile= dest.getParentFile();
	             if(!parentFile.exists()){
	            	 parentFile.mkdirs();
	             }
	        }
	        
	        if(src.getAbsoluteFile().equals(toFile.getAbsoluteFile())){
	        	  throw new  RuntimeException("被复制文件和目标文件不能相同");
	        }
	        copyFile( src,toFile,overwrite);
	        count++;
	      }
	      return count;
    }
   private  static void copyFile(File src,File dest, boolean overwrite ) {
	     InputStream fis =null;
	     OutputStream fos = null;
         try {
    	     fis = new FileInputStream(src);
    	     fos = new FileOutputStream(dest, !overwrite);
    	     copyStream(fis,fos);
    	   // copyStream2(fis,fos);
          } catch (Exception e) {
        	  logger.error(e);
        	  e.printStackTrace();
          }finally {
        	  close(fis,fos);
          }
   }
  /** private  static boolean copyStream2(FileInputStream fileIn, FileOutputStream fileOut ) {
	   FileChannel inChannel=   fileIn.getChannel();
       FileChannel outChannel=   fileOut.getChannel();
       try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true; // if success then return true
       }catch (Exception e) {
       	   logger.error(e);
           e.printStackTrace(); 
       }finally {
     	   close(inChannel,outChannel);
       }
       return false;
   } */
   private  static boolean copyStream(InputStream in1, OutputStream out1 ) {
       try {
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in1.read(bytes)) != -1) {
               out1.write(bytes, 0, c);
            }
            out1.flush();
            return true; // if success then return true
       }catch (Exception e) {
       	   logger.error(e);
           e.printStackTrace(); 
       } 
       return false;
   }   
  //----------复制文本文件----------------------------------------------------
   public static void copyText(File src, File dst) {
		copyText( src,  dst, 1024,CHARSET) ;
	}
    public static void copyText(File src, File dst, int bufferSize) {
		copyText( src,  dst, bufferSize,CHARSET) ;
	}
    public static void copyText(File src, File dst, String encoding) {
    	copyText( src,  dst, 1024,encoding) ;
    }
	public static void copyText(File src, File dst, int bufferSize,String encoding) {
			
		    InputStream fileIn=null;
			InputStreamReader inStream=null;
		    BufferedReader reader = null;
		
			
			OutputStream fileOut=null;
			OutputStreamWriter outStream=null;
			BufferedWriter writer = null;
			try {
				fileIn=new FileInputStream(src);
				inStream=new InputStreamReader(fileIn,encoding);
				reader = new BufferedReader(inStream,bufferSize);
				
				fileOut=new FileOutputStream(dst);
				outStream=new OutputStreamWriter(fileOut,encoding);
				writer = new BufferedWriter(outStream,bufferSize);
				//这里肯能会有问题
				String line = null;
				while ((line=reader.readLine()) != null) {
					writer.write(line);
				}
				writer.flush();
			}catch (IOException e) {
                logger.error(e);
				e.printStackTrace();
			} finally {
				close(reader,inStream,fileIn,fileOut,outStream,writer);

			}
	}

   //--------------------------------------------------------------------------------------------------
    /**
     * 读取文本文件
     * @param fileName
     * @return
     */
    public static String readFile(String fileName){
    	try{
			 return readFile(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			 logger.error(e);
			 throw new RuntimeException(fileName +"文件没有发现");
		}
    }
    public static String readFile(File file){
          return 	readFile( file,CHARSET);
    }
    public static String readFile(File file,String charset){
    	
    	try {
			return readFile(new FileInputStream(file),charset);
		} catch (FileNotFoundException e) {
			 logger.error(e);
			 throw new RuntimeException(file.getName() +"文件没有发现");
		}
    }
    public static String readFile(InputStream fis)
    {
    	return readFile(fis, CHARSET);
    }
    
    public static String readFile(InputStream fis, String charset) 
    {
    	BufferedReader bufferedreader=null;
    	InputStreamReader reader=null;
        try {
        	 StringBuffer ret = new StringBuffer();
        	 reader=new InputStreamReader(fis, charset);
        	 bufferedreader = new BufferedReader(reader);
             char[] buf = new char[1024];
             int size = bufferedreader.read(buf);
             while (size != -1) {
            	ret.append(buf, 0, size);
            	size = bufferedreader.read(buf);
             }
             return ret.toString();
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally { 
        	 close(bufferedreader,reader,fis);
        }
    }
    //-----------------------------------------------------------------------------------------------
    /**
     * 写文件
     * @param fileName
     * @param str
     * @param flag
     */
    /**
     * 写文件(追加内容，不覆盖原有内容)
     * @param fileName
     * @param str
     */
 
	public static void writeFile(File desFile,String content)
	{
		writeFile(desFile, content, false,CHARSET);
	}
    public static void writeFile(String fileName, String content,String charset)
    {
        writeFile(new File(fileName), content, false,charset);
    }
    public static void appendFileContent(String fileName, String content,String charset)
    {
        writeFile(fileName, content, true,charset);
    }
    public static void writeFile(String fileName, String content, boolean flag,String charset)
    { 	
    	writeFile(new File(fileName), content, false, charset);
    }
    public static void writeFile(File file, String content,String charset)
    { 	
    	writeFile(file,  content, false, charset);
    }
    public static void writeFile(File file, String str, boolean append,String charset)
    { 	
    	byte[] fileContent=null;
		try {
			fileContent = str.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(fileContent!=null){
    	   writeFile( file,fileContent, append);
		}
    }
    public static void writeFile(File file, byte[] fileContent){
		 writeFile( file,  fileContent,false);
	}
    public static void writeFile(File file, byte[] fileContent,boolean append)
    { 	
    	if(file.isDirectory()){
    		return ;
    	}
    	FileOutputStream fos=null;
        try {
        	fos= new FileOutputStream(file, append);
            fos.write(fileContent);
            fos.flush();
        } catch (Exception e) {
        	logger.error(e);
        	e.printStackTrace();
        }finally{
        	close(fos);
        }
    }

    /**
     * 取得指定目录下所有的文件列表，包括子孙目录和文件
     * 
     * @param dirName  目录
     * @return List    包含所有子孙文件和目录路径的列表
     * 状态:20140211 已经测试
     */
    public static List<String> listFile(String dirName)
    {
        return listFile(dirName, true);
    }
  
    /**
     * 取得指定目录下的文件，如果flag为true则包括子目录，否则只是其根目录
     * 
     * @param dirName  目录
     * @param flag  是否包含子孙
     * @return   包含所有子孙文件和目录路径的列表
     */
    public static List<String> listFile(String dirName, boolean flag)
    {
             List<String> result = new ArrayList<String>();
             File fp = new File(dirName);
             File fileList[] = fp.listFiles();
	         for (int i = 0; i < fileList.length; i++) {
	            if (fileList[i].isFile()) {
	                result.add(fileList[i].getPath());
	            } else if (flag && fileList[i].isDirectory()) {
	                result.add(fileList[i].getPath());
	                result.addAll(listFile(fileList[i].getPath()));
	            }
	        }

        return result;
    }
   
   
    /**
	  * 获取待压缩文件在ZIP文件中entry的名字。即相对于跟目录的相对路径名
	  * @param baseDirPath
	  * @param file
	  * @return
	  */
	 private static String getEntryName(String baseDirPath, File file){
	     File baseFile=new File(baseDirPath);
	     if(baseFile.isFile()){
	   	    return  file.getName();
	     }
	      baseDirPath=baseFile.getAbsolutePath();
		  if (!baseDirPath.endsWith(File.separator)){
		       baseDirPath += File.separator;
		  }
		  String filePath = file.getAbsolutePath();
		  //对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储。
		  if (file.isDirectory()){
		     filePath += File.separator;
		  }
		  int index = filePath.indexOf(baseDirPath);
		  String fileName= filePath.substring(index + baseDirPath.length());
		  return fileName;
	 }
	 /**
	  * 获取子文件相对于与父目录的相对的路径
	  * @param baseDir
	  * @param childFile
	  * @return
	  */
	 public   static String getRelationPath(File baseDir, File childFile ){
	      if(baseDir.isFile()){
	   	    return  childFile.getAbsolutePath();
	      }
	      String   baseDirPath=baseDir.getAbsolutePath();
		  if (!baseDirPath.endsWith(File.separator)){
		       baseDirPath += File.separator;
		  }
		  String filePath = childFile.getAbsolutePath();
		  //对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储。
		  if (childFile.isDirectory()){
		     filePath += File.separator;
		  }
		  int index = filePath.indexOf(baseDirPath);
		  String fileName= filePath.substring(index + baseDirPath.length());
		  return fileName;
	 }

   

	
	/**
	 * 把文件内容保存到指定的文件中
	 * @param text 文件内容
	 * @param dst  指定的 txt 文件
	 * @param bufferSize 缓冲大小
	 */
	public static void copyForText(String text, File dst)
	{
		BufferedWriter buffWriter = null;
		OutputStream   outStream=null;
		OutputStreamWriter outWriter=null;
		try
		{
			outStream=new FileOutputStream(dst);
			try{
				outWriter=new OutputStreamWriter(outStream);
				buffWriter = new BufferedWriter(outWriter, 1024);
				buffWriter.write(text);
				buffWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				close(buffWriter,outWriter);
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}finally {
			close(outStream);
		}
	}
	//用于txt还可以，word，pdf等好像不行
	/**
	 *  文本文件复制
	 * @param src 源文件
	 * @param dst 目标文件
	 * @param bufferSize 缓冲大小
	 */
	public static void copyForText(File src, File dst, int bufferSize) {
		try {
			FileInputStream fileInput=null;
			InputStreamReader reader=null;
			BufferedReader buffReader = null;
			
		
			
			FileOutputStream fileOut=null;
			OutputStreamWriter writer=null;
			BufferedWriter buffWriter = null;
			try {
				fileInput=new FileInputStream(src);
				reader=new InputStreamReader(fileInput,"GBK");
				buffReader = new BufferedReader(reader,bufferSize);
				
				fileOut=new FileOutputStream(dst);
				writer=new OutputStreamWriter(fileOut);
				buffWriter = new BufferedWriter(writer,bufferSize);
				//这里肯能会有问题
				String line = null;
				while ((line=buffReader.readLine()) != null) {
					buffWriter.write(line);
				}
				writer.flush();
			} finally {
				close(buffReader,reader,fileInput);
				close(buffWriter,writer,fileOut);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void close(Closeable ... closeObjs){
		for(Closeable obj:closeObjs){
		 if(obj!=null){
			 try{ obj.close();} 
			 catch (IOException e) {
				e.printStackTrace();
			 }
 		 }
	   }
	}
	/**
	 * 功能描述：获取当前文件的扩展名，包括 "."符号
	 * @param fileName
	 * @return
	 */
	public  static String getExtention(String fileName) {
		 int pos = fileName.lastIndexOf(".");
		 if(pos<1) return ".tmp";
		 return fileName.substring(pos);
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
    public static String formatPath(String path,String pathSeparator) {
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
    public static String formatPath(String path) {
          return formatPath( path, File.separator );
    }
	public static String getNewFileName(String fileName) {
		return new Date().getTime() + getExtention(fileName);
	}
	public  static void main(String[] args) throws InterruptedException{
		String path1="c://text/text2\\\\text3\\\\text4//text5////text6//////text7";
    	System.out.println(formatPath(path1));
	}
}
