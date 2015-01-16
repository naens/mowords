<%@page import="java.util.Calendar"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <%@taglib uri="/struts-tags" prefix="s"%>
  <%@ page contentType="text/html; charset=UTF-8"%>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>MoWords</title>
	</head>
	<body>
		<p>MoWords Login page</p>
		<p>User is <s:property value="%{user}"/></p>
		<s:if test="user == null">
			<p><a href="login">Login</a></p>
			<p><a href="testlogin">Test login</a></p>
		</s:if>
      	<s:else>
			<p><a href="topics.html">topics</a></p>
			<p><a href="logout">logout</a></p>
		</s:else>
		<p><a href="initialize_database">initialize database</a>
	</body>
</html>
