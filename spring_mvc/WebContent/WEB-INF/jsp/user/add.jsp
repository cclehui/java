<%@ page language="java" contentType="text/html; charset=utf8"
    pageEncoding="utf8"%>

<%@ page language="java" import="model.po.User" %>
<%@ page language="java" import="java.util.List" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<%@ include file="header.jsp" %>

<% 
    User user = (User)request.getAttribute("user");
%>

<h3>添加用户</h3>

<div class="content">
    <form action="${basePath}/user/addUserPost.do" method="post">
        <table>
            <tr>
                <td>用户名</td>
                <td><input type="text" name="name" value="${user.name}" /></td>
            </tr>
            <tr>
                <td>生日</td>
                <td><input type="text" name="birthday" value="${user.birthday}" /></td>
            </tr>
            <tr>
                <td>性别</td>
                <td>
                    <select name="sex"> 
                        <option value="1" <%= user.getSex() == 1 ? "selected=\"selected\"" : "" %>>男</option>
                        <option value="0" <%= user.getSex() == 0 ? "selected=\"selected\"" : "" %>>女</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="提交" /></td>
            </tr>
        </table>
    </form>
</div>

<%@ include file="footer.jsp" %>