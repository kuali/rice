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
