package common.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class ServletRequestUtil implements RequestUtil{

	@Override
	public int getInt(String paramName, HttpServletRequest request) {
		
		String paramValue = request.getParameter(paramName);
    	
    	int result = paramValue == null ? 0 : Integer.parseInt(paramValue);
		
		return result;
	}

	@Override
	public String getString(String paramName, HttpServletRequest request) {
		String paramValue = request.getParameter(paramName);
		return paramValue;
	}

	@Override
	public Float getFloat(String paramName, HttpServletRequest request) {
		String paramValue = request.getParameter(paramName);
    	
    	float result = paramValue == null ? 0 : Float.parseFloat(paramValue);
		
		return result;
	}

	@Override
	public Object getRaw(String paramName, HttpServletRequest request) {
		String paramValue = request.getParameter(paramName);
    	
		
		return paramValue;
	}

	@Override
	public List<Object> getList(String paramName, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
