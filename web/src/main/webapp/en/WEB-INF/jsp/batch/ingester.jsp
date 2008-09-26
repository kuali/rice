<%@ page session="false" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib uri="../../tld/struts-html-el.tld" prefix="html-el" %>
<%@ taglib uri="../../tld/struts-bean-el.tld" prefix="bean-el" %>
<%@ taglib uri="../../tld/struts-logic-el.tld" prefix="logic-el"%>
<%@ taglib uri="../../tld/c.tld" prefix="c" %>
<%@ taglib uri="../../tld/fmt.tld" prefix="fmt" %>
<%@ taglib uri="../../tld/displaytag.tld" prefix="display-el" %>

<html>
<head>
  <title>Ingester</title>
  <link href="../kr/css/kuali.css" rel="stylesheet" type="text/css">  
</head>

<body>
<div class="headerarea-small">
<h2>Ingester</h2>
</div>

<h4>Select files to upload</h4>
<div id="lookup" align="center">
<%
    List list = (List) request.getAttribute("messages");
    if (list != null) {
%>
<ul>
<%
        Iterator it = list.iterator();
        while (it.hasNext()) {
%>
  <li><%= it.next().toString() %></li>
<%
        }
%>
</ul>
<%
    }
%>
<html-el:form method="post" action="/Ingester" enctype="multipart/form-data">
<table class="datatable-80">
  <tr><td>
    <html-el:file styleClass="infocell" name="IngesterForm" property="file[0]"/>
  </td></tr>
  <tr><td>
    <html-el:file styleClass="infocell" name="IngesterForm" property="file[1]"/>
  </td></tr>
  <tr><td>
    <html-el:file styleClass="infocell" name="IngesterForm" property="file[2]"/>
  </td></tr>
  <tr><td>
    <html-el:file styleClass="infocell" name="IngesterForm" property="file[3]"/>
  </td></tr>
  <tr><td align="center" class="infoline" >
    <html-el:submit value="Upload XML data"></html-el:submit>
  </td></tr>
</table>
</html-el:form>

</div>
</body>
</html>