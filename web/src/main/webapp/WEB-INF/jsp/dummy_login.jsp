<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<html>
  <head>
    <title>Login</title>
    <link href="${ConfigProperties.application.url}/${ConfigProperties.portal.css.files}" rel="stylesheet" type="text/css" />

    <style type="text/css">
        div.body {
            background-image: url("${ConfigProperties.application.url}/rice-portal/images/os-guy.gif");
            background-repeat: no-repeat;
            padding-top: 5em;
        }
        
        table#login {
            margin: auto; 
            background-color: #dfdda9; 
            border: .5em solid #fffdd8;
            /* simple rounded corners for mozilla & webkit */
            -moz-border-radius: 10px;
            -webkit-border-radius: 10px;
        }
        
        table#login th {
            height: 30 px;
            padding-top: .8em;
            padding-bottom: .8em;
            color: #a02919; 
            font-size: 2em;
        }

        #login td {
            padding: .2em;
            height: 20px;
        }

        #login .rightTd {
            padding-right: 1.2em;
        }
        
        #login .leftTd {
            padding-left: 1.2em;
        }

        table#login td#buttonRow {
            padding-top: 1em;
            padding-bottom: .6em;
        }
        
    </style>
  </head>
<body>

<form action="" method="post">

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
	                <input type="text" name="__login_user" value="admin" size="20"/>
	            </td>
            </tr>
            <c:if test="${requestScope.showPasswordField}">
            <tr>
            <td class="leftTd" width="Infinity%" align="right">
                <label>Password:&nbsp;</label>
            </td>
              <td class="rightTd" align="left"><input type="password" name="__login_pw" value="admin" size="20"/></td>
            </tr>
            </c:if>
            <c:if test="${requestScope.invalidPassword}">
            <tr>
              <td align="center" colspan="2"><strong>Invalid username or password</strong></td>
            </tr>
            </c:if>
            <tr>
              <td id="buttonRow" height="30" colspan="2" align="center"><input type="submit" value="Login"/>
              <!-- input type="image" title="Click to login." value="login" name="imageField" src="${pageContext.request.contextPath}/rice-portal/images/tinybutton-login.gif"/ -->
              </td>
            </tr>            
          </tbody>
        </table>
</div>
</form>
</body>