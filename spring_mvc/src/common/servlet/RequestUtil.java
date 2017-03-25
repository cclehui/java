package common.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface RequestUtil {
	
	public int getInt(String paramName , HttpServletRequest request);
	public String getString(String paramName , HttpServletRequest request);
	public Float getFloat(String paramName , HttpServletRequest request);
	public Object getRaw(String paramName , HttpServletRequest request);
	public List<Object> getList(String paramName , HttpServletRequest request);

}
