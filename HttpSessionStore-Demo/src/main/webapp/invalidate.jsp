<html>
<body>
sid:<%=session.getId()%>
<br/>
invalidate<%request.getSession().invalidate();%>
<br/>
sid:<%=session.getId()%>
<br/>
time:<%=session.getAttribute("time")%>
</body>
</html>
