<!--
/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2017
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
-->
<!DOCTYPE html>
<%
    response.setHeader("Cache-Control","no-cache"); 
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<%@ include file="header.jsp" %>

<body class="<%=bodyClasses%>" style="width: 100%; height: 100%; position: absolute;">
	<div id="ECMWebUIloadingAnimation">
	 	<div style="position: absolute; top: 40%; text-align: center; width: 100%;">
			<div id="ECMWebUIloadingAnimationImage" class="ecmLoadingApp">
				<svg class="ecmLoader" viewBox="25 25 50 50">
					<circle class="ecmLoader__path" cx="50" cy="50" r="20" />
				</svg>
			</div>
			<div id="ECMWebUIloadingText" class="contentNode loadingText"></div>
		</div>
	</div>
    <div dojoType="ecm.widget.process.StepProcessorLayout" id="ECMStepProcUI" style="width: 100%; height: 100%"></div>
</body>

</html>
