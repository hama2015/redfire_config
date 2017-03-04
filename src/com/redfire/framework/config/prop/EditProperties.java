package com.redfire.framework.config.prop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
/**
 * 功能描述：自定义MyProperties
 * 可以为每properties 添加注释
 * 
 * @author zhao
 * @version 2012-2-13 v1.0
 */
public class EditProperties {
	private File file = null; //文件路径
	private List<String> comments=Collections.emptyList(); //注释
	private String encoding = "UTF-8";
	public List<Property> proList = new ArrayList<Property>();

	public EditProperties(File file, String encoding) {
		this.file = file;
		this.encoding = encoding;
	}
    public List<Property> getList(){
    	return proList;
    }
    public List<String> comments(){
    	return comments;
    }
    public File  getFile(){
    	return file;
    }
	public void   load(){
		if(file==null ||! file.exists()){
			return ;
		}
		try{
		Reader in= new FileReader(file);
		BufferedReader br = new BufferedReader(in);
		
		String line = null;
		
		List<String> filecomments=Collections.EMPTY_LIST;
		List<String> comments =Collections.EMPTY_LIST;
		boolean fileCommentsFlag=true;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ("".equals(line)){ 
				continue;
			}// 如果为空
			//System.out.println(loadConvert(line));
			//转换
			if (line.startsWith("#")) // 如果为注释
			{   
				if (comments.equals(Collections.EMPTY_LIST)) {
					comments = new ArrayList<String>();
				}
				line=loadConvert(line.substring(1));
				comments.add(line);
				continue;
			}
			
            if(fileCommentsFlag==true){ //第一条property上边的一条注释行是property ,这之上认为是文件注释
            	fileCommentsFlag=false;
            	if(comments.size()>1){
            		 filecomments=comments;
            		 comments = new ArrayList<String>();
            		 comments.add(filecomments.remove(filecomments.size()-1));
            		 this.comments=filecomments;
            	}
            }
			String[] keyandvalue = line.split("=");
			if (keyandvalue != null && keyandvalue.length == 2) {
				Property property = new Property();
				property.key = loadConvert(keyandvalue[0]);
				if(property.key!=null)property.key=property.key.trim();
				property.value =loadConvert(keyandvalue[1]);
				property.comments = comments;
				comments = Collections.EMPTY_LIST;
				proList.add(property);
			}
			
		  }
		 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    /**
     * 功能描述:修改属性
     * @param key
     * @param value
     * @param comments
     * @return
     */
	public Property update(String key, String value, String comments) {
		if (key == null ||"".equals(key.trim()))
			return null;
		if (value == null )
			 value="";
		for (Property pro : proList) {
			if (key.equals(pro.key)) {
				pro.value = value;
				if (comments != null){
					pro.comments = Arrays.asList(comments.split("\n"));
				}
				return pro;
			}
		}
		return null;
	}
	public void update(List<Property> updateList) {
		for (Property proNew : updateList) {
			for (Property pro : proList) {
				if (proNew.key.equals(pro.key)) {
					pro.value =proNew.value;
					pro.comments =proNew.comments;
				}
			}
		}
	}
	/**
	 * 功能描述:在控制台打印属性
	 */
	public void show() {
		for (String comment : this.comments) {
			System.out.println("#" + saveConvert(comment,true,true));
		}
		System.out.println("#" + new Date());
		System.out.println();
		for (Property pro : proList) {
			for (String comment : pro.comments) {
				System.out.println("#" + comment);
			}
			System.out.println(pro.key + "=" + pro.value);
		}
	}

	public void save() throws IOException {
	    if(!file.exists()){
		   file.createNewFile();
	    }
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file, false),this.encoding);
		BufferedWriter bw = new BufferedWriter(out);
		boolean setDate=false;
		for (String comment : this.comments) {
			 if(comment.startsWith("_DATE")){
				bw.write("#_DATE " + saveConvert(new Date().toString(),true,true));
				setDate=true;
			 }
			 else{ 
				bw.write("#" + saveConvert(comment,true,true));
			 }
			 bw.newLine();
		}
		if(setDate==false){
			bw.write("#_DATE " + saveConvert(new Date().toString(),true,true));
			setDate=true;
			 bw.newLine();
		}
		for (Property pro : proList) {
			for (String comment : pro.comments) {
				bw.write("#" + saveConvert(comment,true,true));
				bw.newLine();
			}
			bw.write(saveConvert(pro.key,true,true) + "=" +saveConvert(pro.value,true,true));
			bw.newLine();
		}
		bw.close();
		out.close();
		saveTxt() ;
	}
	public void saveTxt() throws IOException {
		   String fileName=file.getName();
		   fileName=fileName.substring(0,fileName.lastIndexOf("."))+".txt";
		   File txtFile=new File(file.getParentFile(),fileName);
		   if(!txtFile.exists()){
			   txtFile.createNewFile();
		   }
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(txtFile, false),this.encoding);
			BufferedWriter bw = new BufferedWriter(out);
			boolean setDate=false;
			for (String comment : this.comments) {
				   if(comment.startsWith("_DATE ")){
						bw.write("#DATE " + saveConvert(new Date().toString(),true,true));
						setDate=true;
					 }
					 else{ 
				       bw.write("#" + comment);
					 }
				     bw.newLine();
			}
			if(setDate==false){
				 bw.write("#_DATE " + saveConvert(new Date().toString(),true,true));
				 setDate=true;
				 bw.newLine();
			}
			for (Property pro : proList) {
				for (String comment : pro.comments) {
					bw.write("#" + comment);
					bw.newLine();
				}
				bw.write(pro.key + "=" +pro.value);
				bw.newLine();
			}
			bw.close();
			out.close();
	}
	/**
	 * 功能描述:判断是否包换特定的key 
	 * @param key
	 * @return
	 */
    public boolean contains(String key){
    	if(key==null||"".equals(key.trim())){
    		return false;
    	}
    	for (Property pro : proList) {
			if(pro.key.equals(key)){
				return true;
			}
		}
    	return false;
    }
    /**
     * 
     * @param key
     * @param value
     * @param comments
     * @return
     */
	public synchronized Property add(String key, String value, String comments) {
		if (key == null)
			return null;
		if(contains(key)){
			return update( key,  value,  comments);
		}
		Property property = new Property();
		property.key = key.trim();
		property.value = value;
		if (comments != null)
			property.comments = Arrays.asList(comments.split("\n"));
		this.proList.add(property);
		return property;

	}
	public synchronized void add(List<Property> addList) {
	
		for (Property proNew : addList) {
			if(proNew.key==null||"".equals(proNew.key))
				continue;
			
			if(!this.contains(proNew.key)){
				this.proList.add(proNew);
			}
		}
	}
	public synchronized Property remove(String key) {
		if (key == null)
			return null;
		for (Property pro : proList) {
			if (key.equals(pro.key)) {
				proList.remove(pro);
				return pro;
			}
		}
		return null;
	}
	public synchronized void removeAll(List<Property> deleteList) {
		for (Property pro : deleteList) {
			remove(pro.key);
		}
	}
	public static class Property {
		public String key;
		public String value;
		public List<String> comments = Collections.EMPTY_LIST;
		public String getComments(){
			String comm="";
			String flag="";
			for(String c:comments){
				comm+=flag+c;
				flag="\n";
			}
			return comm;
		}
	}

	public static void main(String[] args) throws Exception {
		File file = new File(
				"D:/application.properties");
		
		EditProperties myPro = new EditProperties(file, "UTF-8");
		myPro.load();
		myPro.show();
		myPro.saveTxt();
	}
    /**
     * 功能描述：加载转换
     * @param lineStr
     * @return
     */
	private String loadConvert (String theString) {
		 char[] in=theString.toCharArray();
		 int off=0;
		 int len=in.length;
         int newLen = len * 2;
         if (newLen < 0) {
	      newLen = Integer.MAX_VALUE;
	     } 
         char[] convtBuf= new char[newLen];
  
        char aChar;
        char[] out = convtBuf; 
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];   
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
		    for (int i=0; i<4; i++) {
		        aChar = in[off++];  
		        switch (aChar) {
		          case '0': case '1': case '2': case '3': case '4':
		          case '5': case '6': case '7': case '8': case '9':
		             value = (value << 4) + aChar - '0';
			     break;
			  case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
			     value = (value << 4) + 10 + aChar - 'a';
			     break;
			  case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
			     value = (value << 4) + 10 + aChar - 'A';
			     break;
			  default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                     }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t'; 
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f'; 
                    out[outLen++] = aChar;
                }
            } else {
	        out[outLen++] = (char)aChar;
            }
        }
        return new String (out, 0, outLen);
    }
    /**
     * 功能描述:保存转换 
     * @param theString
     * @param escapeSpace =true
     * @param escapeUnicode =true
     * @return
     */
	private String saveConvert(String theString, boolean escapeSpace,
			boolean escapeUnicode) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace)
					outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}
    /**
     * 功能描述 ：写注释
     * @param bw
     * @param comments
     * @throws IOException
     */
	private static void writeComments(BufferedWriter bw, String comments)
			throws IOException {
		bw.write("#");
		int len = comments.length();
		int current = 0;
		int last = 0;
		char[] uu = new char[6];
		uu[0] = '\\';
		uu[1] = 'u';
		while (current < len) {
			char c = comments.charAt(current);
			if (c > '\u00ff' || c == '\n' || c == '\r') {
				if (last != current)
					bw.write(comments.substring(last, current));
				if (c > '\u00ff') {
					uu[2] = toHex((c >> 12) & 0xf);
					uu[3] = toHex((c >> 8) & 0xf);
					uu[4] = toHex((c >> 4) & 0xf);
					uu[5] = toHex(c & 0xf);
					bw.write(new String(uu));
				} else {
					bw.newLine();
					if (c == '\r' && current != len - 1
							&& comments.charAt(current + 1) == '\n') {
						current++;
					}
					if (current == len - 1
							|| (comments.charAt(current + 1) != '#' && comments
									.charAt(current + 1) != '!'))
						bw.write("#");
				}
				last = current + 1;
			}
			current++;
		}
		if (last != current)
			bw.write(comments.substring(last, current));
		bw.newLine();
	}

	/**
	 * Convert a nibble to a hex character 
	 * @param nibble     the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
