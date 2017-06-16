package model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import common.ContextUtil;
import model.po.User;

public class UserDao {
    
	
	public User getUserById(int userId) {
		SqlSession sqlSession = ContextUtil.getSqlSession();
		
		User user = sqlSession.selectOne("model.mybatis.mapper.UserMapper.findById", userId);
		
		return user;
	}
	
	
	public int getUserListCount() {
		
		SqlSession sqlSession = ContextUtil.getSqlSession();
		
		int userListCount = sqlSession.selectOne("model.mybatis.mapper.UserMapper.getUserListCount");
		
		return userListCount;
	}
    
    
    public List<User> getUserList(int offset , int limit){
        
    	
    	SqlSession sqlSession = ContextUtil.getSqlSession();
    	
    	
    	Map<String, Object> selectParams = new HashMap<String, Object>();
    	selectParams.put("start", offset);
    	selectParams.put("limit", limit);
    	
        List<User> userList = sqlSession.selectList("model.mybatis.mapper.UserMapper.getUserList", selectParams);
        
        return userList;
    }
    
    
    public int insertUser(User user){
    	
    	SqlSession sqlSession = ContextUtil.getSqlSession();
    	
    	int result = 0;
    	
    	if (sqlSession != null) {
    		
    		sqlSession.insert("model.mybatis.mapper.UserMapper.insertUser", user);
    		
    		result = user.getId();
    		
    		
    	}
    	
    	return result;
    }
    
    public int deleteUser(int id) {
    	SqlSession sqlSession = ContextUtil.getSqlSession();
    	
    	return sqlSession.delete("model.mybatis.mapper.UserMapper.deleteUser", id);
    }

}
