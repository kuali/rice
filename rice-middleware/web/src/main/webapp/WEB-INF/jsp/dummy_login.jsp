<%--

    Copyright 2005-2015 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <title>Login</title>
	<c:forEach items="${fn:split(ConfigProperties.portal.css.files, ',')}" var="cssFile">
		<c:if test="${fn:length(fn:trim(cssFile)) > 0}">
	        <link href="${pageContext.request.contextPath}/${fn:trim(cssFile)}" rel="stylesheet" type="text/css" />
		</c:if>
	</c:forEach>
	<c:forEach items="${fn:split(ConfigProperties.portal.javascript.files, ',')}" var="javascriptFile">
		<c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
	        <script language="JavaScript" type="text/javascript" src="${ConfigProperties.application.url}/${fn:trim(javascriptFile)}"></script>
		</c:if>
	</c:forEach>
  </head>

<body OnLoad="document.loginForm.__login_user.focus();">

<div class="build">${ConfigProperties.version} (${ConfigProperties.datasource.ojb.platform})</div>

<form name="loginForm" action="" method="post">

<div class="body">
        <table id="login" cellspacing="0" cellpadding="0" align="center">
          <tbody>
            <tr>
              <th colspan="2">Login</th>
            </tr>
            <tr>
	            <td class="leftTd" align="right" width="Infinity%">
	                <label>Username:&nbsp;</label>
	            </td>
	            <td class="rightTd" align="left">
	                <input type="text" name="__login_user" value="" size="20"/>
	            </td>
            </tr>
            <c:set var="invalidAuthMsg" value="Invalid username" />
            <c:if test="${requestScope.showPasswordField}">
            <c:set var="invalidAuthMsg" value="Invalid username or password" />
            <tr>
            <td class="leftTd" width="Infinity%" align="right">
                <label>Password:&nbsp;</label>
            </td>
              <td class="rightTd" align="left"><input type="password" name="__login_pw" value="" size="20"/></td>
            </tr>
            </c:if>
            <c:if test="${requestScope.invalidAuth}">
            <tr>
              <td align="center" colspan="2"><strong>${invalidAuthMsg}</strong></td>
            </tr>
            </c:if>
            <tr>
              <td id="buttonRow" height="30" colspan="2"><input type="submit" value="Login" class="btn btn-primary login-button"/>
              <!-- input type="image" title="Click to login." value="login" name="imageField" src="${pageContext.request.contextPath}/rice-portal/images/tinybutton-login.gif"/ -->
              </td>
            </tr>
          </tbody>
        </table>
</div>
</form>
</body>
