<html>
<body>
sid:<%=session.getId()%>
<br/>
<%session.setAttribute("time",new java.util.Date());%>
session.setAttribute("time",new java.util.Date())
<br/>
<a href="test.jsp">test attribute</a>
<a href="invalidate.jsp">test invalidate</a>
</body>
</html>
