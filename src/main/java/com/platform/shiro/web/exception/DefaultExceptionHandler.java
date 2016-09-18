package com.platform.shiro.web.exception;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
* @ClassName: DefaultExceptionHandler 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author yangyw(imalex@163.com)
* @date 2015年3月20日 下午2:10:10 
* 可以理解成一个全局的异常捕获处理器
* 
*
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    /**
     * 没有权限 异常
     * <p/>
     * 后续根据不同的需求定制即�?
     */
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView processUnauthenticatedException(NativeWebRequest request, UnauthorizedException e) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("exception", e);
        mv.setViewName("unauthorized");
        return mv;
    }
    
    /**
     * 返回json格式的异常数据信息
     */
    @ExceptionHandler(SQLException.class)  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  
    @ResponseBody  
    public Object handleSQLException(HttpServletRequest request, Exception ex) {  
        String message = ex.getMessage();  
        Map<Integer,Object> resultMap=new HashMap<>();
        resultMap.put(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        return resultMap;  
    } 
    
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="IOException occured")  
    @ExceptionHandler(IOException.class)  
    @ResponseBody  
    public void handleIOException(){  
    }  
      
    @ResponseStatus(HttpStatus.BAD_REQUEST)  
    @ResponseBody  
    @ExceptionHandler(TimeoutException.class)  
    public Object signException(TimeoutException ex) {  
        return ex.getMessage();  
    } 
    
}
