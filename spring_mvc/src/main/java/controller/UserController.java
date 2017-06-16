package controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.dao.UserDao;
import model.po.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;

import common.Pager;
import common.servlet.RequestUtil;
import common.servlet.ServletRequestUtil;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request ) throws Exception {
    	
    	int cur_page = 0;
    	if (request.getParameter("pg") != null) {
    		cur_page = Integer.parseInt(request.getParameter("pg"));
    	}
    	
    	int page_size = 2;
    	int start = (cur_page - 1) * page_size;
    	start = start > 0 ? start : 0;
        
        UserDao userDao = new UserDao();
        List<User> userList = userDao.getUserList(start, page_size);
        
        int totalUser = userDao.getUserListCount();
        
        Pager pager = new Pager();
        pager.setTotalCount(totalUser);
        pager.setPageRowCount(page_size);
        pager.setCurPage(cur_page);
        String pagerHtml = pager.getToolBar("/springmvc/user/index.do");
        
        
        
        
        ModelAndView modelAndView = new ModelAndView("user/index");
        
        modelAndView.addObject("userList", userList);
        modelAndView.addObject("pagerHtml", pagerHtml);
        modelAndView.addObject("action", "index");
        
        return modelAndView;
    }
    
    @RequestMapping(value = "addUser")
    public String addUser(HttpServletRequest request) {
    	
    	RequestUtil reqeustUtil = new ServletRequestUtil();
    	
    	int userId = reqeustUtil.getInt("uid", request);
    	
    	User user = new User();
    	
    	
    	if (userId > 0) {
    		UserDao userDao = new UserDao();
    		user = userDao.getUserById(userId);
    	}
    	
    	request.setAttribute("user", user);
    	request.setAttribute("action", "add");
    	
    	return "user/add";
    }
    
    @RequestMapping(value = "addUserPost")
    public String addUserPost(@RequestParam("name") String name , @RequestParam("birthday") String birthday ,@RequestParam("sex") int sex){
    	
    	
    	User user = new User();
    	user.setName(name);
    	user.setBirthday(birthday);
    	user.setSex(sex);

    	UserDao userDao = new UserDao();
    	int userId = userDao.insertUser(user);
    	
    	
    	
    	return "redirect:/user/index.do?uid=" + userId;
    }
    
    @RequestMapping(value = "deleteUser")
    public String deleteUser(HttpServletRequest request) {
    	
    	RequestUtil reqeustUtil = new ServletRequestUtil();
    	
    	int userId = reqeustUtil.getInt("uid", request);
    	
    	if (userId > 0) {
    		UserDao userDao = new UserDao();
    		int result = userDao.deleteUser(userId);
    	}
    	
    	request.setAttribute("action", "index");
    	
    	return "redirect:/user/index.do";
    }
    
    
    
}
