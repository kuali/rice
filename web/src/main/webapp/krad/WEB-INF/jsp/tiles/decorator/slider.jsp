<%--
 Copyright 2006-2007 The Kuali Foundation
 
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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="group" classname="org.kuali.rice.kns.uif.container.Group"/>

  <div style="position:absolute; width:100%; z-index:1000">
    <div style="width:250px; margin-left:auto; margin-right:20px;">
      <div class="green" style="" > 
         <tiles:insertTemplate template="/krad/WEB-INF/jsp/tiles/container/group/group.jsp">
           <tiles:putAttribute name="group" value="${group}"/>
         </tiles:insertTemplate>
      </div>
      <div class="tabs-container">
        <div id="tabs-helpsection">
          <div class="greentab" style="float:right"><a class="showgreen"><img style="margin-left:12px; margin-right:4px;" src="${ConfigProperties.krad.externalizable.images.url}down.png" width="16" height="16" alt="collapse" /> </a></div>
        </div>
        <div style="clear:both"></div>
      </div>
    </div>
  </div>
