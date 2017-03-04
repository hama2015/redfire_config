<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.redfire.framework.config.prop.EditProperties"%>
<%@page import="com.redfire.framework.config.prop.EditProperties.Property"%>
<%@page import="com.redfire.framework.config.util.SimpleFileFilter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.io.File"%>
<%!
/**
*功能描述:特定目录下所有的properites文件列表 ，包括子孙文件
*/
public List<File> getFileOptions(String filePath){
	File file=new File(filePath);
    return SimpleFileFilter.deeplistFiles(file, new SimpleFileFilter(SimpleFileFilter.EXTENSION,"properties"));	

}
/**
*功能描述:返回提交的需要操作的选定值 
*/
public List<Property> getList(HttpServletRequest request){
            List<Property> record=new ArrayList<Property>();
	  		String[] checks=request.getParameterValues("check");//需要改变的
	  		if(checks==null || checks.length<1){
	  			return record;
	  		}
	  		String[] keys=request.getParameterValues("key");
	  		String[] values=request.getParameterValues("value");
	  		String[] comments=request.getParameterValues("comment");
	  		
	  		for(int i=0;i<checks.length;i++){
	  		    if(checks[i]==null ||"".equals(checks[i].trim())){ 
	  		       	continue;
	  		    }
	  		    
	  		    checks[i]=checks[i].trim();
	  		    
	  		    for(int j=0;j<keys.length;j++){
	  		        if(checks[i].equals(keys[j].trim())){
			  			Property pro=new Property();
			  			pro.key=keys[j].trim();
			  			pro.value=values[j].trim();
			  			if(comments[j]!=null){
			  			   pro.comments=Arrays.asList(comments[j].split("\n"));
			  			}
			  			record.add(pro);
			  			break;
	  		        }
	  		    }
	  		 }
	  		return record;
}
/**
*功能描述：处理请求
*/
public EditProperties dealRequest(HttpServletRequest request,String filePath) throws Exception{
	   EditProperties myPro=null;
        String type = request.getParameter("type");
        if(filePath!=null && !"".equals(filePath.trim())){
	    File file=new File(filePath);
	    if(file.exists()){
	      myPro=new EditProperties(file,"UTF-8");
	      myPro.load();
	      if("query".equals(type)){
		    	  return myPro;
		  }
	     
	      if("add".equals(type)){
	    	   String addKey = request.getParameter("addKey");
	    	   if(addKey!=null && !"".equals(addKey.trim())){
	    	      myPro.add(addKey.trim().toUpperCase(),"[待添加]","待添加");
	    	      myPro.save();
	    	   }
	    	   return myPro;
	      }
	      List<Property> list= getList(request);
	      if("update".equals(type)){
	    	   myPro.update(list);
	    	   myPro.save();
	      }else if("del".equals(type)){
	    	   myPro.removeAll(list);
	    	   myPro.save();
	       }
	    }
     }
    return myPro;
}
public String getComments(List<String> comments ){
	String comm="";
	String flag="";
	for(String c:comments){
		comm+=flag+c;
		flag="\n";
	}
	return comm;
}
 %>
<%
String filePath = request.getParameter("filePath");
String srcBase=request.getParameter("srcBase");
if(srcBase==null || "".equals(srcBase)){
	//一般这里修改为你的项目了路径，演示的时候配置为项目发布目录
	srcBase=request.getSession().getServletContext().getRealPath("/");
	srcBase=srcBase+"WEB-INF/classes/";
}
List<File> fileOptions=getFileOptions(srcBase);
if(filePath==null||"".equals(filePath)){
	String name="dict.properties" ; 
    for(File file:fileOptions){
    	if(file.getName().endsWith(name)){
    		filePath=file.getAbsolutePath();
    	}
    }
}//设置默认值
EditProperties myPro=dealRequest(request,filePath);
String path = request.getContextPath();
String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
%>
<html>
	<head>
		<base href="<%=basePath%>"></base>
		<title>properties文件修改器工具,不支持多线程使用</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
	<script>
     function doSubmit(type){
    	 var theForm=document.forms['queryForm'];
    	 theForm.type.value=type;
    	 if("add"==type){
    		  var data=window.prompt("请输入键","redfire.xxx");
              if(data==null || data=='' || data=="redfire.xxx"){
                 alert("你没有输入键 ");
                 return ;
    	      }    
    	     theForm.addKey.value=data;
    	     theForm.submit();
    	     return ;
    	 }
    	 var checkFlag=false;
    	 if(theForm.check && theForm.check.length){
 			for(var i=0;i<theForm.check.length;i++){
 				if(theForm.check[i].checked==true){
 					checkFlag=true;
 					break;
 				}
 			}
 		}else if(theForm.check) {
 			if(theForm.check.checked==true){
 				checkFlag=true;
 			}	
 		}
  		if(checkFlag==true ||type=="query"){
    	   theForm.submit();
  		}else{
  	  	   alert("请选择你需要操作的记录");
  		}
     }
     function doSelect(){//全选句柄关联到项 
	    var theForm=document.forms['queryForm'];
	    var checkedValue=theForm.selectall.checked;
		if(theForm.check&&theForm.check.length){
			for(var i=0;i<theForm.check.length;i++){
				theForm.check[i].checked=checkedValue;
			}
		}else if(theForm.check) {
			theForm.check.checked=checkedValue;
		}	
	 }
     function doChecked(thisChecked){//项选择关联到全选句柄
    	   var theForm=document.forms['queryForm'];
           if(thisChecked.checked==false){
        	   theForm.selectall.checked=false;
           }else if(theForm.selectall.checked==false){
        	  if(theForm.check){
            	 if(theForm.check.length){
	    			for(var i=0;i<theForm.check.length;i++){
	    				if(theForm.check[i].checked==false){
	        				 return ;
	    				}
	    			}
    	    	 }
    			 theForm.selectall.checked=true;
               }
         }
 	 }	
     function itemSet(){//设置默认值
    	 var options=document.getElementById('selectFilePath').options
 		 for(var  i=0;i<options.length;i++){
 			 var option=options[i];
 			if('<%=filePath %>'==option.value){
 				option.selected=true;
 				 break;
 			 }
 		 }
    }	
   </script>
   <style>
    body{font-size:9pt;}
    td{font-size:11pt;}
    input{font-size:9pt;}
   </style>
	</head>
	<body onload="itemSet()">
		<form name="queryForm" method="post">
			<input type="hidden" name="type" value="query" />
			<input type="hidden" name="addKey" value="" />
			<div class="queryPanel">
				<fieldset>
					<legend>
						文件路径
					</legend>
					<table width="100%">
						  <tr>
						    <td width="600">
								基础路径：<input name="scrBase" style="width:500" value="<%=srcBase %>"/><br>
								properties:<select id="selectFilePath" name="filePath"   style="width:500">
									<option value=""></option>
									<% 
									   for(File file:fileOptions){ 
										String selected=file.getAbsolutePath().equals(filePath)?"selected":"";
									   %>
									  <option value="<%=file.getAbsolutePath() %>"   <%=selected %> ><%=file.getAbsolutePath() %></option>
									<%} %>
								</select>
							</td>
							<td>
								<button class="btnQuery" width="80" onclick="doSubmit('query')">查询</button>&nbsp;
								<button class="btnReset" width="80" onclick="doSubmit('update')">修改</button>&nbsp;
								<button class="btnAdd" width="80" onclick="doSubmit('add')">添加</button>&nbsp;
								<button  class="btnDelete" width="80" onclick="doSubmit('del')">删除</button>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>

			<div class="gridTable">
				<table width="100%" border="0" cellspacing="0" class="gridBody">
						<tr>
							<th width="9">
								<input type="checkbox" name="selectall" onclick='doSelect();' />
							</th>
							<th width="45px">序号</th>
							<th>键</th>
							<th>值</th>
							<th>描述</th>
						</tr>
 <% if(myPro!=null) {
	 int index=0;
   for(Property pro:myPro.getList()) {%>
	<tr  class="<%=(index++ % 2 ==0 )? "odd" : "even"%>">
     <td><input  name="check" value="<%=pro.key %>" type="checkbox" onclick='doChecked(this);'/></td>
      <td ><%=index %></td>
     <td><input name="key" type="hidden" value="<%=pro.key %>"/><%=pro.key %></td>
     <td><input size="100" name="value" type="text" value="<%=pro.value %>"/></td>
     <td><textarea rows="2" cols="40"  name="comment" ><%=getComments(pro.comments) %></textarea> </td>
    </tr>	    
   <%}%>
     <tr  class="<%=(index++ % 2 ==0 )? "odd" : "even"%>">
     <td colspan="5">文件注释 :<%=getComments(myPro.comments()) %></td>
     </tr>	
  <%} %> 
 </tr>
</table>
</div>
<form>
</body>
</html>
