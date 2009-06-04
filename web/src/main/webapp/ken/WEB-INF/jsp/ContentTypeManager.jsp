<%@ include file="Include.jsp"%>

<html>
<head>
<title>Kuali Enterprise Notification - Manage Content Types</title>
<meta name="Author" content="John Fereira">
<meta name="Keywords" content="Notification Services">
<meta name="Description" content="Notification Services">
<link href="css/notification.css" rel="stylesheet" type="text/css" />
</head>
<body>

<div id="pagebody">
      <div style="padding: 5px">
      <div class="title">Content Type Manager</div>
      <div class="para"><a href="ContentTypeForm.form">Add New Content Type</a></div>

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
</div>

</body>
</html>