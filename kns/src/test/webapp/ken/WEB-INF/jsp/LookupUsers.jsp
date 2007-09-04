<%@ include file="/WEB-INF/jsp/Include.jsp" %>
<html>
<head>
<title>Kuali Enterprise Notification - Search</title>
<meta name="Author" content="John Fereira">
<meta name="Author" content="Aaron Godert">
<link href="css/notification.css" rel="stylesheet" type="text/css" />
</head>
<body>
 
<%@ include file="/WEB-INF/jsp/Header.jsp" %>

<div id="pagebody">
<table width="100%" border="0" align="center" cellpadding="3" cellspacing="0"  summary="">
<tr>
	<td colspan="2">

		<%@ include file="/WEB-INF/jsp/LogoutForm.jsp" %>
		
	</td>
</tr>
<tr>

<!-- Include the Menu -->
<%@ include file="/WEB-INF/jsp/Menu.jsp" %>

<!-- Include the top half of the Work Area -->
<%@ include file="/WEB-INF/jsp/WorkAreaTop.jsp" %>

<iframe name="iframe_51148" id="iframe_51148" src="en/Lookup.do?lookupableImplServiceName=UserLookupableImplService" frameborder="0" scrolling="auto" width="100%" height="100%">
</iframe>

<!-- Include the bottom half of the Work Area -->
<%@ include file="/WEB-INF/jsp/WorkAreaBottom.jsp" %>

</tr>
</table>
</div> <!-- end pagebody -->

<%@ include file="/WEB-INF/jsp/Footer.jsp" %>

</body>
</html>
