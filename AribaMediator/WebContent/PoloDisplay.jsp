<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/Master.css" rel="stylesheet"
	type="text/css">
<TITLE>Mail Router Display</TITLE>
<body style="background-image: url(images/bg.jpg); background-repeat: repeat-x; margin: 0px;">

<SCRIPT language="Javascript">
	function EnableMailRouter()	{
<!--		alert("Enable Mail Router Selected"); -->
		document.PoloMailControl.PoloMailController.value = "EnableInvoiceController";
<!--		alert("Enable Mail Router Selected RouterController Value =" + document.MailRouterControl.RouterControler.value);	 -->
<!--		document.MailRouterControl.submit();  -->
	}
	function DisableMailRouter()	{
<!--		alert("Disable Mail Router Selected");	 -->
		document.PoloMailControl.PoloMailController.value = "DisableInvoiceController";
<!--		alert("Disable Mail Router Selected RouterController Value =" + document.MailRouterControl.RouterControler.value);	-->
<!--		document.PoloMailControl.submit(); -->
}	
</SCRIPT>
</HEAD>
<BODY >
<FORM action="/Polo/SenderServlet" method="post" name="PoloMailControl">
<br>
<center><img src="images/top.jpg" WIDTH=158 HEIGHT=43></img></center>
<br>
<br>
<CENTER><H3>Mail Router Control Panel</H3>
<P>.<INPUT type="image" name="StartMailRouter" src="images/startmail.jpg" alt="Start Mail" onclick="EnableMailRouter()">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<INPUT type="image" name="Stop" src="images/stopmail.jpg" alt="Stop Mail Router" onclick="DisableMailRouter()">
	<INPUT type="hidden" name="PoloMailController" size="20"></P>
	
	<H3>Current Mail Router Status</H3>
	<%	session = request.getSession(false);    
		String currState = (String)session.getAttribute("CurrentState");

	if ((currState == null) || (currState.equalsIgnoreCase("Stopped"))) { %><IMG
	border="0" src="images/stopped.jpg" width="140" height="140">

	
	
	<% } else {%><IMG border="0" src="images/running.jpg" width="140"
	height="140">
	
	
	<% } %>
	</CENTER>
	
	

</FORM>
</BODY>
</HTML>
