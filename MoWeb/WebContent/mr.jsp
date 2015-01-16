<html>
	<head>
		<title>Make Ready User Interface</title>
	</head>

	<body>
		<h1>Make Ready</h1>
		<%	Boolean readyB = (Boolean) request.getServletContext().getAttribute("ready");
		if (readyB == null) {out.println("readyB null");}  else {out.println("readyB not null<br/>");}
		boolean ready = readyB != null && readyB; %>
		<%if (ready) {%>
        	<p>Ready!!</p>
        <%} else {%>
        	<p>Not ready...</p>
        <%}%>
	</body>
</html>