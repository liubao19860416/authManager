package com.platform.shiro.web.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
* @ClassName: LoginController 
* @Description: 登录
* @author yangyw(imalex@163.com)
* @date 2015年3月20日 下午2:08:48 
*
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/test/pdf")
    public String showPdfUI(HttpServletRequest req, Model model){
    	return "pdf";
    }
    
	@RequestMapping(value = "/test/getPdfStream")
    public void getPdfStream(HttpServletRequest request,HttpServletResponse response, Model model){
    	//String filepath="D:\\temp\\test.pdf";
    	String filepath = "https://pdfobject.com/pdf/sample-3pp.pdf";
		InputStream in = null;
		try {
			//in = new FileInputStream(filepath);
			in=getInputStream(filepath);
			
			ServletOutputStream out = response.getOutputStream();
			StreamUtils.copy(in, out);
		} catch ( IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    }
    
	private InputStream getInputStream(String filepath) {
		// 创建url;
		URL url = null;
		HttpURLConnection urlconn = null;
		BufferedInputStream bis = null;
		try {
			url = new URL(filepath);
			// 创建url连接;
			urlconn = (HttpURLConnection) url.openConnection();
			// 链接远程服务器;
			urlconn.connect();
			// 获取远程服务器端文件输入流;
			bis = new BufferedInputStream(urlconn.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return bis;
	}
    
    @SuppressWarnings("unused")
	private InputStream getInputStream2() {
    	String filepath = "https://pdfobject.com/pdf/sample-3pp.pdf";

    	 //创建url;
    	 URL url=null;
    	 HttpURLConnection urlconn =null;
		try {
			url = new URL(filepath);
			//创建url连接;
			urlconn = (HttpURLConnection)url.openConnection();
			//链接远程服务器;
			urlconn.connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

    	BufferedInputStream  bis = null;
//    	ServletOutputStream os =null;
    	//BufferedOutputStream   bos   =   null;
    	 try{
    	   //获取远程服务器端文件输入流;
    	   bis = new BufferedInputStream(urlconn.getInputStream() );

    	   /*os = response.getOutputStream();
    	    bos   =  new   BufferedOutputStream(response.getOutputStream());   
    	   //缓存;  
    	    byte b[]=new byte[2048];    
    	   //客户使用保存文件的对话框：   
    	   response.setHeader("Content-disposition","inline;filename="+filename);
    	   //通知客户文件的MIME类型：   
    	   response.setContentType("application/octet-stream exe;charset=utf-8");   
    	   
    	   int size;
    	   //读取文件内容到缓存;
    	   while((size=bis.read(b,0,b.length))!=-1)   
    	   {   //把文件内容写到本地文件中;
    	   //bos.write(b,0,size);   
    	    os.write(b,0,size);
    	   }*/
    	  }catch (Exception e) {
    	   e.printStackTrace();
    	  }/*finally{
    	  
    	   if(fos != null)
    	   { //关闭文件输出流;
    	    fos.close();
    	   }
    	   if(bis != null)
    	   { //关闭文件输入流;
    	    bis.close();
    	    urlconn.disconnect();
    	   }
    	  }*/
		return bis;
	}

	@RequestMapping(value = "/mall/login")
    public String showMallLoginForm(HttpServletRequest req, Model model){
    	return doLogin(req,model ,"mall/login");
    }
    
    @RequestMapping(value = "/medical/login")
    public String showMedicalLoginForm(HttpServletRequest req, Model model){
    	return doLogin(req,model,"medical/login");
    }
    
    @RequestMapping(value = "/login")
    public String showLoginForm(HttpServletRequest req, Model model){
    	return doLogin(req, model,"login");
    }
    
    @RequestMapping(value = "/mall/logout")
    public String mallLogout(HttpServletRequest req, Model model){
    	doLogout();
    	return "mall/login";
    }
    @RequestMapping(value = "/medical/logout")
    public String medicalLogout(HttpServletRequest req, Model model){
    	 doLogout();
    	 return "medical/login";
    }
    		
    private String doLogin(HttpServletRequest req, Model model,String pageName) {
        String exceptionClassName = (String)req.getAttribute("shiroLoginFailure");
        String error = null;
        if(UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if(IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "用户名/密码错误";
        } else if(exceptionClassName != null) {
            error = "其他错误：" + exceptionClassName;
        }
        model.addAttribute("error", error);
        return pageName;
    }
    
	private void doLogout(){
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {           
            subject.logout();
        }
    }


}
