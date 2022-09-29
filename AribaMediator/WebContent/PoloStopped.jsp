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
<TITLE>Mail Router Stopped</TITLE>
<SCRIPT language="Javascript">
	function ControlMailRouter()	{
<!--		alert("Enable Mail Router Selected"); -->
		document.RouterStopped.submit();
	}
</SCRIPT>
</HEAD>
<BODY>
<FORM action="/Polo/SenderServlet" method="post" enctype="text/plain" name="RouterStopped">
<CENTER>
<H3>Go Back to control Mail Router</H3>
<INPUT type="image" name="MailRouterControl" src="images/Signal_Light_-_Red.jpg"
	alt="Control Mail Router">
</CENTER>
</FORM>
</BODY>
</HTML>
