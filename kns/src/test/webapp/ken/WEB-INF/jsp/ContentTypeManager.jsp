<%@ include file="/WEB-INF/jsp/Include.jsp"%>

<html>
<head>
<title>Kuali Enterprise Notification - Manage Content Types</title>
<meta name="Author" content="John Fereira">
<meta name="Keywords" content="Notification Services">
<meta name="Description" content="Notification Services">
<link href="css/notification.css" rel="stylesheet" type="text/css" />
</head>
<body>

<%@ include file="/WEB-INF/jsp/Header.jsp"%>

<div id="pagebody">
<table width="100%" border="0" align="center" cellpadding="3"
	cellspacing="0" summary="">
	<tr>
		<td colspan="2">
		
		<%@ include file="/WEB-INF/jsp/LogoutForm.jsp"%>

		</td>
	</tr>
	<tr>

		<!-- Include the Menu -->
		<%@ include file="/WEB-INF/jsp/Menu.jsp" %>

		<!-- Include the top half of the Work Area -->
		<%@ include file="/WEB-INF/jsp/WorkAreaTop.jsp" %>
      <div style="padding: 5px">
      <div class="title">Content Type Manager</div>
      <div class="para"><a class="thfont"
         href="ContentTypeForm.form">Add New Content Type</a></div>

      <table border="0" cellpadding="0" cellspacing="0" class="bord-all"
         width="60%">
         <tr>
            <td class="thnormal"><strong>Content Type Name</strong></td>
            <td class="thnormal" width="20%">&nbsp;</td>
         </tr>
         <c:forEach var="contentType" items="${contentTypes}">
            <tr>
               <td class="thnormal"><c:out
                  value="${contentType.name}" /></td>
               <td align="center" width="15%"><a
                  href="<c:url value="ContentTypeForm.form"><c:param name="name" value="${contentType.name}"/></c:url>"><img
                  src="images/buttonsmall_update.gif" border="0"
                  alt="Update" /></a></td>
            </tr>
         </c:forEach>
      </table>
      </div>
      <!-- Include the bottom half of the Work Area -->
		<%@ include file="/WEB-INF/jsp/WorkAreaBottom.jsp" %>
		
	</tr>
</table>
</div>

<%@ include file="/WEB-INF/jsp/Footer.jsp"%>

</body>
</html>