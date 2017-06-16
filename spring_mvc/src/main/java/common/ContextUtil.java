package common;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContextUtil {
	
	
	public static Object getBean(String beanName) {

		return ContextLoader.getCurrentWebApplicationContext().getBean(beanName);
		
	}
	
	
	public static SqlSession getSqlSession() {
		
		//SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)ContextUtil.getBean("sqlSessionFactory");
		
		SqlSession sqlSession = (SqlSession)ContextUtil.getBean("sqlSession");
		
		return sqlSession;
		
	}
	
}
