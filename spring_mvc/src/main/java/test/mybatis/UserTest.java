package test.mybatis;

//import static org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.mybatis.mapper.UserMapper;
import model.po.User;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
//import org.junit.*;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserTest {

	public static void main(String[] args) throws IOException {
		System.out.println("aaaaaaaaaa");

		UserTest userTest = new UserTest();

		userTest.testFindUserById();

	}

	public void testFindUserById() throws IOException {

//		IOExceptionSqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(
//					new FileInputStream("SqlSessionConfig.xml")
//				);
//		SqlSession sqlSession = sqlSessionFactory.openSession();
//
//		User user = sqlSession.selectOne("mapper.UserMapper" , 1);
//
//		System.out.println(user);
//		


		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("SpringApplicationContext.xml");

		//SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)applicationContext.getBean("sqlSessionFactory");

//		SqlSession sqlSession = sqlSessionFactory.openSession();

		SqlSession sqlSession = (SqlSession) applicationContext.getBean("sqlSession");

//		UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//		User user = userMapper.findById(1);
//
//		System.out.println(user);
//
//        System.out.println("xxxxxxxxxxxxxxx");
//
//        user = sqlSession.selectOne("model.mybatis.mapper.UserMapper.findById" , 1);
//        System.out.println(user);

        System.out.println("yyyyyyyyyyyy");

        Map<String, Object> selectParmas = new HashMap<String, Object>();
        selectParmas.put("start", 0);
        selectParmas.put("limit", 2);

        List<User> userList = sqlSession.selectList("model.mybatis.mapper.UserMapper.getUserList", selectParmas);

        System.out.println(userList);

        System.out.println("zzzzzzzzzzzz");


	}

}
