package common.springmvc.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;



public class DefaultExceptionHanlder implements HandlerExceptionResolver {
	
	public ModelAndView resolveException( HttpServletRequest request, HttpServletResponse response, 
            Object handler, Exception exception ) {
         
        System.out.println( "【抛出异常】--异常路径为：" + 
            request.getServletPath() + "\n【异常信息】--" +  exception.toString() ) ;
        
        //直接返回页面内容
        ModelAndView model = new ModelAndView("exception/500");
        model.addObject("servletPath", request.getServletPath());
        model.addObject("errorMessage", exception.toString());
         
        return model;
    }

}
