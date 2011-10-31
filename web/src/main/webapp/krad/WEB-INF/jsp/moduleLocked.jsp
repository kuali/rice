<%@ page import="org.kuali.rice.kns.web.struts.action.KualiAction"%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<html>
<head>
 <title>Module Locked</title>
 <link href="${ConfigProperties.kr.url}/css/kuali.css" rel="stylesheet" type="text/css">
 <script type="text/javascript" src="scripts/en-common.js"></script>
</head>
<body>
 <div style="margin-top: 25px;">
   <strong><%=request.getAttribute(KualiAction.MODULE_LOCKED_MESSAGE)%></strong>
 </div>
</body>
</html>