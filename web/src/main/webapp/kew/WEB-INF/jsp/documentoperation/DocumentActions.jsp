<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<tr>
  <td><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
  <td>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" align=center>
	  <tr>
		<td class="thnormal" colspan=2 align=center height=30><strong>Document Actions</strong></td>
	  </tr>
	      <tr>
	  	    <td width="33%" align="right" class="thnormal"><input type="button" value="Flush Rule Cache" onclick="setMethodToCallAndSubmit('flushRuleCache')"></td>
	  	    <td width="66%" class="datacell">&nbsp;</td>
	  	  </tr>
		  <tr>
	  	    <td width="33%" align="right" class="thnormal"><input type="button" value="Queue Document" onclick="setMethodToCallAndSubmit('queueDocument')"></td>
	  	    <td width="66%" class="datacell">&nbsp;</td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal"><input type="button" value="Index Searchable Attributes" onclick="setMethodToCallAndSubmit('indexSearchableAttributes')"></td>
	  	    <td width="66%" class="datacell">&nbsp;</td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal"><input type="button" value="Queue Document Requeuer" onclick="setMethodToCallAndSubmit('queueDocumentRequeuer')"></td>
	  	    <td width="66%" class="datacell">&nbsp;</td>
	  	  </tr>
	  	  <tr>
		    <td width="33%" class="thnormal" align=right><input type="button" value="Queue Document Blanket Approve" onclick="setMethodToCallAndSubmit('blanketApproveDocument')"><br>
		    <input type="button" value="Queue Document Move" onclick="setMethodToCallAndSubmit('moveDocument')"></td>
		    <td width="66%" class="datacell">User: <html-el:text property="blanketApproveUser"/><br>
		    Action Taken Id: <html-el:text property="blanketApproveActionTakenId"/><br>
		    Node Names: <html-el:text property="blanketApproveNodes"/>
		    </td>
		  </tr>
		  <tr>
		    <td width="33%" class="thnormal" align=right><input type="button" value="Queue Action Invocation" onclick="setMethodToCallAndSubmit('queueActionInvocation')"></td>
		    <td width="66%" class="datacell">User: <html-el:text property="actionInvocationUser"/><br>
		    Action Item Id: <html-el:text property="actionInvocationActionItemId"/><br>
		    Action Code: <html-el:text property="actionInvocationActionCode"/>
		    </td>
		  </tr>
	  </table>
    </td>
    <td width="20" height="30">&nbsp;</td>
  </tr>
