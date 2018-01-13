<%--

    Copyright 2005-2018 The Kuali Foundation

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

<kul:page headerTitle="Data Integrity" lookup="true"
          transactionalDocument="false" showDocumentInfo="false"
          htmlFormAction="dataIntegrity" docTitle="Data Integrity">
    <script>
        function check() {
            document.forms[0].elements['methodToCall'].value = 'check';
            document.forms[0].submit();
        }
        function repair() {
            document.forms[0].elements['methodToCall'].value = 'repair';
            document.forms[0].submit();
        }
    </script>
    <div class="headerarea" id="headerarea">
        <h1>Data Integrity Repair Utility</h1>
    </div>
    <html-el:form action="dataIntegrity">
        <html-el:hidden property="methodToCall" value=""/>
        <kul:csrf />
        <div style="margin-left:20px">
            <p>Use the buttons below to run data integrity checks and repair on KIM delegation data.</p>
            <br/>

            <div>
                <input type="button" value="Run Data Integrity Check" onclick="check()"/>
            </div>
            <br/>
            <c:if test="${! empty checkMessages}">
                <div>
                    <ul>
                        <c:forEach var="message" items="${checkMessages}">
                            <li><c:out value="${message}" escapeXml="true"/></li>
                        </c:forEach>
                    </ul>
                </div>
                <br/>
            </c:if>
            <div>
                <input type="button" value="Run Data Repair" onclick="repair()"/>
            </div>
            <br/>
            <c:if test="${! empty repairMessages}">
                <div>
                    <ul>
                        <c:forEach var="message" items="${repairMessages}">
                            <li><c:out value="${message}" escapeXml="true"/></li>
                        </c:forEach>
                    </ul>
                </div>
                <br/>
            </c:if>
        </div>
    </html-el:form>

</kul:page>
