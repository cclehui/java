package test.mybatis;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import model.po.User;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserTest {

	@Test
	public void testFindUserById() throws IOException {
		
//		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(
//					new FileInputStream("SqlSessionConfig.xml")
//				);
		//SqlSession sqlSession = sqlSessionFactory.openSession();
		
		//User user = sqlSession.selectOne("mapper.UserMapper" , 1);
		
		//System.out.println(user);
//		
		
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		
		SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)applicationContext.getBean("sqlSessionFactory");
		
		SqlSession sqlSession = sqlSessionFactory.openSession();
		
		
//		
		User user = sqlSession.selectOne("model.mybatis.mapper.UserMapper.findById" , 1);
//		
		System.out.println(user);				
		sqlSession.close();
		
		
		Logger logger = Logger.getLogger("cclehui");
		
		logger.debug("xxx" + UserTest.class);
		
		
		
	}

}
