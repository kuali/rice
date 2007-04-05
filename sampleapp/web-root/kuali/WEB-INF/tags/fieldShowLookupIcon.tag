<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="isReadOnly" required="true"
              description="Is the view for this field readOnly?" %>
<%@ attribute name="field" required="true" type="org.kuali.core.web.ui.Field"
              description="The field for which to show the lookup icon." %>

<c:if test="${isReadOnly ne true && field.fieldType ne field.KUALIUSER && field.fieldType ne field.HIDDEN}">
	
    <c:if test="${!(empty field.quickFinderClassNameImpl)}">
        
        <kul:lookup
        	boClassName="${field.quickFinderClassNameImpl}"
            fieldConversions="${field.fieldConversions}"
            lookupParameters="${field.lookupParameters}"
            fieldLabel="${field.fieldLabel}"
            referencesToRefresh="${field.referencesToRefresh}" />
                
    </c:if>

</c:if>
